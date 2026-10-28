package br.edu.sistemasdistribuidos.exercicio04problemasclassicos.produtorconsumidor;

public interface Buffer {
    void inserir(Produto produto) throws InterruptedException;

    Produto remover() throws InterruptedException;
}
