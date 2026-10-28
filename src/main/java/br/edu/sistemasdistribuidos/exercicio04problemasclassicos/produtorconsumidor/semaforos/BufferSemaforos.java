package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.semaforos;

import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Buffer;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Produto;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BufferSemaforos implements Buffer {
    private final Queue<Produto> fila = new ArrayDeque<>();
    private final Semaphore espacosVazios;
    private final Semaphore itensDisponiveis = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1, true);

    public BufferSemaforos(int capacidade) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade do buffer deve ser maior que zero.");
        }
        espacosVazios = new Semaphore(capacidade, true);
    }

    @Override
    public void inserir(Produto produto) throws InterruptedException {
        if (produto == null) {
            throw new IllegalArgumentException("Não é possível inserir um produto nulo.");
        }
        espacosVazios.acquire();
        boolean bloqueioAdquirido = false;
        try {
            mutex.acquire();
            bloqueioAdquirido = true;
            fila.add(produto);
        } catch (InterruptedException e) {
            espacosVazios.release();
            throw e;
        } finally {
            if (bloqueioAdquirido) {
                mutex.release();
            }
        }
        itensDisponiveis.release();
    }

    @Override
    public Produto remover() throws InterruptedException {
        itensDisponiveis.acquire();
        boolean bloqueioAdquirido = false;
        Produto produto;
        try {
            mutex.acquire();
            bloqueioAdquirido = true;
            produto = fila.remove();
        } catch (InterruptedException e) {
            itensDisponiveis.release();
            throw e;
        } finally {
            if (bloqueioAdquirido) {
                mutex.release();
            }
        }
        espacosVazios.release();
        return produto;
    }
}
