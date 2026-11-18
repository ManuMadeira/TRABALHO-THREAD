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
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/** Esta classe executa demonstrações de sincronização para os problemas clássicos do exercício. */
public final class Main {
    private static final int CAPACIDADE_BUFFER = 3;
    private static final int QUANTIDADE_PRODUTORES = 2;
    private static final int QUANTIDADE_CONSUMIDORES = 2;
    private static final int PRODUTOS_POR_PRODUTOR = 5;

    private Main() {
    }

    /** O método apresenta o submenu e executo apenas a demonstração escolhida. */
    public static void main(String[] args) throws InterruptedException {
        int quantidadeRoletas = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        int quantidadePessoas = args.length > 1 ? Integer.parseInt(args[1]) : 10;

        try (Scanner scanner = new Scanner(System.in)) {
            executarMenu(scanner, quantidadeRoletas, quantidadePessoas);
        }
    }

    /**
     * O código usa o mesmo Scanner do menu principal para evitar que dois leitores
     * concorram pela entrada do teclado.
     */
    public static void executarMenu(Scanner scanner) throws InterruptedException {
        executarMenu(scanner, 3, 10);
    }

    private static void executarMenu(Scanner scanner, int quantidadeRoletas,
                                     int quantidadePessoas) throws InterruptedException {
        boolean executando = true;
        while (executando) {
            System.out.println("========== EXERCÍCIO 4 - PROBLEMAS CLÁSSICOS ==========");
            System.out.println("[1] Roletas");
            System.out.println("[2] Contas bancárias");
            System.out.println("[3] Produtor/consumidor com semáforos");
            System.out.println("[4] Produtor/consumidor com monitor");
            System.out.println("[0] Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");

            if (!scanner.hasNextLine()) {
                return;
            }

            final int opcao;
            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número de 0 a 4.\n");
                continue;
            }

            switch (opcao) {
                case 0:
                    executando = false;
                    break;
                case 1:
                    System.out.println("\n========== 4.a - ROLETAS ==========");
                    executarRoletas(quantidadeRoletas, quantidadePessoas);
                    break;
                case 2:
                    System.out.println("\n========== 4.b - CONTAS BANCÁRIAS ==========");
                    executarContasBancarias();
                    break;
                case 3:
                    System.out.println("\n========== 4.c - PRODUTOR/CONSUMIDOR COM SEMÁFOROS ==========");
                    executarProdutorConsumidor(
                            new BufferSemaforos(CAPACIDADE_BUFFER), "Semaforos");
                    break;
                case 4:
                    System.out.println("\n========== 4.c - PRODUTOR/CONSUMIDOR COM MONITOR ==========");
                    executarProdutorConsumidor(
                            new BufferMonitor(CAPACIDADE_BUFFER), "Monitor");
                    break;
                default:
                    System.out.println("Opção fora do intervalo. Escolha um número de 0 a 4.\n");
            }
        }
        System.out.println("Retornando ao menu principal.");
    }

    private static void executarRoletas(int quantidadeRoletas, int quantidadePessoas)
            throws InterruptedException {
        if (quantidadePessoas < 0) {
            throw new IllegalArgumentException("A quantidade de pessoas não pode ser negativa.");
        }

        Roletas roletas = new Roletas(quantidadeRoletas);
        List<Thread> pessoas = new ArrayList<>();
        // O código cria uma thread por pessoa; cada uma ocupa e libera uma roleta de forma segura.
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
                        // O código libera a roleta até mesmo quando a pessoa é interrompida.
                        roletas.sair(roleta);
                    }
                }
            }, "Pessoa-" + pessoaId));
        }
        iniciarEAguardar(pessoas);
    }

    /** O método compara operações simultâneas em duas contas e consulto os saldos ao final. */
    private static void executarContasBancarias() throws InterruptedException {
        ContaBancaria contaA = new ContaBancaria("A", new BigDecimal("1000.00"));
        ContaBancaria contaB = new ContaBancaria("B", new BigDecimal("500.00"));
        List<Thread> operacoes = new ArrayList<>();

        // O método inicia operações concorrentes para exercitar a sincronização dos saldos.
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
        // O método inicia os consumidores primeiro para que possam aguardar os produtos no buffer.
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
            // O código espera todos os produtores terminarem antes de enviar os sinais de parada.
            aguardarThreads(produtores);

            // O código envia um sinal por consumidor para encerrar todas as threads consumidoras.
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
            // A classe só continuo depois que todas as threads desta demonstração terminarem.
            aguardarThreads(threads);
        } finally {
            interromperThreads(threads);
        }
    }

    private static void iniciarThreads(List<Thread> threads) {
        // O método inicia cada thread da lista.
        for (Thread thread : threads) {
            thread.start();
        }
    }

    private static void aguardarThreads(List<Thread> threads) throws InterruptedException {
        // O código usa join para coordenar o término antes de consultar os resultados.
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static void interromperThreads(List<Thread> threads) {
        // A classe peço a parada das threads que ainda estejam ativas ao sair da demonstração.
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
