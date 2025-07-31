package com.example;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PedidoApp extends JFrame {

    private final PedidoDAO pedidoDAO;

    // Componentes da GUI
    private JTextField idField, clienteNomeField, valorTotalField, quantidadeItensField, statusField, descontoField;
    private JTable pedidoTable;
    private DefaultTableModel tableModel;
    private JButton createButton, readButton, updateButton, deleteButton, clearButton;
    private JTextArea aggregationResultsArea;

    public PedidoApp() {
        super("Gerenciador de Pedidos");
        pedidoDAO = new PedidoDAO();

        initComponents();
        setupLayout();
        addEventHandlers();
        loadPedidosIntoTable(); // Carrega os pedidos ao iniciar a aplicação
    }

    private void initComponents() {
        // Campos de entrada
        idField = new JTextField(5);
        idField.setEditable(false); // ID é gerado automaticamente
        clienteNomeField = new JTextField(20);
        valorTotalField = new JTextField(10);
        quantidadeItensField = new JTextField(5);
        statusField = new JTextField(10);
        descontoField = new JTextField(5);

        // Botões CRUD
        createButton = new JButton("Criar");
        readButton = new JButton("Buscar por ID");
        updateButton = new JButton("Atualizar");
        deleteButton = new JButton("Deletar");
        clearButton = new JButton("Limpar Campos");

        // Tabela de Pedidos
        String[] columnNames = {"ID", "Cliente", "Data", "Valor Total", "Qtd. Itens", "Status", "Desconto"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células da tabela não editáveis
            }
        };
        pedidoTable = new JTable(tableModel);
        pedidoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha
        pedidoTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && pedidoTable.getSelectedRow() != -1) {
                // Preenche os campos de texto com os dados da linha selecionada
                int selectedRow = pedidoTable.getSelectedRow();
                idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                clienteNomeField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                // data_pedido e desconto podem precisar de formatação específica se fossem Date/BigDecimal no campo
                // Por simplicidade aqui, pegamos como String
                valorTotalField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                quantidadeItensField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                statusField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                descontoField.setText(tableModel.getValueAt(selectedRow, 6).toString());
            }
        });

        // Área de resultados de agregação
        aggregationResultsArea = new JTextArea(10, 30);
        aggregationResultsArea.setEditable(false);
        aggregationResultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10)); // Layout principal

        // Painel de entrada de dados
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Dados do Pedido"));
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Cliente:"));
        inputPanel.add(clienteNomeField);
        inputPanel.add(new JLabel("Valor Total:"));
        inputPanel.add(valorTotalField);
        inputPanel.add(new JLabel("Qtd. Itens:"));
        inputPanel.add(quantidadeItensField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusField);
        inputPanel.add(new JLabel("Desconto:"));
        inputPanel.add(descontoField);
        inputPanel.add(new JLabel("Data (YYYY-MM-DD):")); // Apenas para informação, usaremos LocalDate.now()
        inputPanel.add(new JLabel(LocalDate.now().toString())); // Exibe a data atual como padrão

        // Painel de botões CRUD
        JPanel crudButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        crudButtonPanel.add(createButton);
        crudButtonPanel.add(readButton);
        crudButtonPanel.add(updateButton);
        crudButtonPanel.add(deleteButton);
        crudButtonPanel.add(clearButton);

        // Painel para a tabela
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Lista de Pedidos"));
        tablePanel.add(new JScrollPane(pedidoTable), BorderLayout.CENTER);

        // Painel para resultados de agregação
        JPanel aggregationPanel = new JPanel(new BorderLayout());
        aggregationPanel.setBorder(BorderFactory.createTitledBorder("Resultados de Agregação"));
        aggregationPanel.add(new JScrollPane(aggregationResultsArea), BorderLayout.CENTER);

        // Adicionar painéis ao JFrame principal
        add(inputPanel, BorderLayout.WEST);
        add(crudButtonPanel, BorderLayout.NORTH); // Botões CRUD na parte superior central
        add(tablePanel, BorderLayout.CENTER);
        add(aggregationPanel, BorderLayout.EAST); // Resultados de agregação à direita

        // Configurações da janela
        setSize(1200, 700); // Tamanho da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela na tela
    }

    private void addEventHandlers() {
        createButton.addActionListener(e -> createPedido());
        readButton.addActionListener(e -> readPedido());
        updateButton.addActionListener(e -> updatePedido());
        deleteButton.addActionListener(e -> deletePedido());
        clearButton.addActionListener(e -> clearFields());

        // Adiciona um evento para carregar dados na tabela quando a aplicação iniciar
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent windowEvent) {
                loadPedidosIntoTable();
                performAggregations(); // Executa as agregações ao iniciar
            }
        });
    }

    private void clearFields() {
        idField.setText("");
        clienteNomeField.setText("");
        valorTotalField.setText("");
        quantidadeItensField.setText("");
        statusField.setText("");
        descontoField.setText("");
        pedidoTable.clearSelection();
    }

    private void loadPedidosIntoTable() {
        tableModel.setRowCount(0); // Limpa a tabela
        try {
            List<Pedido> pedidos = pedidoDAO.listarTodosPedidos();
            for (Pedido pedido : pedidos) {
                Vector<Object> row = new Vector<>();
                row.add(pedido.getId());
                row.add(pedido.getClienteNome());
                row.add(pedido.getDataPedido().toString()); // Converte Date para String
                row.add(pedido.getValorTotal().toPlainString()); // Converte BigDecimal para String
                row.add(pedido.getQuantidadeItens());
                row.add(pedido.getStatus());
                row.add(pedido.getDesconto().toPlainString()); // Converte BigDecimal para String
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createPedido() {
        try {
            String clienteNome = clienteNomeField.getText();
            BigDecimal valorTotal = new BigDecimal(valorTotalField.getText());
            int quantidadeItens = Integer.parseInt(quantidadeItensField.getText());
            String status = statusField.getText();
            BigDecimal desconto = new BigDecimal(descontoField.getText());
            Date dataPedido = Date.valueOf(LocalDate.now()); // Data atual

            Pedido novoPedido = new Pedido(clienteNome, dataPedido, valorTotal, quantidadeItens, status, desconto);
            pedidoDAO.criarPedido(novoPedido);
            JOptionPane.showMessageDialog(this, "Pedido criado com ID: " + novoPedido.getId(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadPedidosIntoTable();
            clearFields();
            performAggregations();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro de formato numérico: Verifique Valor Total, Quantidade de Itens e Desconto.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar pedido: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void readPedido() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite o ID do pedido para buscar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = Integer.parseInt(idText);
            Pedido pedido = pedidoDAO.buscarPedidoPorId(id);
            if (pedido != null) {
                clienteNomeField.setText(pedido.getClienteNome());
                valorTotalField.setText(pedido.getValorTotal().toPlainString());
                quantidadeItensField.setText(String.valueOf(pedido.getQuantidadeItens()));
                statusField.setText(pedido.getStatus());
                descontoField.setText(pedido.getDesconto().toPlainString());
                JOptionPane.showMessageDialog(this, "Pedido encontrado.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Pedido com ID " + id + " não encontrado.", "Não Encontrado", JOptionPane.WARNING_MESSAGE);
                clearFields();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido. Por favor, digite um número inteiro.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar pedido: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePedido() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela ou digite o ID para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = Integer.parseInt(idText);
            Pedido pedidoExistente = pedidoDAO.buscarPedidoPorId(id);

            if (pedidoExistente == null) {
                JOptionPane.showMessageDialog(this, "Pedido com ID " + id + " não encontrado para atualização.", "Não Encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Atualiza o objeto existente com os novos dados dos campos
            pedidoExistente.setClienteNome(clienteNomeField.getText());
            pedidoExistente.setValorTotal(new BigDecimal(valorTotalField.getText()));
            pedidoExistente.setQuantidadeItens(Integer.parseInt(quantidadeItensField.getText()));
            pedidoExistente.setStatus(statusField.getText());
            pedidoExistente.setDesconto(new BigDecimal(descontoField.getText()));
            // A data do pedido geralmente não é atualizada, mas se fosse, precisaria de um campo de data na GUI

            boolean updated = pedidoDAO.atualizarPedido(pedidoExistente);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Pedido atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadPedidosIntoTable();
                clearFields();
                performAggregations();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao atualizar pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro de formato numérico: Verifique ID, Valor Total, Quantidade de Itens e Desconto.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar pedido: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePedido() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela ou digite o ID para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar o pedido ID " + idText + "?", "Confirmar Deleção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idText);
                boolean deleted = pedidoDAO.deletarPedido(id);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Pedido deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    loadPedidosIntoTable();
                    clearFields();
                    performAggregations();
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao deletar pedido. Pedido não encontrado?", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID inválido. Por favor, digite um número inteiro.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar pedido: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performAggregations() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("--- Resultados de Agregação ---\n");
            sb.append(String.format("Total de Pedidos: %d\n", pedidoDAO.contarTotalPedidos()));
            sb.append(String.format("Soma Total dos Valores: R$%.2f\n", pedidoDAO.somarValorTotalPedidos()));
            sb.append(String.format("Média do Valor dos Pedidos: R$%.2f\n", pedidoDAO.calcularMediaValorPedidos()));
            sb.append(String.format("Maior Valor de Pedido: R$%.2f\n", pedidoDAO.encontrarMaxValorPedido()));
            sb.append(String.format("Menor Valor de Pedido: R$%.2f\n", pedidoDAO.encontrarMinValorPedido()));

            sb.append("\nContagem de Pedidos por Status:\n");
            Map<String, Long> pedidosPorStatus = pedidoDAO.contarPedidosPorStatus();
            pedidosPorStatus.forEach((status, count) -> sb.append(String.format("  - %s: %d\n", status, count)));

            sb.append("\nSoma dos Valores de Pedidos por Cliente:\n");
            Map<String, BigDecimal> valorPorCliente = pedidoDAO.somarValorPedidosPorCliente();
            valorPorCliente.forEach((cliente, totalGasto) -> sb.append(String.format("  - %s: R$%.2f\n", cliente, totalGasto)));

        } catch (SQLException ex) {
            sb.append("\nErro ao realizar agregações: ").append(ex.getMessage());
        } finally {
            aggregationResultsArea.setText(sb.toString());
        }
    }

    public static void main(String[] args) {
        // Garante que a GUI seja executada na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new PedidoApp().setVisible(true);
        });
    }
}
