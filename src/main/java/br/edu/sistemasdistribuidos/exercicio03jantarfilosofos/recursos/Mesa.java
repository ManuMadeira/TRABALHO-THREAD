package br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.recursos;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Mesa {
    public static final int QUANTIDADE_FILOSOFOS = 5;

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final EstadoFilosofo[] estados =
            new EstadoFilosofo[QUANTIDADE_FILOSOFOS];
    private final boolean[] palitosDisponiveis =
            new boolean[QUANTIDADE_FILOSOFOS];
    private final Deque<Integer> filaDeFome = new ArrayDeque<>();

    public Mesa() {
        Arrays.fill(estados, EstadoFilosofo.PENSANDO);
        Arrays.fill(palitosDisponiveis, true);
    }

    public synchronized void solicitarPalitos(int id) throws InterruptedException {
        validarId(id);
        estados[id] = EstadoFilosofo.COM_FOME;
        filaDeFome.addLast(id);
        registrar("Filósofo " + id + " está com fome; aguardando os dois palitos.");

        try {
            while (filaDeFome.peekFirst() != id || !doisPalitosDisponiveis(id)) {
                wait();
            }

            filaDeFome.removeFirst();
            int palitoEsquerdo = palitoEsquerdo(id);
            int palitoDireito = palitoDireito(id);
            palitosDisponiveis[palitoEsquerdo] = false;
            palitosDisponiveis[palitoDireito] = false;
            estados[id] = EstadoFilosofo.COMENDO;
            registrar("Filósofo " + id + " pegou os palitos "
                    + palitoEsquerdo + " e " + palitoDireito + " e está COMENDO.");
        } catch (InterruptedException e) {
            filaDeFome.remove(id);
            estados[id] = EstadoFilosofo.PENSANDO;
            notifyAll();
            throw e;
        }
    }

    public synchronized void devolverPalitos(int id) {
        validarId(id);
        if (estados[id] != EstadoFilosofo.COMENDO) {
            throw new IllegalStateException(
                    "O filósofo " + id + " não está comendo e não pode devolver palitos.");
        }

        palitosDisponiveis[palitoEsquerdo(id)] = true;
        palitosDisponiveis[palitoDireito(id)] = true;
        estados[id] = EstadoFilosofo.PENSANDO;
        registrar("Filósofo " + id + " devolveu os palitos e voltou a meditar.");
        notifyAll();
    }

    private boolean doisPalitosDisponiveis(int id) {
        return palitosDisponiveis[palitoEsquerdo(id)]
                && palitosDisponiveis[palitoDireito(id)];
    }

    private int palitoEsquerdo(int id) {
        return (id + QUANTIDADE_FILOSOFOS - 1) % QUANTIDADE_FILOSOFOS;
    }

    private int palitoDireito(int id) {
        return id;
    }

    private void validarId(int id) {
        if (id < 0 || id >= QUANTIDADE_FILOSOFOS) {
            throw new IllegalArgumentException("ID de filósofo inválido: " + id);
        }
    }

    private void registrar(String mensagem) {
        System.out.printf("[%s] %s%n", LocalTime.now().format(FORMATO_HORA), mensagem);
    }
}
