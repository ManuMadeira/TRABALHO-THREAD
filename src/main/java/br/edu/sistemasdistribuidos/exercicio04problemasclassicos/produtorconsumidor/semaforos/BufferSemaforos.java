package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.semaforos;

import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Buffer;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Produto;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * A classe o buffer limitado contando espaços e itens com semáforos;
 * uso um terceiro semáforo para proteger a fila contra condições de corrida.
 */
public class BufferSemaforos implements Buffer {
    private final Queue<Produto> fila = new ArrayDeque<>();
    // O código usa semáforos justos para vagas e mutex, reduzindo starvation nessas esperas.
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
        // A classe reservo um espaço antes de acessar a fila, para nunca exceder a capacidade.
        espacosVazios.acquire();
        boolean bloqueioAdquirido = false;
        try {
            // A classe protege a alteração da fila; somente uma thread entra nessa região por vez.
            mutex.acquire();
            bloqueioAdquirido = true;
            fila.add(produto);
        } catch (InterruptedException e) {
            // Se a thread for interrompida antes de inserir, o método devolve a vaga que havia reservado.
            espacosVazios.release();
            throw e;
        } finally {
            if (bloqueioAdquirido) {
                // O código libera o mutex mesmo se ocorrer uma interrupção ou erro na operação.
                mutex.release();
            }
        }
        // O código anuncia o novo item somente depois que ele já está na fila.
        itensDisponiveis.release();
    }

    @Override
    public Produto remover() throws InterruptedException {
        // O método só prossegue quando um produtor tiver anunciado um item disponível.
        itensDisponiveis.acquire();
        boolean bloqueioAdquirido = false;
        Produto produto;
        try {
            mutex.acquire();
            bloqueioAdquirido = true;
            produto = fila.remove();
        } catch (InterruptedException e) {
            // Se o método não conseguir retirar o item, a permissão consumida é restaurada.
            itensDisponiveis.release();
            throw e;
        } finally {
            if (bloqueioAdquirido) {
                // O código libera o mutex para que outra thread possa acessar a fila.
                mutex.release();
            }
        }
        // O lote é sinalizado que a retirada abriu espaço para um produtor.
        espacosVazios.release();
        return produto;
    }
}
