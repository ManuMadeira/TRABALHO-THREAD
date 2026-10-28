package br.edu.sistemasdistribuidos;

import br.edu.sistemasdistribuidos.exercicio03jantarfilosofos.app.Main;

import java.util.Scanner;

public final class MenuPrincipal {
    private MenuPrincipal() {
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean executando = true;
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
                    executarExercicio(opcao);
                } catch (InterruptedException e) {
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

    private static void executarExercicio(int opcao) throws InterruptedException {
        switch (opcao) {
            case 1:
                informarImplementacaoPendente("Atendimento no Bar");
                break;
            case 2:
                informarImplementacaoPendente("Barbeiro Dorminhoco");
                break;
            case 3:
                System.out.println("\nIniciando o Jantar dos Filósofos...\n");
                Main.main(new String[0]);
                break;
            case 4:
                System.out.println("\nIniciando os Problemas Clássicos...\n");
                br.edu.sistemasdistribuidos.exercicio04problemasclassicos.app.Main
                        .main(new String[0]);
                break;
            default:
                throw new IllegalArgumentException("Opção de exercício inválida: " + opcao);
        }
    }

    private static void informarImplementacaoPendente(String exercicio) {
        System.out.printf("%n%s ainda não possui uma implementação executável neste projeto.%n",
                exercicio);
    }
}
