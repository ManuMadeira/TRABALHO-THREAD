package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.contas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

public class ContaBancaria {
    private static final AtomicLong PROXIMO_ID = new AtomicLong();

    private final long id = PROXIMO_ID.getAndIncrement();
    private final String identificador;
    private BigDecimal saldo;

    public ContaBancaria(String identificador, BigDecimal saldoInicial) {
        if (identificador == null || identificador.trim().isEmpty()) {
            throw new IllegalArgumentException("O identificador da conta é obrigatório.");
        }
        validarValorNaoNegativo(saldoInicial, "Saldo inicial");
        this.identificador = identificador;
        this.saldo = dinheiro(saldoInicial);
    }

    public synchronized void depositar(BigDecimal valor) {
        validarValorPositivo(valor, "Depósito");
        saldo = saldo.add(dinheiro(valor));
        registrar("depositou R$ " + dinheiro(valor).toPlainString());
    }

    public synchronized boolean sacar(BigDecimal valor) {
        validarValorPositivo(valor, "Saque");
        BigDecimal valorFormatado = dinheiro(valor);
        if (saldo.compareTo(valorFormatado) < 0) {
            registrar("não conseguiu sacar R$ " + valorFormatado.toPlainString()
                    + " por saldo insuficiente");
            return false;
        }
        saldo = saldo.subtract(valorFormatado);
        registrar("sacou R$ " + valorFormatado.toPlainString());
        return true;
    }

    public synchronized void creditarJuros(BigDecimal taxa) {
        validarValorNaoNegativo(taxa, "Taxa de juros");
        BigDecimal juros = saldo.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
        saldo = saldo.add(juros);
        registrar("recebeu juros de R$ " + juros.toPlainString());
    }

    public static boolean transferir(ContaBancaria origem, ContaBancaria destino,
                                     BigDecimal valor) {
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("As contas de origem e destino são obrigatórias.");
        }
        validarValorPositivo(valor, "Transferência");
        if (origem == destino) {
            throw new IllegalArgumentException("A origem e o destino devem ser contas distintas.");
        }

        ContaBancaria primeira = origem.id < destino.id ? origem : destino;
        ContaBancaria segunda = origem.id < destino.id ? destino : origem;
        synchronized (primeira) {
            synchronized (segunda) {
                BigDecimal valorFormatado = dinheiro(valor);
                if (origem.saldo.compareTo(valorFormatado) < 0) {
                    origem.registrar("não conseguiu transferir R$ "
                            + valorFormatado.toPlainString() + " por saldo insuficiente");
                    return false;
                }
                origem.saldo = origem.saldo.subtract(valorFormatado);
                destino.saldo = destino.saldo.add(valorFormatado);
                origem.registrar("transferiu R$ " + valorFormatado.toPlainString()
                        + " para a conta " + destino.identificador);
                destino.registrar("recebeu R$ " + valorFormatado.toPlainString()
                        + " da conta " + origem.identificador);
                return true;
            }
        }
    }

    public synchronized BigDecimal getSaldo() {
        return saldo;
    }

    public String getIdentificador() {
        return identificador;
    }

    private static BigDecimal dinheiro(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private static void validarValorPositivo(BigDecimal valor, String operacao) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(operacao + " deve ser maior que zero.");
        }
    }

    private static void validarValorNaoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(campo + " não pode ser negativo.");
        }
    }

    private void registrar(String mensagem) {
        System.out.printf("[%s] Conta %s %s. Saldo: R$ %s.%n",
                Thread.currentThread().getName(), identificador, mensagem,
                saldo.toPlainString());
    }
}
