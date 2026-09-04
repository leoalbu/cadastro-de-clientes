package com.leandro.cadastro.model;

/** Objeto de valor com os dados de endereco de um cliente. */
public class Endereco {

    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    public Endereco() {
    }

    public Endereco(String logradouro, String numero, String bairro,
                    String cidade, String uf, String cep) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    /** Retorna "Cidade/UF" ou string vazia se nao houver cidade. */
    public String getCidadeUf() {
        if (cidade == null || cidade.isBlank()) {
            return "";
        }
        return uf == null || uf.isBlank() ? cidade : cidade + "/" + uf;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (logradouro != null && !logradouro.isBlank()) {
            sb.append(logradouro);
            if (numero != null && !numero.isBlank()) {
                sb.append(", ").append(numero);
            }
        }
        if (bairro != null && !bairro.isBlank()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(bairro);
        }
        String cidadeUf = getCidadeUf();
        if (!cidadeUf.isBlank()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(cidadeUf);
        }
        return sb.toString();
    }
}
