package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.monitores;

import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Buffer;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Produto;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A classe o buffer limitado com monitor: uso synchronized para proteger a fila
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
        // O método verifica em while porque posso acordar sem a condição ter mudado.
        // A instrução wait libera o monitor enquanto aguarda enquanto permite retirar itens.
        while (fila.size() == capacidade) {
            wait();
        }
        fila.add(produto);
        // O código acorda todos para que produtores e consumidores reavaliem suas condições.
        notifyAll();
    }

    @Override
    public synchronized Produto remover() throws InterruptedException {
        // O método aguarda enquanto não há itens; com wait, libera o monitor para os produtores.
        while (fila.isEmpty()) {
            wait();
        }
        Produto produto = fila.remove();
        // O código avisa que surgiu espaço e também permite que todos verifiquem se podem agir.
        notifyAll();
        return produto;
    }
}
