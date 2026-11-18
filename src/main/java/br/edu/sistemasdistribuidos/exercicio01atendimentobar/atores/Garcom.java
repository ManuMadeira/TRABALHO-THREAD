package br.edu.sistemasdistribuidos.exercicio01atendimentobar.atores;

import br.edu.sistemasdistribuidos.exercicio01atendimentobar.controle.Bartender;
import br.edu.sistemasdistribuidos.exercicio01atendimentobar.controle.Pedido;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Esta classe representa uma thread que recebe as escolhas de seus clientes, agrupa pedidos
 * em lotes e os encaminha ao bartender.
 */
public final class Garcom extends Thread {
    private final int clientesAtendidos;
    private final int capacidade;
    private final int rodadas;
    private final Bartender bartender;
    private final BlockingQueue<Escolha> escolhas = new LinkedBlockingQueue<>();
    private final Map<Integer, ArrayDeque<Escolha>> escolhasFuturas = new HashMap<>();

    public Garcom(int id, int clientesAtendidos, int capacidade, int rodadas,
                  Bartender bartender) {
        super("Garcom-" + id);
        this.clientesAtendidos = clientesAtendidos;
        this.capacidade = capacidade;
        this.rodadas = rodadas;
        this.bartender = bartender;
    }

    public void registrarEscolha(Pedido pedido, boolean desejaPedido)
            throws InterruptedException {
        // O código publica a escolha na fila thread-safe; o garçom a retira sem disputa pela fila.
        escolhas.put(new Escolha(pedido, desejaPedido));
        if (desejaPedido) {
            // A entrega é confirmada ao cliente depois que o bartender sinalizar o atendimento.
            pedido.aguardarAtendimento();
            System.out.printf("%s entregou o pedido para %s.%n",
                    getName(), pedido.getCliente());
        }
    }

    @Override
    public void run() {
        try {
            // O método processa as rodadas em ordem até atender todos os clientes atribuídos.
            for (int rodada = 1; rodada <= rodadas; rodada++) {
                processarRodada(rodada);
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }

    private void processarRodada(int rodada) throws InterruptedException {
        int escolhasRecebidas = 0;
        List<Pedido> lote = new ArrayList<>(capacidade);

        // O método recebe uma escolha de cada cliente e os pedidos são enviados em lotes limitados
        // pela capacidade; no fim também um lote incompleto é encaminhado ao final, se houver.
        while (escolhasRecebidas < clientesAtendidos) {
            Escolha escolha = proximaEscolha(rodada);
            escolhasRecebidas++;
            if (!escolha.desejaPedido) {
                System.out.printf("%s confirmou que %s não fará pedido na rodada %d.%n",
                        getName(), escolha.pedido.getCliente(), rodada);
                continue;
            }

            lote.add(escolha.pedido);
            System.out.printf("%s recebeu o pedido de %s (posição %d na fila da rodada %d).%n",
                    getName(), escolha.pedido.getCliente(), lote.size(), rodada);
            if (lote.size() == capacidade) {
                enviarLote(rodada, lote);
                lote.clear();
            }
        }

        if (!lote.isEmpty()) {
            enviarLote(rodada, lote);
        }
    }

    private Escolha proximaEscolha(int rodada) throws InterruptedException {
        // O código reutiliza primeiro escolhas futuras que já chegaram fora da ordem das rodadas.
        ArrayDeque<Escolha> futuras = escolhasFuturas.get(rodada);
        if (futuras != null && !futuras.isEmpty()) {
            return futuras.removeFirst();
        }

        while (true) {
            // O código bloqueia sem ocupar CPU até chegar uma escolha na fila thread-safe.
            Escolha escolha = escolhas.take();
            if (escolha.pedido.getRodada() == rodada) {
                return escolha;
            }
            escolhasFuturas.computeIfAbsent(escolha.pedido.getRodada(),
                    ignorado -> new ArrayDeque<>()).addLast(escolha);
        }
    }

    private void enviarLote(int rodada, List<Pedido> lote) throws InterruptedException {
        System.out.printf("%s está indo à copa com %d pedido(s) da rodada %d.%n",
                getName(), lote.size(), rodada);
        // O método aguarda o bartender concluir o lote antes de continuar o atendimento.
        bartender.atender(lote);
        System.out.printf("%s voltou da copa para atender os clientes.%n", getName());
    }

    /** Esta classe agrupa a escolha do cliente e seu pedido para a fila do garçom. */
    private static final class Escolha {
        private final Pedido pedido;
        private final boolean desejaPedido;

        private Escolha(Pedido pedido, boolean desejaPedido) {
            this.pedido = pedido;
            this.desejaPedido = desejaPedido;
        }
    }
}
