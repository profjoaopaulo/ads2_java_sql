package com.example;

import java.math.BigDecimal;
import java.sql.Date;

public class Pedido {
    private int id;
    private String clienteNome;
    private Date dataPedido;
    private BigDecimal valorTotal;
    private int quantidadeItens;
    private String status;
    private BigDecimal desconto;

    // Construtor completo
    public Pedido(int id, String clienteNome, Date dataPedido, BigDecimal valorTotal, int quantidadeItens, String status, BigDecimal desconto) {
        this.id = id;
        this.clienteNome = clienteNome;
        this.dataPedido = dataPedido;
        this.valorTotal = valorTotal;
        this.quantidadeItens = quantidadeItens;
        this.status = status;
        this.desconto = desconto;
    }

    // Construtor sem ID (para novos pedidos que serão inseridos)
    public Pedido(String clienteNome, Date dataPedido, BigDecimal valorTotal, int quantidadeItens, String status, BigDecimal desconto) {
        this.clienteNome = clienteNome;
        this.dataPedido = dataPedido;
        this.valorTotal = valorTotal;
        this.quantidadeItens = quantidadeItens;
        this.status = status;
        this.desconto = desconto;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public Date getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(Date dataPedido) {
        this.dataPedido = dataPedido;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public int getQuantidadeItens() {
        return quantidadeItens;
    }

    public void setQuantidadeItens(int quantidadeItens) {
        this.quantidadeItens = quantidadeItens;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    @Override
    public String toString() {
        return "Pedido{" +
               "id=" + id +
               ", clienteNome='" + clienteNome + '\'' +
               ", dataPedido=" + dataPedido +
               ", valorTotal=" + valorTotal +
               ", quantidadeItens=" + quantidadeItens +
               ", status='" + status + '\'' +
               ", desconto=" + desconto +
               '}';
    }
}
