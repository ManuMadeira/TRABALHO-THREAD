package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.roletas;

import java.util.concurrent.Semaphore;

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
        vagas.acquire();
        synchronized (proprietarios) {
            for (int i = 0; i < proprietarios.length; i++) {
                if (proprietarios[i] == null) {
                    proprietarios[i] = Thread.currentThread();
                    return i + 1;
                }
            }
        }

        vagas.release();
        throw new IllegalStateException("Permissão adquirida, mas nenhuma roleta estava livre.");
    }

    public void sair(int identificador) {
        if (identificador < 1 || identificador > proprietarios.length) {
            throw new IllegalArgumentException("Identificador de roleta inválido: " + identificador);
        }

        synchronized (proprietarios) {
            int indice = identificador - 1;
            if (proprietarios[indice] != Thread.currentThread()) {
                throw new IllegalStateException("A roleta " + identificador
                        + " não está ocupada por esta thread.");
            }
            proprietarios[indice] = null;
        }
        vagas.release();
    }
}
