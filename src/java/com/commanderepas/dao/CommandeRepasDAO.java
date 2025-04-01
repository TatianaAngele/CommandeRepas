package com.commanderepas.dao;

import com.commanderepas.jdbc.Jdbc;
import com.commanderepas.model.CommandeRepas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeRepasDAO 
{
    private Connection connection;

    public CommandeRepasDAO() 
    {
        Jdbc jdbc = new Jdbc(); 
        this.connection = jdbc.getConnection();
    }

    public boolean ajouterCommandeRepas(CommandeRepas commandeRepas) 
    {
        String sql = "INSERT INTO commanderepas (idCommande, idRepas, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, commandeRepas.getIdCommande());
            stmt.setInt(2, commandeRepas.getIdRepas());
            stmt.setInt(3, commandeRepas.getQuantite());
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) 
            {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) 
                {
                    commandeRepas.setIdCommandeRepas(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) 
        {
            System.out.println("Erreur : " + e.getMessage()); 
        }
        return false;
    }

    public boolean supprimerCommandeRepas(int idCommandeRepas) 
    {
        String sql = "DELETE FROM commanderepas WHERE idCommandeRepas = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCommandeRepas);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage()); 
        }
        return false;
    }

    public List<String> obtenirRepasParCommande(int idCommande) {
        List<String> repasList = new ArrayList<>();
        String sql = "SELECT r.nomRepas, cr.quantite FROM commanderepas cr " +
                     "INNER JOIN repas r ON cr.idRepas = r.idRepas " +
                     "WHERE cr.idCommande = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                repasList.add(rs.getString("nomRepas") + " (Quantité: " + rs.getInt("quantite") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return repasList;
    }

    public List<String> obtenirDetailsCommandeClient(int idCommande) 
    {
        List<String> details = new ArrayList<>();
        String sql = "SELECT c.nom, c.prenom, c.email, cm.date, r.nomRepas, cr.quantite " +
                     "FROM commande cm " +
                     "INNER JOIN client c ON cm.idClient = c.idClient " +
                     "INNER JOIN commanderepas cr ON cm.idCommande = cr.idCommande " +
                     "INNER JOIN repas r ON cr.idRepas = r.idRepas " +
                     "WHERE cm.idCommande = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) 
        {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) 
            {
                String detail = "Client: " + rs.getString("nom") + " " + rs.getString("prenom") + " (" + rs.getString("email") + ")\n" +
                                "Date: " + rs.getString("date") + "\n" +
                                "Repas: " + rs.getString("nomRepas") + " (Quantité: " + rs.getInt("quantite") + ")";
                details.add(detail);
            }
        } catch (SQLException e)
        {
            System.out.println("Erreur : " + e.getMessage()); 
        }
        return details;
    }
}
