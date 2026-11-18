package br.edu.sistemasdistribuidos.exercicio01atendimentobar.controle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/** Esta classe representa a thread que retira lotes da fila e prepara os pedidos. */
public final class Bartender extends Thread {
    private final BlockingQueue<LotePedidos> fila = new LinkedBlockingQueue<>();

    public Bartender() {
        super("Bartender");
    }

    public void atender(List<Pedido> pedidos) throws InterruptedException {
        // O código publica um lote imutável na fila e aguarda sua conclusão.
        LotePedidos lote = new LotePedidos(pedidos);
        fila.put(lote);
        lote.aguardarConclusao();
    }

    @Override
    public void run() {
        try {
            // O código retira e processa lotes em FIFO; take me deixa bloqueado quando não há trabalho.
            while (!isInterrupted()) {
                LotePedidos lote = fila.take();
                System.out.printf("Bartender recebeu %d pedido(s) do garçom.%n",
                        lote.pedidos.size());
                for (Pedido pedido : lote.pedidos) {
                    System.out.printf("Bartender preparou o pedido da rodada %d para %s.%n",
                            pedido.getRodada(), pedido.getCliente());
                    // O lote é sinalizado cada pedido assim que ele fica pronto para liberar seu cliente.
                    pedido.concluirAtendimento();
                }
                // O método também libera o garçom que aguarda a conclusão deste lote.
                lote.concluir();
            }
        } catch (InterruptedException e) {
            // O código restaura o sinal para que o ciclo do bartender termine.
            interrupt();
        }
    }

    /** Esta classe encapsula os pedidos de uma viagem e o sinal de conclusão correspondente. */
    private static final class LotePedidos {
        private final List<Pedido> pedidos;
        private final CountDownLatch concluido = new CountDownLatch(1);

        private LotePedidos(List<Pedido> pedidos) {
            // A lista é copiada a lista para impedir que o garçom altere o lote enquanto o preparo.
            this.pedidos = Collections.unmodifiableList(new ArrayList<>(pedidos));
        }

        private void concluir() {
            // O lote é sinalizado que o lote inteiro já foi processado.
            concluido.countDown();
        }

        private void aguardarConclusao() throws InterruptedException {
            // O método mantém o garçom aguardando sem polling enquanto o bartender trabalha.
            concluido.await();
        }
    }
}
