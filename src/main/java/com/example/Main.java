package com.example;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        PedidoDAO pedidoDAO = new PedidoDAO();

        try {
            System.out.println("--- Testes CRUD ---");

            // C - Criar Pedido
            System.out.println("\nCriando um novo pedido...");
            Pedido novoPedido = new Pedido(
                "João Paulo",
                Date.valueOf(LocalDate.now()),
                new BigDecimal("250.75"),
                3,
                "pendente",
                new BigDecimal("0.00")
            );
            novoPedido = pedidoDAO.criarPedido(novoPedido);
            System.out.println("Pedido criado: " + novoPedido);

            // R - Ler (Buscar por ID)
            System.out.println("\nBuscando pedido com ID " + novoPedido.getId() + "...");
            Pedido pedidoBuscado = pedidoDAO.buscarPedidoPorId(novoPedido.getId());
            if (pedidoBuscado != null) {
                System.out.println("Pedido encontrado: " + pedidoBuscado);
            } else {
                System.out.println("Pedido não encontrado.");
            }

            // R - Ler (Listar Todos)
            System.out.println("\nListando todos os pedidos:");
            List<Pedido> todosPedidos = pedidoDAO.listarTodosPedidos();
            todosPedidos.forEach(System.out::println);

            // U - Atualizar Pedido
            System.out.println("\nAtualizando o status do pedido " + novoPedido.getId() + " para 'entregue'...");
            if (pedidoBuscado != null) {
                pedidoBuscado.setStatus("entregue");
                boolean atualizado = pedidoDAO.atualizarPedido(pedidoBuscado);
                System.out.println("Pedido atualizado? " + atualizado);
                System.out.println("Pedido após atualização: " + pedidoDAO.buscarPedidoPorId(novoPedido.getId()));
            }

            // D - Deletar Pedido
            System.out.println("\nDeletando o pedido com ID " + novoPedido.getId() + "...");
            boolean deletado = pedidoDAO.deletarPedido(novoPedido.getId());
            System.out.println("Pedido deletado? " + deletado);
            System.out.println("Buscando pedido deletado: " + pedidoDAO.buscarPedidoPorId(novoPedido.getId()));


            System.out.println("\n--- Testes de Agregação ---");

            // COUNT
            long totalPedidos = pedidoDAO.contarTotalPedidos();
            System.out.println("\nTotal de Pedidos: " + totalPedidos);

            // SUM
            BigDecimal somaValores = pedidoDAO.somarValorTotalPedidos();
            System.out.println("Soma total dos valores dos pedidos: R$" + somaValores);

            // AVG
            BigDecimal mediaValores = pedidoDAO.calcularMediaValorPedidos();
            System.out.println("Média do valor dos pedidos: R$" + mediaValores);

            // MAX
            BigDecimal maxValor = pedidoDAO.encontrarMaxValorPedido();
            System.out.println("Maior valor de pedido: R$" + maxValor);

            // MIN
            BigDecimal minValor = pedidoDAO.encontrarMinValorPedido();
            System.out.println("Menor valor de pedido: R$" + minValor);

            // COUNT com GROUP BY (por status)
            System.out.println("\nContagem de Pedidos por Status:");
            Map<String, Long> pedidosPorStatus = pedidoDAO.contarPedidosPorStatus();
            pedidosPorStatus.forEach((status, count) -> System.out.println("  " + status + ": " + count));

            // SUM com GROUP BY (por cliente)
            System.out.println("\nSoma dos Valores de Pedidos por Cliente:");
            Map<String, BigDecimal> valorPorCliente = pedidoDAO.somarValorPedidosPorCliente();
            valorPorCliente.forEach((cliente, totalGasto) -> System.out.println("  " + cliente + ": R$" + totalGasto));


        } catch (SQLException e) {
            System.err.println("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }
}
