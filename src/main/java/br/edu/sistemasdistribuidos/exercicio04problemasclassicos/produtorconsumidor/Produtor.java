package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor;

public class Produtor implements Runnable {
    private final Buffer buffer;
    private final String identificador;
    private final int quantidade;

    public Produtor(Buffer buffer, String identificador, int quantidade) {
        if (buffer == null || identificador == null || quantidade < 0) {
            throw new IllegalArgumentException("Parâmetros inválidos para o produtor.");
        }
        this.buffer = buffer;
        this.identificador = identificador;
        this.quantidade = quantidade;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= quantidade; i++) {
                Produto produto = new Produto(identificador + "-item-" + i);
                buffer.inserir(produto);
                System.out.printf("[%s] Produziu %s.%n",
                        Thread.currentThread().getName(), produto.getValor());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
