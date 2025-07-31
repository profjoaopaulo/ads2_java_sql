package com.example;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoDAO {

    // --- Operações CRUD ---

    /**
     * Insere um novo pedido no banco de dados.
     * @param pedido O objeto Pedido a ser inserido.
     * @return O objeto Pedido com o ID gerado pelo banco.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public Pedido criarPedido(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (cliente_nome, data_pedido, valor_total, quantidade_itens, status, desconto) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, pedido.getClienteNome());
            stmt.setDate(2, pedido.getDataPedido());
            stmt.setBigDecimal(3, pedido.getValorTotal());
            stmt.setInt(4, pedido.getQuantidadeItens());
            stmt.setString(5, pedido.getStatus());
            stmt.setBigDecimal(6, pedido.getDesconto());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar pedido, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pedido.setId(generatedKeys.getInt(1)); // Define o ID gerado no objeto
                } else {
                    throw new SQLException("Falha ao criar pedido, nenhum ID obtido.");
                }
            }
        }
        return pedido;
    }

    /**
     * Busca um pedido pelo seu ID.
     * @param id O ID do pedido a ser buscado.
     * @return O objeto Pedido se encontrado, ou null caso contrário.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public Pedido buscarPedidoPorId(int id) throws SQLException {
        String sql = "SELECT id, cliente_nome, data_pedido, valor_total, quantidade_itens, status, desconto FROM pedidos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Pedido(
                        rs.getInt("id"),
                        rs.getString("cliente_nome"),
                        rs.getDate("data_pedido"),
                        rs.getBigDecimal("valor_total"),
                        rs.getInt("quantidade_itens"),
                        rs.getString("status"),
                        rs.getBigDecimal("desconto")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retorna todos os pedidos do banco de dados.
     * @return Uma lista de objetos Pedido.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public List<Pedido> listarTodosPedidos() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pedidos.add(new Pedido(
                    rs.getInt("id"),
                    rs.getString("cliente_nome"),
                    rs.getDate("data_pedido"),
                    rs.getBigDecimal("valor_total"),
                    rs.getInt("quantidade_itens"),
                    rs.getString("status"),
                    rs.getBigDecimal("desconto")
                ));
            }
        }
        return pedidos;
    }

    /**
     * Atualiza um pedido existente no banco de dados.
     * @param pedido O objeto Pedido com as informações atualizadas.
     * @return true se o pedido foi atualizado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public boolean atualizarPedido(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET cliente_nome = ?, data_pedido = ?, valor_total = ?, quantidade_itens = ?, status = ?, desconto = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pedido.getClienteNome());
            stmt.setDate(2, pedido.getDataPedido());
            stmt.setBigDecimal(3, pedido.getValorTotal());
            stmt.setInt(4, pedido.getQuantidadeItens());
            stmt.setString(5, pedido.getStatus());
            stmt.setBigDecimal(6, pedido.getDesconto());
            stmt.setInt(7, pedido.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Deleta um pedido do banco de dados pelo seu ID.
     * @param id O ID do pedido a ser deletado.
     * @return true se o pedido foi deletado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public boolean deletarPedido(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // --- Consultas com Funções de Agregação ---

    /**
     * Conta o número total de pedidos.
     * @return O número total de pedidos.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public long contarTotalPedidos() throws SQLException {
        String sql = "SELECT COUNT(*) AS total_pedidos FROM pedidos";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong("total_pedidos");
            }
        }
        return 0;
    }

    /**
     * Calcula o valor total de todos os pedidos.
     * @return O valor total de vendas.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public BigDecimal somarValorTotalPedidos() throws SQLException {
        String sql = "SELECT SUM(valor_total) AS soma_total FROM pedidos";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal("soma_total");
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcula a média do valor dos pedidos.
     * @return A média do valor dos pedidos.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public BigDecimal calcularMediaValorPedidos() throws SQLException {
        String sql = "SELECT AVG(valor_total) AS media_valor FROM pedidos";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal("media_valor");
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Encontra o pedido com o maior valor total.
     * @return O maior valor total de um pedido.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public BigDecimal encontrarMaxValorPedido() throws SQLException {
        String sql = "SELECT MAX(valor_total) AS max_valor FROM pedidos";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal("max_valor");
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Encontra o pedido com o menor valor total.
     * @return O menor valor total de um pedido.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public BigDecimal encontrarMinValorPedido() throws SQLException {
        String sql = "SELECT MIN(valor_total) AS min_valor FROM pedidos";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal("min_valor");
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Conta o número de pedidos por status.
     * @return Um mapa onde a chave é o status e o valor é a contagem de pedidos.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public Map<String, Long> contarPedidosPorStatus() throws SQLException {
        Map<String, Long> contagemPorStatus = new HashMap<>();
        String sql = "SELECT status, COUNT(*) AS count_status FROM pedidos GROUP BY status";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                contagemPorStatus.put(rs.getString("status"), rs.getLong("count_status"));
            }
        }
        return contagemPorStatus;
    }

    /**
     * Soma o valor total de pedidos por cliente.
     * @return Um mapa onde a chave é o nome do cliente e o valor é a soma total dos pedidos.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public Map<String, BigDecimal> somarValorPedidosPorCliente() throws SQLException {
        Map<String, BigDecimal> valorPorCliente = new HashMap<>();
        String sql = "SELECT cliente_nome, SUM(valor_total) AS total_gasto FROM pedidos GROUP BY cliente_nome";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                valorPorCliente.put(rs.getString("cliente_nome"), rs.getBigDecimal("total_gasto"));
            }
        }
        return valorPorCliente;
    }
}
