package com.commanderepas.dao;

import com.commanderepas.jdbc.Jdbc;
import com.commanderepas.model.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO 
{
    private Connection connection;

    public ClientDAO() 
    {
        Jdbc jdbc = new Jdbc(); 
        this.connection = jdbc.getConnection();
    }

    public boolean ajouterClient(Client client) 
    {
        String sql = "INSERT INTO client (nom, prenom, numero, email, motDePasse, adresse, photo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setInt(3, client.getNumero());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getMotDePasse());
            stmt.setString(6, client.getAdresse());
            stmt.setString(7, client.getPhoto());
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) 
            {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) 
                {
                    client.setIdClient(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage()); 
        }
        return false;
    }

    public boolean modifierClient(Client client) 
    {
        String sql = "UPDATE client SET nom = ?, prenom = ?, numero = ?, email = ?, motDePasse = ?, adresse = ?, photo = ? WHERE idClient = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) 
        {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setInt(3, client.getNumero());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getMotDePasse());
            stmt.setString(6, client.getAdresse());
            stmt.setString(7, client.getPhoto());
            stmt.setInt(8, client.getIdClient());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.out.println(e.getMessage()); 
        }
        return false;
    }

    public boolean supprimerClient(int idClient) 
    {
        String sql = "DELETE FROM client WHERE idClient = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) 
        {
            stmt.setInt(1, idClient);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.out.println(e.getMessage()); 
        }
        return false;
    }

    public Client obtenirClient(int idClient) 
    {
        String sql = "SELECT * FROM client WHERE idClient = ?";
        Client client = new Client(); 
        try (PreparedStatement stmt = connection.prepareStatement(sql)) 
        {
            stmt.setInt(1, idClient);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) 
            {
                client.setIdClient(rs.getInt("idClient")); 
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setNumero(rs.getInt("numero"));
                client.setEmail(rs.getString("email"));
                client.setMotDePasse(rs.getString("motDePasse"));
                client.setAdresse(rs.getString("adresse"));
                client.setPhoto(rs.getString("photo"));
                return client;
            }
        } catch (SQLException e) 
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Client authentifierClient(String email, String motDePasse) 
    {
        String sql = "SELECT * FROM client WHERE email = ? AND motDePasse = ?;";
        Client client = new Client(); 
        try (PreparedStatement stmt = connection.prepareStatement(sql)) 
        {
            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) 
            {
                client.setIdClient(rs.getInt("idClient")); 
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setNumero(rs.getInt("numero"));
                client.setEmail(rs.getString("email"));
                client.setMotDePasse(rs.getString("motDePasse"));
                client.setAdresse(rs.getString("adresse"));
                client.setPhoto(rs.getString("photo"));
                return client;
            }
        } catch (SQLException e) 
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public List<Client> obtenirTousLesClients() 
    {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM client";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) 
            {
                Client client = new Client(); 
                client.setIdClient(rs.getInt("idClient")); 
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setNumero(rs.getInt("numero"));
                client.setEmail(rs.getString("email"));
                client.setMotDePasse(rs.getString("motDePasse"));
                client.setAdresse(rs.getString("adresse"));
                client.setPhoto(rs.getString("photo"));
                
                clients.add(client);
            }
        } catch (SQLException e) 
        {
            System.out.println(e.getMessage());
        }
        return clients;
    }
}
