package com.commanderepas.dao;

import com.commanderepas.jdbc.Jdbc;
import com.commanderepas.model.Commande;
import com.commanderepas.model.CommandeRepas;
import com.commanderepas.model.Repas;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public List<Commande> getCommandesByClient(int idClient) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE idClient = ? ORDER BY date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idClient);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Commande commande = new Commande();
                commande.setIdCommande(rs.getInt("idCommande"));
                commande.setIdClient(rs.getInt("idClient"));
                commande.setDate(rs.getString("date"));
                commandes.add(commande);
            }
        }
        return commandes;
    }

    public List<CommandeRepas> getRepasByCommande(int idCommande) throws SQLException {
        List<CommandeRepas> commandeRepasList = new ArrayList<>();
        String sql = "SELECT * FROM commanderepas WHERE idCommande = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CommandeRepas commandeRepas = new CommandeRepas();
                commandeRepas.setIdCommandeRepas(rs.getInt("idCommandeRepas"));
                commandeRepas.setIdCommande(rs.getInt("idCommande"));
                commandeRepas.setIdRepas(rs.getInt("idRepas"));
                commandeRepas.setQuantite(rs.getInt("quantite"));
                commandeRepasList.add(commandeRepas);
            }
        }
        return commandeRepasList;
    }

    public Repas getRepasById(int idRepas) throws SQLException {
        String sql = "SELECT * FROM repas WHERE idRepas = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idRepas);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Repas repas = new Repas();
                repas.setIdRepas(rs.getInt("idRepas"));
                repas.setNomRepas(rs.getString("nomRepas"));
                repas.setPrixRepas(rs.getInt("prix"));
                repas.setDescription(rs.getString("description"));
                repas.setPhoto(rs.getString("photo"));
                return repas;
            }
        }
        return null;
    }
}
