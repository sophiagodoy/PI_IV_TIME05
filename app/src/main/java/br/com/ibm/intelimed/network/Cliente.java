package br.com.ibm.intelimed.network;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

public class Cliente {

    private final String hostPadrao = "10.0.2.2";
    private final int portaPadrao = 3000;

    private Parceiro servidor;
    private String uid;
    private String uidOutro;
    private Thread threadReceberMensagens;
    private OnNovaMensagemListener listener;
    private volatile boolean conectado = false;

    public void setListener(OnNovaMensagemListener listener) {
        this.listener = listener;
    }

    // Conectar ao servidor
    public synchronized void conectarServidor(String h, int p, String uid, String uidOutro) throws Exception {
        resetarCliente(); // garante que qualquer conexão antiga seja fechada

        this.uid = uid;
        this.uidOutro = uidOutro;

        String host = (h != null) ? h : hostPadrao;
        int porta = (p > 0) ? p : portaPadrao;

        Socket conexao = new Socket(host, porta);
        ObjectOutputStream transmissor = new ObjectOutputStream(conexao.getOutputStream());
        transmissor.flush();
        ObjectInputStream receptor = new ObjectInputStream(conexao.getInputStream());

        servidor = new Parceiro(conexao, receptor, transmissor);
        servidor.recebeUmPedido(new PedidoIdentificacao(uid, uidOutro));

        conectado = true;
        System.out.println("✅ Conectado ao servidor");

        // iniciar recebimento
        receberMensagens();
    }

    // Envio de mensagens
    public synchronized void enviarMensagem(String conteudo) throws Exception {
        if (!conectado) throw new Exception("Cliente não conectado.");
        PedidoMensagem msg = new PedidoMensagem(uid, conteudo, uidOutro);

        try {
            servidor.recebeUmPedido(msg);
        } catch (EOFException eof) {
            System.out.println("⚠ Socket fechado pelo servidor durante envio. Reconecte ou trate.");
            conectado = false;
            throw eof; // opcional: propaga a exceção para o ViewModel
        } catch (IOException e) {
            System.out.println("❌ Erro inesperado durante envio: " + e.getMessage());
            conectado = false;
            throw e;
        }
    }

    // Recebimento em loop
    private void receberMensagens() {
        if (threadReceberMensagens != null && threadReceberMensagens.isAlive()) return;

        threadReceberMensagens = new Thread(() -> {
            try {
                while (conectado) {
                    Pedido pedido = servidor.enviarUmPedido(); // bloqueante
                    if (pedido == null) {
                        System.out.println("⚠ Socket fechado pelo servidor.");
                        conectado = false;
                        break;
                    }

                    if (pedido instanceof PedidoMensagem && listener != null) {
                        listener.aoChegarMensagem((PedidoMensagem) pedido);
                    }
                }
            } catch (EOFException eof) {
                System.out.println("⚠ EOFException detectada, socket fechado.");
                conectado = false;
            } catch (Exception e) {
                System.out.println("❌ Erro inesperado: " + e.getMessage());
                conectado = false;
            }
        });

        threadReceberMensagens.setDaemon(true);
        threadReceberMensagens.start();
    }

    // Fechar conexão
    public synchronized void fecharConexao() {
        conectado = false;

        try {
            if (servidor != null) {
                servidor.recebeUmPedido(new PedidoDeSaida());
                servidor.fecharConexao(); // fecha socket + streams
            }
        } catch (Exception ignored) {}

        servidor = null;

        if (threadReceberMensagens != null) {
            threadReceberMensagens.interrupt();
            threadReceberMensagens = null;
        }
    }

    // Resetar cliente (para troca de conta)
    public synchronized void resetarCliente() {
        fecharConexao();
        uid = null;
        uidOutro = null;
    }

    // Método Mongo (igual)
    public List<Map<String, Object>> usarMongo(PedidoDeUsoMongo pedido) throws Exception {
        if (!conectado) throw new Exception("Cliente não conectado.");

        servidor.recebeUmPedido(pedido);

        if ("find".equalsIgnoreCase(pedido.getTipoPedido())) {
            Pedido retorno = servidor.enviarUmPedido();
            if (retorno instanceof RetornarDados) {
                RetornarDados resultado = (RetornarDados) retorno;
                return resultado.getDados();
            }
        }
        return null;
    }
}
