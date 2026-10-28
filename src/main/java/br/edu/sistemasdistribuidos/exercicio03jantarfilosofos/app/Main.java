package br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.app;

import br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.atores.Filosofo;
import br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.recursos.Mesa;

public final class Main {
    private static final long DURACAO_PADRAO_SEGUNDOS = 15;

    private Main() {
    }

    public static void main(String[] args) throws InterruptedException {
        long duracaoSegundos = args.length == 0
                ? DURACAO_PADRAO_SEGUNDOS
                : Long.parseLong(args[0]);
        if (duracaoSegundos <= 0) {
            throw new IllegalArgumentException("A duração deve ser maior que zero.");
        }

        Mesa mesa = new Mesa();
        Filosofo[] filosofos = new Filosofo[Mesa.QUANTIDADE_FILOSOFOS];
        for (int id = 0; id < filosofos.length; id++) {
            filosofos[id] = new Filosofo(id, mesa);
            filosofos[id].start();
        }

        try {
            Thread.sleep(duracaoSegundos * 1000);
        } finally {
            for (Filosofo filosofo : filosofos) {
                filosofo.interrupt();
            }

            boolean interrompida = false;
            for (Filosofo filosofo : filosofos) {
                while (filosofo.isAlive()) {
                    try {
                        filosofo.join();
                    } catch (InterruptedException e) {
                        interrompida = true;
                        for (Filosofo thread : filosofos) {
                            thread.interrupt();
                        }
                    }
                }
            }
            if (interrompida) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
