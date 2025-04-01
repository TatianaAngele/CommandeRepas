package com.commanderepas.dao;

import com.commanderepas.jdbc.Jdbc;
import com.commanderepas.model.Repas;
import java.sql.*;

public class RepasDAO 
{
    private Connection connection;

    public RepasDAO() 
    {
        Jdbc jdbc = new Jdbc(); 
        this.connection = jdbc.getConnection();
    }

    public boolean ajouterRepas(Repas repas) 
    {
        String sql = "INSERT INTO repas (nomRepas, prix, description, photo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, repas.getNomRepas());
            stmt.setInt(2, repas.getPrixRepas());
            stmt.setString(3, repas.getDescription());
            stmt.setString(4, repas.getPhoto());
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) 
            {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) 
                {
                    repas.setIdRepas(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) 
        {
            System.out.println("Erreur : " + e.getMessage()); 
        }
        return false;
    }

    public boolean modifierRepas(Repas repas)
    {
        String sql = "UPDATE repas SET nomRepas = ?, prix = ?, description = ?, photo = ? WHERE idRepas = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, repas.getNomRepas());
            stmt.setInt(2, repas.getPrixRepas());
            stmt.setString(3, repas.getDescription());
            stmt.setString(4, repas.getPhoto());
            stmt.setInt(5, repas.getIdRepas());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e)
        {
            System.out.println("Erreur : " + e.getMessage()); 
        }
        return false;
    }

    public boolean supprimerRepas(int idRepas) 
    {
        String sql = "DELETE FROM repas WHERE idRepas = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, idRepas);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.out.println("Erreur : " + e.getMessage()); 
        }
        return false;
    }
}