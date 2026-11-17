package br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.app;

import br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.atores.Barbeiro;
import br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.atores.Cliente;
import br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.monitor.BarbeariaMonitor;

import java.util.ArrayList;
import java.util.List;

/** Eu configuro a barbearia e coordeno o ciclo de vida das threads do exercício. */
public final class BarbeiroDorminhoco {
    private static final int CADEIRAS_PADRAO = 3;
    private static final int CLIENTES_PADRAO = 10;

    private BarbeiroDorminhoco() {
    }

    /** Eu inicio o barbeiro e os clientes, aguardo os cortes e encerro as threads. */
    public static void main(String[] args) throws InterruptedException {
        if (args.length > 2) {
            throw new IllegalArgumentException(
                    "Use: BarbeiroDorminhoco [cadeiras-de-espera] [clientes]");
        }
        int cadeiras = argumento(args, 0, CADEIRAS_PADRAO, "cadeiras");
        int quantidadeClientes = argumento(args, 1, CLIENTES_PADRAO, "clientes");

        BarbeariaMonitor barbearia = new BarbeariaMonitor(cadeiras);
        Barbeiro barbeiro = new Barbeiro(barbearia);
        // Eu inicio o barbeiro antes dos clientes para ele poder dormir esperando atendimento.
        barbeiro.start();

        List<Cliente> clientes = new ArrayList<>();
        try {
            // Eu crio e inicio cada cliente como uma thread independente.
            for (int i = 1; i <= quantidadeClientes; i++) {
                Cliente cliente = new Cliente(i, barbearia);
                clientes.add(cliente);
                cliente.start();
            }
            // Eu aguardo todos os clientes terminarem antes de encerrar o monitor.
            for (Cliente cliente : clientes) {
                cliente.join();
            }
        } finally {
            // Eu interrompo clientes que ainda estejam ativos e acordo o barbeiro para encerrar.
            for (Cliente cliente : clientes) {
                if (cliente.isAlive()) {
                    cliente.interrupt();
                }
            }
            barbearia.encerrar();
            // Eu aguardo o barbeiro sair de sua espera antes de concluir a simulação.
            barbeiro.join();
        }
        System.out.println("A barbearia encerrou o expediente.");
    }

    private static int argumento(String[] args, int indice, int padrao, String nome) {
        // Eu verifico os parâmetros antes de iniciar as threads.
        int valor = args.length > indice ? Integer.parseInt(args[indice]) : padrao;
        if (valor <= 0) {
            throw new IllegalArgumentException("A quantidade de " + nome + " deve ser maior que zero.");
        }
        return valor;
    }
}
