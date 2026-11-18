package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor;

/** Esta classe modela uma thread que retira produtos do buffer e os consome. */
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
            // O consumo continua consumindo até receber o produto especial de encerramento.
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
            // O código preserva o sinal para que quem coordena as threads saiba da interrupção.
            Thread.currentThread().interrupt();
        }
    }
}
