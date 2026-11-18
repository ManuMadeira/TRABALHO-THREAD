package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor;

/** Esta interface descreve as operações que os produtores e consumidores compartilham. */
public interface Buffer {
    /** A classe insiro um produto, aguardando espaço quando o buffer estiver cheio. */
    void inserir(Produto produto) throws InterruptedException;

    /** O código retira um produto, aguardando até que exista algum disponível. */
    Produto remover() throws InterruptedException;
}
