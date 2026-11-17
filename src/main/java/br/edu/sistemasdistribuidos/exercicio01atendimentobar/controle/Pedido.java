package br.edu.sistemasdistribuidos.exercicio01atendimentobar.controle;

import java.util.concurrent.CountDownLatch;

/** Eu guardo os dados de um pedido e sincronizo a espera pelo seu atendimento. */
public final class Pedido {
    private final String cliente;
    private final int rodada;
    private final CountDownLatch atendido = new CountDownLatch(1);

    public Pedido(String cliente, int rodada) {
        this.cliente = cliente;
        this.rodada = rodada;
    }

    public String getCliente() {
        return cliente;
    }

    public int getRodada() {
        return rodada;
    }

    public void concluirAtendimento() {
        // Eu libero o cliente que está aguardando este pedido específico.
        atendido.countDown();
    }

    public void aguardarAtendimento() throws InterruptedException {
        // Eu bloqueio o cliente sem espera ativa até o bartender concluir o pedido.
        atendido.await();
    }
}
