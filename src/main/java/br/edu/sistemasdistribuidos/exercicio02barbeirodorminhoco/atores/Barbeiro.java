package br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.atores;

import br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.monitor.BarbeariaMonitor;
import br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.monitor.BarbeariaMonitor.Atendimento;

import java.util.concurrent.ThreadLocalRandom;

/** Eu represento a thread do barbeiro, que dorme aguardando clientes e executa os cortes. */
public final class Barbeiro extends Thread {
    private final BarbeariaMonitor barbearia;

    public Barbeiro(BarbeariaMonitor barbearia) {
        super("Barbeiro");
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        try {
            Atendimento atendimento;
            // Eu atendo a fila até o monitor indicar encerramento e não haver mais clientes.
            while ((atendimento = barbearia.aguardarProximoCliente()) != null) {
                System.out.printf("O barbeiro começou o corte de %s.%n",
                        atendimento.getCliente());
                Thread.sleep(ThreadLocalRandom.current().nextLong(400, 1001));
                // Eu aviso ao monitor que este corte terminou para liberar o cliente correspondente.
                barbearia.concluirCorte(atendimento);
            }
        } catch (InterruptedException e) {
            // Eu restauro o sinal de interrupção recebido durante uma espera ou um corte.
            interrupt();
        }
    }
}
