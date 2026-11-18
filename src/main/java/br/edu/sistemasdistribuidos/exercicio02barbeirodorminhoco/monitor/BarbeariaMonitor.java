package br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.monitor;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A classe protege a fila, a capacidade da sala de espera e os estados do atendimento
 * com synchronized; wait/notifyAll coordenam clientes e barbeiro sem busy waiting.
 */
public final class BarbeariaMonitor {
    private final Queue<Atendimento> fila = new ArrayDeque<>();
    private final int cadeirasDeEspera;
    private boolean encerrada;
    private boolean barbeiroDormindo;

    public BarbeariaMonitor(int cadeirasDeEspera) {
        if (cadeirasDeEspera <= 0) {
            throw new IllegalArgumentException("A barbearia deve ter ao menos uma cadeira de espera.");
        }
        this.cadeirasDeEspera = cadeirasDeEspera;
    }

    public synchronized boolean solicitarCorte(String cliente) throws InterruptedException {
        // O método recusa a chegada se a barbearia fechou ou se todas as cadeiras de espera estão ocupadas.
        if (encerrada || fila.size() >= cadeirasDeEspera) {
            System.out.printf("%s foi embora: não há cadeira de espera disponível.%n", cliente);
            return false;
        }

        Atendimento atendimento = new Atendimento(cliente);
        // O código adiciona o cliente no fim da fila FIFO para preservar a ordem de chegada.
        fila.add(atendimento);
        System.out.printf("%s sentou na sala de espera (%d/%d cadeira(s) ocupada(s)).%n",
                cliente, fila.size(), cadeirasDeEspera);
        if (barbeiroDormindo) {
            System.out.printf("%s acordou o barbeiro.%n", cliente);
            barbeiroDormindo = false;
        }
        // O código acorda o barbeiro e qualquer cliente que precise reavaliar sua condição de espera.
        notifyAll();

        // O código espera a conclusão do próprio atendimento; wait libera o monitor
        // para o barbeiro trabalhar e outros clientes alterarem o estado.
        while (!atendimento.concluido) {
            wait();
        }
        return true;
    }

    public synchronized Atendimento aguardarProximoCliente() throws InterruptedException {
        // O método mantém o barbeiro aguardando quando a fila está vazia e a barbearia segue aberta.
        while (fila.isEmpty() && !encerrada) {
            if (!barbeiroDormindo) {
                System.out.println("O barbeiro está dormindo: não há clientes.");
                barbeiroDormindo = true;
            }
            wait();
        }
        // O código usa null para informar que a barbearia encerrou e não há mais clientes na fila.
        if (fila.isEmpty()) {
            return null;
        }

        // O código retira sempre o próximo da fila FIFO dentro da seção crítica.
        Atendimento atendimento = fila.remove();
        barbeiroDormindo = false;
        System.out.printf("O barbeiro chamou %s. Cadeiras de espera ocupadas: %d/%d.%n",
                atendimento.cliente, fila.size(), cadeirasDeEspera);
        return atendimento;
    }

    public synchronized void concluirCorte(Atendimento atendimento) {
        // O código altera o estado protegido e acorda quem espera pela conclusão do corte.
        atendimento.concluido = true;
        System.out.printf("O barbeiro terminou o corte de %s.%n", atendimento.cliente);
        notifyAll();
    }

    public synchronized void encerrar() {
        // O código marca o fechamento e acorda o barbeiro caso ele esteja esperando por clientes.
        encerrada = true;
        notifyAll();
    }

    /** Esta classe guarda o cliente e o estado compartilhado de conclusão de um corte. */
    public static final class Atendimento {
        private final String cliente;
        private boolean concluido;

        private Atendimento(String cliente) {
            this.cliente = cliente;
        }

        public String getCliente() {
            return cliente;
        }
    }
}
