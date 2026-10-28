package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor;

public final class Produto {
    private static final Produto ENCERRAMENTO = new Produto("fim", true);

    private final String valor;
    private final boolean encerramento;

    public Produto(String valor) {
        this(valor, false);
    }

    private Produto(String valor, boolean encerramento) {
        if (valor == null) {
            throw new IllegalArgumentException("O valor do produto não pode ser nulo.");
        }
        this.valor = valor;
        this.encerramento = encerramento;
    }

    public static Produto sinalDeEncerramento() {
        return ENCERRAMENTO;
    }

    public String getValor() {
        return valor;
    }

    public boolean isEncerramento() {
        return encerramento;
    }
}
