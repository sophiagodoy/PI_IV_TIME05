package br.com.ibm.intelimed.network;

public class PedidoIdentificacao extends Pedido {
    private String uidUsuario;
    private String uidContato;

    public PedidoIdentificacao(String uidUsuario, String uidContato) {
        this.uidUsuario = uidUsuario;
        this.uidContato = uidContato;
    }

    public String getUidUsuario() { return uidUsuario; }
    public String getUidContato() { return uidContato; }
}