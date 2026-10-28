package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor;

public class Consumidor implements Runnable {
    private final Buffer buffer;
    private final String identificador;

    public Consumidor(Buffer buffer, String identificador) {
        if (buffer == null || identificador == null) {
            throw new IllegalArgumentException("Parâmetros inválidos para o consumidor.");
        }
        this.buffer = buffer;
        this.identificador = identificador;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Produto produto = buffer.remover();
                if (produto.isEncerramento()) {
                    System.out.printf("[%s] Recebeu sinal de encerramento.%n",
                            Thread.currentThread().getName());
                    return;
                }
                System.out.printf("[%s] Consumidor %s consumiu %s.%n",
                        Thread.currentThread().getName(), identificador, produto.getValor());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
