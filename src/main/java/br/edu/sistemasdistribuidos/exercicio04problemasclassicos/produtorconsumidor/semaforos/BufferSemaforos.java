package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.semaforos;

import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Buffer;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Produto;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Eu implemento o buffer limitado contando espaços e itens com semáforos;
 * uso um terceiro semáforo para proteger a fila contra condições de corrida.
 */
public class BufferSemaforos implements Buffer {
    private final Queue<Produto> fila = new ArrayDeque<>();
    // Eu uso semáforos justos para vagas e mutex, reduzindo starvation nessas esperas.
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
        // Eu reservo um espaço antes de acessar a fila, para nunca exceder a capacidade.
        espacosVazios.acquire();
        boolean bloqueioAdquirido = false;
        try {
            // Eu protejo a alteração da fila; somente uma thread entra nessa região por vez.
            mutex.acquire();
            bloqueioAdquirido = true;
            fila.add(produto);
        } catch (InterruptedException e) {
            // Se eu for interrompido antes de inserir, devolvo a vaga que havia reservado.
            espacosVazios.release();
            throw e;
        } finally {
            if (bloqueioAdquirido) {
                // Eu libero o mutex mesmo se ocorrer uma interrupção ou erro na operação.
                mutex.release();
            }
        }
        // Eu anuncio o novo item somente depois que ele já está na fila.
        itensDisponiveis.release();
    }

    @Override
    public Produto remover() throws InterruptedException {
        // Eu só prossigo quando um produtor tiver anunciado um item disponível.
        itensDisponiveis.acquire();
        boolean bloqueioAdquirido = false;
        Produto produto;
        try {
            mutex.acquire();
            bloqueioAdquirido = true;
            produto = fila.remove();
        } catch (InterruptedException e) {
            // Se eu não conseguir retirar o item, restauro a permissão que consumi.
            itensDisponiveis.release();
            throw e;
        } finally {
            if (bloqueioAdquirido) {
                // Eu libero o mutex para que outra thread possa acessar a fila.
                mutex.release();
            }
        }
        // Eu sinalizo que a retirada abriu espaço para um produtor.
        espacosVazios.release();
        return produto;
    }
}
