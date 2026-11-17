package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.roletas;

import java.util.concurrent.Semaphore;

/**
 * Eu controlo o acesso a um conjunto limitado de roletas: o semáforo limita
 * quantas pessoas entram, e o monitor protege a identificação de cada ocupante.
 */
public class Roletas {
    private final Semaphore vagas;
    private final Thread[] proprietarios;

    public Roletas(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("É necessário haver pelo menos uma roleta.");
        }
        vagas = new Semaphore(quantidade, true);
        proprietarios = new Thread[quantidade];
    }

    public int entrar() throws InterruptedException {
        // Eu aguardo uma vaga; a fila justa do semáforo organiza a ordem de aquisição.
        vagas.acquire();
        synchronized (proprietarios) {
            // Eu localizo e registro uma roleta livre dentro da região protegida.
            for (int i = 0; i < proprietarios.length; i++) {
                if (proprietarios[i] == null) {
                    proprietarios[i] = Thread.currentThread();
                    return i + 1;
                }
            }
        }

        // Eu devolvo a permissão se o estado não corresponder à vaga adquirida.
        vagas.release();
        throw new IllegalStateException("Permissão adquirida, mas nenhuma roleta estava livre.");
    }

    public void sair(int identificador) {
        if (identificador < 1 || identificador > proprietarios.length) {
            throw new IllegalArgumentException("Identificador de roleta inválido: " + identificador);
        }

        synchronized (proprietarios) {
            int indice = identificador - 1;
            // Eu só permito que a própria thread que entrou libere a roleta.
            if (proprietarios[indice] != Thread.currentThread()) {
                throw new IllegalStateException("A roleta " + identificador
                        + " não está ocupada por esta thread.");
            }
            proprietarios[indice] = null;
        }
        // Eu anuncio a vaga somente depois de atualizar o estado protegido.
        vagas.release();
    }
}
