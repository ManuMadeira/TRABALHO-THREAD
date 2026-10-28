package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.app;

import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.contas.ContaBancaria;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Buffer;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Consumidor;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Produto;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.Produtor;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.monitores.BufferMonitor;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor.semaforos.BufferSemaforos;
import br.edu.sistemasdistribuidos.exercicio04problemasclassicos.roletas.Roletas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class Main {
    private static final int CAPACIDADE_BUFFER = 3;
    private static final int QUANTIDADE_PRODUTORES = 2;
    private static final int QUANTIDADE_CONSUMIDORES = 2;
    private static final int PRODUTOS_POR_PRODUTOR = 5;

    private Main() {
    }

    public static void main(String[] args) throws InterruptedException {
        int quantidadeRoletas = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        int quantidadePessoas = args.length > 1 ? Integer.parseInt(args[1]) : 10;

        System.out.println("========== 4.a - ROLETAS ==========");
        executarRoletas(quantidadeRoletas, quantidadePessoas);

        System.out.println("\n========== 4.b - CONTAS BANCÁRIAS ==========");
        executarContasBancarias();

        System.out.println("\n========== 4.c - PRODUTOR/CONSUMIDOR COM SEMÁFOROS ==========");
        executarProdutorConsumidor(new BufferSemaforos(CAPACIDADE_BUFFER), "Semaforos");

        System.out.println("\n========== 4.c - PRODUTOR/CONSUMIDOR COM MONITOR ==========");
        executarProdutorConsumidor(new BufferMonitor(CAPACIDADE_BUFFER), "Monitor");

        System.out.println("\nDemonstração do Exercício 4 concluída.");
    }

    private static void executarRoletas(int quantidadeRoletas, int quantidadePessoas)
            throws InterruptedException {
        if (quantidadePessoas < 0) {
            throw new IllegalArgumentException("A quantidade de pessoas não pode ser negativa.");
        }

        Roletas roletas = new Roletas(quantidadeRoletas);
        List<Thread> pessoas = new ArrayList<>();
        for (int id = 1; id <= quantidadePessoas; id++) {
            final int pessoaId = id;
            pessoas.add(new Thread(() -> {
                int roleta = -1;
                try {
                    roleta = roletas.entrar();
                    System.out.printf("Pessoa %d começou a usar a roleta %d.%n", pessoaId, roleta);
                    Thread.sleep(tempoAleatorio());
                    System.out.printf("Pessoa %d terminou de usar a roleta %d.%n", pessoaId, roleta);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    if (roleta >= 0) {
                        roletas.sair(roleta);
                    }
                }
            }, "Pessoa-" + pessoaId));
        }
        iniciarEAguardar(pessoas);
    }

    private static void executarContasBancarias() throws InterruptedException {
        ContaBancaria contaA = new ContaBancaria("A", new BigDecimal("1000.00"));
        ContaBancaria contaB = new ContaBancaria("B", new BigDecimal("500.00"));
        List<Thread> operacoes = new ArrayList<>();

        operacoes.add(new Thread(() ->
                contaA.depositar(new BigDecimal("200.00")), "Deposito-A"));
        operacoes.add(new Thread(() ->
                contaA.sacar(new BigDecimal("150.00")), "Saque-A"));
        operacoes.add(new Thread(() ->
                contaA.creditarJuros(new BigDecimal("0.02")), "Juros-A"));
        operacoes.add(new Thread(() ->
                ContaBancaria.transferir(contaA, contaB, new BigDecimal("300.00")),
                "Transferencia-A-para-B"));
        operacoes.add(new Thread(() ->
                ContaBancaria.transferir(contaB, contaA, new BigDecimal("100.00")),
                "Transferencia-B-para-A"));
        operacoes.add(new Thread(() ->
                contaB.depositar(new BigDecimal("50.00")), "Deposito-B"));

        iniciarEAguardar(operacoes);
        System.out.printf("Saldos finais: conta %s = R$ %s; conta %s = R$ %s.%n",
                contaA.getIdentificador(), contaA.getSaldo().toPlainString(),
                contaB.getIdentificador(), contaB.getSaldo().toPlainString());
    }

    private static void executarProdutorConsumidor(Buffer buffer, String variante)
            throws InterruptedException {
        List<Thread> consumidores = new ArrayList<>();
        for (int id = 1; id <= QUANTIDADE_CONSUMIDORES; id++) {
            consumidores.add(new Thread(new Consumidor(buffer, variante + "-C" + id),
                    variante + "-Consumidor-" + id));
        }

        List<Thread> produtores = new ArrayList<>();
        for (int id = 1; id <= QUANTIDADE_PRODUTORES; id++) {
            produtores.add(new Thread(
                    new Produtor(buffer, variante + "-P" + id, PRODUTOS_POR_PRODUTOR),
                    variante + "-Produtor-" + id));
        }

        try {
            iniciarThreads(consumidores);
            iniciarThreads(produtores);
            aguardarThreads(produtores);

            for (int i = 0; i < QUANTIDADE_CONSUMIDORES; i++) {
                buffer.inserir(Produto.sinalDeEncerramento());
            }
            aguardarThreads(consumidores);
        } finally {
            interromperThreads(produtores);
            interromperThreads(consumidores);
        }
    }

    private static void iniciarEAguardar(List<Thread> threads) throws InterruptedException {
        try {
            iniciarThreads(threads);
            aguardarThreads(threads);
        } finally {
            interromperThreads(threads);
        }
    }

    private static void iniciarThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    private static void aguardarThreads(List<Thread> threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static void interromperThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
    }

    private static long tempoAleatorio() {
        return ThreadLocalRandom.current().nextLong(250, 751);
    }
}
