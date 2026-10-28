package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.monitores;

import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Buffer;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Produto;

import java.util.ArrayDeque;
import java.util.Queue;

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
        while (fila.size() == capacidade) {
            wait();
        }
        fila.add(produto);
        notifyAll();
    }

    @Override
    public synchronized Produto remover() throws InterruptedException {
        while (fila.isEmpty()) {
            wait();
        }
        Produto produto = fila.remove();
        notifyAll();
        return produto;
    }
}
