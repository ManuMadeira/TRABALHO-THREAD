package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.monitores;

import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Buffer;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Produto;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Eu implemento o buffer limitado com monitor: uso synchronized para proteger a fila
 * e wait/notifyAll para coordenar quem espera por espaço ou por itens. Assim evito
 * condições de corrida, mas o monitor não garante por si só que ninguém sofra starvation.
 */
public class BufferMonitor implements Buffer {
    private final Queue<Produto> fila = new ArrayDeque<>();
    private final int capacidade;

    public BufferMonitor(int capacidade) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade do buffer deve ser maior que zero.");
        }
        this.capacidade = capacidade;
    }

    @Override
    public synchronized void inserir(Produto produto) throws InterruptedException {
        if (produto == null) {
            throw new IllegalArgumentException("Não é possível inserir um produto nulo.");
        }
        // Eu verifico em while porque posso acordar sem a condição ter mudado.
        // Uso wait para liberar o monitor enquanto aguardo, permitindo retirar itens.
        while (fila.size() == capacidade) {
            wait();
        }
        fila.add(produto);
        // Eu acordo todos para que produtores e consumidores reavaliem suas condições.
        notifyAll();
    }

    @Override
    public synchronized Produto remover() throws InterruptedException {
        // Eu aguardo enquanto não há itens; com wait, libero o monitor para os produtores.
        while (fila.isEmpty()) {
            wait();
        }
        Produto produto = fila.remove();
        // Eu aviso que surgiu espaço e também permito que todos verifiquem se podem agir.
        notifyAll();
        return produto;
    }
}
