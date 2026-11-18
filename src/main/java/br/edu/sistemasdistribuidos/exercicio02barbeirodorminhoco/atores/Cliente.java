package br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.atores;

import br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.monitor.BarbeariaMonitor;

import java.util.concurrent.ThreadLocalRandom;

/** Esta classe representa uma thread cliente que tenta ocupar uma cadeira e pedir um corte. */
public final class Cliente extends Thread {
    private final BarbeariaMonitor barbearia;

    public Cliente(int id, BarbeariaMonitor barbearia) {
        super("Cliente-" + id);
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        try {
            // O código simula a chegada e solicito atendimento; se não houver vaga, saio da barbearia.
            Thread.sleep(ThreadLocalRandom.current().nextLong(100, 701));
            if (barbearia.solicitarCorte(getName())) {
                System.out.printf("%s saiu da barbearia após o corte.%n", getName());
            }
        } catch (InterruptedException e) {
            // O código preserva o sinal para permitir o encerramento cooperativo da thread.
            interrupt();
        }
    }
}
