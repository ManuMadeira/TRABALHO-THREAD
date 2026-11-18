package br.edu.sistemasdistribuidos.exercicio01atendimentobar.atores;

import br.edu.sistemasdistribuidos.exercicio01atendimentobar.controle.Pedido;

import java.util.concurrent.ThreadLocalRandom;

/** Esta classe representa um cliente concorrente que pode fazer pedidos em várias rodadas. */
public final class Cliente extends Thread {
    private final Garcom garcom;
    private final int rodadas;

    public Cliente(int id, Garcom garcom, int rodadas) {
        super("Cliente-" + id);
        this.garcom = garcom;
        this.rodadas = rodadas;
    }

    @Override
    public void run() {
        try {
            // O ciclo repete as rodadas, a escolha é registrada no garçom e o pedido é aguardado quando solicitado.
            for (int rodada = 1; rodada <= rodadas; rodada++) {
                boolean desejaPedido = ThreadLocalRandom.current().nextInt(100) < 70;
                if (!desejaPedido) {
                    System.out.printf("%s não fará pedido na rodada %d.%n", getName(), rodada);
                } else {
                    System.out.printf("%s fez um pedido na rodada %d.%n", getName(), rodada);
                }
                garcom.registrarEscolha(new Pedido(getName(), rodada), desejaPedido);
                if (desejaPedido) {
                    consumirPedido();
                }
            }
        } catch (InterruptedException e) {
            // O código preserva a interrupção para que a thread possa ser encerrada corretamente.
            interrupt();
        }
    }

    private void consumirPedido() throws InterruptedException {
        // O código simula o tempo de consumo; durante essa espera os outros clientes seguem em paralelo.
        long tempoConsumo = ThreadLocalRandom.current().nextLong(300, 1001);
        System.out.printf("%s está consumindo por %d ms.%n", getName(), tempoConsumo);
        Thread.sleep(tempoConsumo);
    }
}
