package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:postgresql://localhost:5432/NOME_BD"; //assumindo localhost...
    private static final String USER = "<SEU USUÁRIO DO POSTGRESQL>";
    private static final String PASSWORD = "SUA SENHA";

    public static Connection getConnection() throws SQLException {
        
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL não encontrado.");
            throw new SQLException("Erro ao carregar o driver JDBC.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
