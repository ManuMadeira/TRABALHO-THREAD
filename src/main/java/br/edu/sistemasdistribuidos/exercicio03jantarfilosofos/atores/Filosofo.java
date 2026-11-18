package br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.atores;

import br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.recursos.Mesa;

import java.util.concurrent.ThreadLocalRandom;

/** Esta classe representa um filósofo que alterna entre pensar e tentar comer. */
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
            // O ciclo repete o ciclo enquanto ninguém solicitar que esta thread pare.
            while (!isInterrupted()) {
                meditar();
                mesa.solicitarPalitos(id);
                try {
                    Thread.sleep(tempoAleatorio());
                } finally {
                    // O código devolve os dois palitos mesmo se a espera para comer for interrompida.
                    mesa.devolverPalitos(id);
                }
            }
        } catch (InterruptedException e) {
            // O código restaura o sinal para que o ciclo e quem me iniciou reconheçam a interrupção.
            interrupt();
        }
    }

    private void meditar() throws InterruptedException {
        // O código simula o tempo de pensamento antes de solicitar os recursos compartilhados.
        System.out.printf("Filósofo %d está pensando...%n", id);
        Thread.sleep(tempoAleatorio());
    }

    private long tempoAleatorio() {
        return ThreadLocalRandom.current()
                .nextLong(TEMPO_MINIMO_MS, TEMPO_MINIMO_MS + TEMPO_VARIACAO_MS);
    }
}
