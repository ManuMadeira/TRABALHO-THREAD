package br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.recursos;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Eu centralizo os estados dos filósofos e a disponibilidade dos palitos.
 * O monitor garante que a verificação e a reserva dos dois palitos sejam atômicas.
 * Eu uso uma fila FIFO para evitar ultrapassagens, embora a JVM não garanta o escalonamento.
 */
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
            // Eu atendo a fila por ordem e só avanço quando ambos os palitos estão livres.
            // Uso wait para liberar o monitor enquanto aguardo a devolução dos palitos.
            while (filaDeFome.peekFirst() != id || !doisPalitosDisponiveis(id)) {
                wait();
            }

            // Eu retiro e marco os dois palitos juntos, sem deixar outra thread pegá-los.
            filaDeFome.removeFirst();
            int palitoEsquerdo = palitoEsquerdo(id);
            int palitoDireito = palitoDireito(id);
            palitosDisponiveis[palitoEsquerdo] = false;
            palitosDisponiveis[palitoDireito] = false;
            estados[id] = EstadoFilosofo.COMENDO;
            registrar("Filósofo " + id + " pegou os palitos "
                    + palitoEsquerdo + " e " + palitoDireito + " e está COMENDO.");
        } catch (InterruptedException e) {
            // Eu retiro da fila quem desistiu de esperar e acordo os demais para reavaliar.
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

        // Eu libero os dois recursos de uma vez e aviso aos que estão esperando.
        palitosDisponiveis[palitoEsquerdo(id)] = true;
        palitosDisponiveis[palitoDireito(id)] = true;
        estados[id] = EstadoFilosofo.PENSANDO;
        registrar("Filósofo " + id + " devolveu os palitos e voltou a meditar.");
        notifyAll();
    }

    private boolean doisPalitosDisponiveis(int id) {
        // Eu exijo os dois palitos simultaneamente, evitando que cada filósofo segure um
        // enquanto espera pelo outro, situação que poderia causar deadlock.
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
