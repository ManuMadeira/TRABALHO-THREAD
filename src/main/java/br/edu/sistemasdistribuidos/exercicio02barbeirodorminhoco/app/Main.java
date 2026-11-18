package br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.app;

/** Esta classe oferece o ponto de entrada que encaminha para a simulação do barbeiro dorminhoco. */
public final class Main {
    private Main() {
    }

    /** O método delega a execução à classe que cria e coordena as threads da barbearia. */
    public static void main(String[] args) throws InterruptedException {
        BarbeiroDorminhoco.main(args);
    }
}
