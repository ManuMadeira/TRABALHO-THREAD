package br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.atores;

import br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.recursos.Mesa;

import java.util.concurrent.ThreadLocalRandom;

public class Filosofo extends Thread {
    private static final long TEMPO_MINIMO_MS = 500;
    private static final long TEMPO_VARIACAO_MS = 1500;

    private final int id;
    private final Mesa mesa;

    public Filosofo(int id, Mesa mesa) {
        super("Filosofo-" + id);
        this.id = id;
        this.mesa = mesa;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                meditar();
                mesa.solicitarPalitos(id);
                try {
                    Thread.sleep(tempoAleatorio());
                } finally {
                    mesa.devolverPalitos(id);
                }
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }

    private void meditar() throws InterruptedException {
        System.out.printf("Filósofo %d está pensando...%n", id);
        Thread.sleep(tempoAleatorio());
    }

    private long tempoAleatorio() {
        return ThreadLocalRandom.current()
                .nextLong(TEMPO_MINIMO_MS, TEMPO_MINIMO_MS + TEMPO_VARIACAO_MS);
    }
}
