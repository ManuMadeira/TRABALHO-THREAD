package br.edu.sistemasdistribuidos;

import br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.app.Main;

import java.util.Scanner;

/**
 * O método apresenta as opções do trabalho e encaminha a execução para cada exercício.
 * O menu roda na thread principal; cada exercício coordena suas próprias threads.
 */
public final class MenuPrincipal {
    private MenuPrincipal() {
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean executando = true;
            // O método mantém o menu ativo até a pessoa escolher sair ou a execução ser interrompida.
            while (executando) {
                exibirMenu();
                System.out.print("Escolha uma opção: ");

                if (!scanner.hasNextLine()) {
                    System.out.println("\nEntrada encerrada. Até a próxima!");
                    break;
                }

                String entrada = scanner.nextLine().trim();
                final int opcao;
                try {
                    opcao = Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Digite um número de 0 a 4.\n");
                    continue;
                }

                if (opcao == 0) {
                    System.out.println("Obrigado por utilizar o programa. Até a próxima!");
                    break;
                }
                if (opcao < 1 || opcao > 4) {
                    System.out.println("Opção fora do intervalo. Escolha um número de 0 a 4.\n");
                    continue;
                }

                try {
                    // Esta classe executa uma opção por vez; o menu só volta quando o exercício retorna.
                    executarExercicio(opcao, scanner);
                } catch (InterruptedException e) {
                    // O código preserva a interrupção e encerro o laço para não ignorar o pedido de parada.
                    Thread.currentThread().interrupt();
                    System.out.println("\nA execução foi interrompida. Encerrando o menu.");
                    executando = false;
                }

                if (executando) {
                    System.out.print("\nPressione Enter para voltar ao menu principal...");
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                        System.out.println();
                    } else {
                        System.out.println("\nEntrada encerrada. Até a próxima!");
                        executando = false;
                    }
                }
            }
        }
    }

    private static void exibirMenu() {
        System.out.println("==============================================");
        System.out.println("       SISTEMAS DISTRIBUÍDOS - UTFPR");
        System.out.println("==============================================");
        System.out.println("[1] Exercício 01: Atendimento no Bar");
        System.out.println("[2] Exercício 02: Barbeiro Dorminhoco");
        System.out.println("[3] Exercício 03: O Jantar dos Filósofos");
        System.out.println("[4] Exercício 04: Problemas Clássicos");
        System.out.println("    (Roletas, Contas Bancárias, Produtor/Consumidor)");
        System.out.println("[0] Sair");
        System.out.println("==============================================");
    }

    private static void executarExercicio(int opcao, Scanner scanner) throws InterruptedException {
        // O método encaminha a opção selecionada; os exercícios implementados iniciam e aguardam
        // suas próprias threads antes de retornar para este menu.
        switch (opcao) {
            case 1:
                System.out.println("\nIniciando o Atendimento no Bar...\n");
                br.edu.sistemasdistribuidos.exercicio01atendimentobar.app.Main
                        .main(new String[0]);
                break;
            case 2:
                System.out.println("\nIniciando o Barbeiro Dorminhoco...\n");
                br.edu.sistemasdistribuidos.exercicio02barbeirodorminhoco.app.Main
                        .main(new String[0]);
                break;
            case 3:
                System.out.println("\nIniciando o Jantar dos Filósofos...\n");
                Main.main(new String[0]);
                break;
            case 4:
                System.out.println("\nAbrindo o menu de Problemas Clássicos...\n");
                br.edu.sistemasdistribuidos.exercicio04problemasclassicos.app.Main
                        .executarMenu(scanner);
                break;
            default:
                throw new IllegalArgumentException("Opção de exercício inválida: " + opcao);
        }
    }

}
