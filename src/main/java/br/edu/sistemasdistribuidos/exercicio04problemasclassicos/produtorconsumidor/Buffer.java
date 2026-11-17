package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor;

/** Eu descrevo as operações que os produtores e consumidores compartilham. */
public interface Buffer {
    /** Eu insiro um produto, aguardando espaço quando o buffer estiver cheio. */
    void inserir(Produto produto) throws InterruptedException;

    /** Eu retiro um produto, aguardando até que exista algum disponível. */
    Produto remover() throws InterruptedException;
}
