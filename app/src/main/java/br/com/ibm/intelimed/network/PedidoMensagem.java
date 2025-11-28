package br.com.ibm.intelimed.network;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class PedidoMensagem extends Pedido {
    private String uidRemetente;
    private String conteudoCriptografado;
    private String uidDestinatario;
    private String chaveBase64;
    private long timestamp;

    public PedidoMensagem(String uidRemetente, String conteudo, String uidDestinatario) throws Exception {
        this.uidRemetente = uidRemetente;
        this.uidDestinatario = uidDestinatario;


        try {
            SecretKey chave = gerarChaveAleatoria();
            this.chaveBase64 = Base64.getEncoder().encodeToString(chave.getEncoded());
            this.conteudoCriptografado = criptografar(conteudo, chave);
        } catch (Exception erro) {
            throw new Exception("Erro na criptografia da mensagem" + erro);
        }
    }

    public PedidoMensagem(String uidRemetente, String conteudo, String uidDestinatario, String chaveBase64, long timestamp) throws Exception {
        this.uidRemetente = uidRemetente;
        this.uidDestinatario = uidDestinatario;
        this.conteudoCriptografado = conteudo;
        this.chaveBase64 = chaveBase64;
        this.timestamp = timestamp;
    }

    public String getUidRemetente() {
        return this.uidRemetente;
    }

    public String getConteudoCriptografado() {
        return this.conteudoCriptografado;
    }

    public String getUidDestinatario() {
        return this.uidDestinatario;
    }

    public String getConteudo() {
        try {
            SecretKey chave = restaurarChave(this.chaveBase64);
            return descriptografar(this.conteudoCriptografado, chave);
        } catch (Exception e) {
            return "[Erro ao descriptografar mensagem]";
        }
    }

    public String getChaveBase64() {
        return this.chaveBase64;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private SecretKey gerarChaveAleatoria() throws Exception {
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance("AES");
            generator.init(128); // ou 256 se quiser mais segurança
        } catch (Exception erro) {
            throw new Exception("Erro ao gerar chave" + erro);
        }

        return generator.generateKey();

    }

    private SecretKey restaurarChave(String chaveBase64) {
        byte[] decodedKey = Base64.getDecoder().decode(chaveBase64);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    private String criptografar(String texto, SecretKey chave) throws Exception {
        byte[] bytesCriptografados;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, chave);
            bytesCriptografados = cipher.doFinal(texto.getBytes());
        } catch (Exception erro) {
            throw new Exception("Erro na criptografia" + erro);
        }

        return Base64.getEncoder().encodeToString(bytesCriptografados);
    }

    private String descriptografar(String textoCriptografado, SecretKey chave) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, chave);
        byte[] bytesDecodificados = Base64.getDecoder().decode(textoCriptografado);
        byte[] bytesDescriptografados = cipher.doFinal(bytesDecodificados);
        return new String(bytesDescriptografados);
    }
}