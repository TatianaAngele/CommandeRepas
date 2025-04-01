package com.commanderepas.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Jdbc 
{
    private static final String URL = "jdbc:mysql://localhost:3306/commanderepas";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static Connection getConnection() 
    {
        if (connection == null) 
        {
            try 
            {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion réussie à la base de données.");
            } catch (ClassNotFoundException | SQLException e) 
            {
                System.out.println("Erreur de connexion a la base de donnees : " + e.getMessage());
            }
        }
        return connection;
    }
}
