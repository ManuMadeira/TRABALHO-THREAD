package br.edu.sistemasdistribuidos.exercicio01atendimentobar.app;

import br.edu.sistemasdistribuidos.exercicio01atendimentobar.atores.Cliente;
import br.edu.sistemasdistribuidos.exercicio01atendimentobar.atores.Garcom;
import br.edu.sistemasdistribuidos.exercicio01atendimentobar.controle.Bartender;

import java.util.ArrayList;
import java.util.List;

/** Eu coordeno a criação e o encerramento das threads do atendimento no bar. */
public final class Main {
    private static final int CLIENTES_PADRAO = 12;
    private static final int GARCONS_PADRAO = 3;
    private static final int CAPACIDADE_PADRAO = 4;
    private static final int RODADAS_PADRAO = 5;

    private Main() {
    }

    /** Eu inicio o bartender, os garçons e os clientes e aguardo o fim do atendimento. */
    public static void main(String[] args) throws InterruptedException {
        if (args.length > 4) {
            throw new IllegalArgumentException(
                    "Use: Main [clientes] [garçons] [capacidade] [rodadas]");
        }

        int quantidadeClientes = argumento(args, 0, CLIENTES_PADRAO, "clientes");
        int quantidadeGarcons = argumento(args, 1, GARCONS_PADRAO, "garçons");
        int capacidade = argumento(args, 2, CAPACIDADE_PADRAO, "capacidade");
        int rodadas = argumento(args, 3, RODADAS_PADRAO, "rodadas");

        System.out.printf("Bar aberto: %d cliente(s), %d garçom(ns), capacidade %d, %d rodada(s).%n",
                quantidadeClientes, quantidadeGarcons, capacidade, rodadas);

        Bartender bartender = new Bartender();
        // Eu inicio o bartender antes dos garçons para que ele possa receber os lotes.
        bartender.start();

        Garcom[] garcons = new Garcom[quantidadeGarcons];
        int[] clientesPorGarcom = new int[quantidadeGarcons];
        // Eu distribuo os clientes entre os garçons para cada um saber quantas escolhas esperar.
        for (int cliente = 0; cliente < quantidadeClientes; cliente++) {
            clientesPorGarcom[cliente % quantidadeGarcons]++;
        }
        // Eu crio e inicio um trabalhador por garçom; cada um processa seus clientes em paralelo.
        for (int i = 0; i < garcons.length; i++) {
            garcons[i] = new Garcom(i + 1, clientesPorGarcom[i], capacidade, rodadas, bartender);
            garcons[i].start();
        }

        List<Cliente> clientes = new ArrayList<>();
        // Eu inicio os clientes como threads independentes, associados a seus garçons.
        for (int i = 0; i < quantidadeClientes; i++) {
            Cliente cliente = new Cliente(i + 1, garcons[i % quantidadeGarcons], rodadas);
            clientes.add(cliente);
            cliente.start();
        }

        try {
            // Eu aguardo primeiro os clientes e depois os garçons terminarem suas rodadas.
            for (Cliente cliente : clientes) {
                cliente.join();
            }
            for (Garcom garcom : garcons) {
                garcom.join();
            }
        } finally {
            // Se a execução for interrompida, eu sinalizo as threads ainda ativas.
            for (Cliente cliente : clientes) {
                if (cliente.isAlive()) {
                    cliente.interrupt();
                }
            }
            for (Garcom garcom : garcons) {
                if (garcom.isAlive()) {
                    garcom.interrupt();
                }
            }
            bartender.interrupt();
            // Eu aguardo o bartender encerrar antes de finalizar a demonstração.
            bartender.join();
        }

        System.out.println("O bar encerrou o atendimento.");
    }

    private static int argumento(String[] args, int indice, int padrao, String nome) {
        // Eu valido os parâmetros antes de iniciar qualquer thread.
        int valor = args.length > indice ? Integer.parseInt(args[indice]) : padrao;
        if (valor <= 0) {
            throw new IllegalArgumentException("A quantidade de " + nome + " deve ser maior que zero.");
        }
        return valor;
    }
}
