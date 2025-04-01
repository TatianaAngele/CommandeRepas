package com.commanderepas.dao;

import com.commanderepas.jdbc.Jdbc;
import com.commanderepas.model.Commande;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO 
{
    private Connection connection;

    public CommandeDAO() 
    {
        Jdbc jdbc = new Jdbc();
        this.connection = jdbc.getConnection();
    }

    public boolean ajouterCommande(Commande commande) 
    {
        String sql = "INSERT INTO commande (idClient) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {
            stmt.setInt(1, commande.getIdClient());
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) 
            {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) 
                {
                    commande.setIdCommande(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) 
        {
            System.out.println("Erreur : " + e.getMessage()); 
        }
        return false;
    }

    public boolean supprimerCommande(int idCommande) 
    {
        String sql = "DELETE FROM commande WHERE idCommande = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) 
        {
            stmt.setInt(1, idCommande);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.out.println("Erreur : " + e.getMessage());
        }
        return false;
    }

    public List<Commande> obtenirToutesLesCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) 
            {
                Commande commande = new Commande(); 
                commande.setIdCommande(rs.getInt("idCommande"));
                commande.setIdClient(rs.getInt("idClient"));
                commande.setDate(rs.getString("date"));
            }
        } catch (SQLException e) 
        {
            System.out.println("Erreur : " + e.getMessage());
        }
        return commandes;
    }
}
