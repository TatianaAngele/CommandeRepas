package com.commanderepas.servlets;

import com.commanderepas.dao.CommandeDAO;
import com.commanderepas.dao.CommandeRepasDAO;
import com.commanderepas.jdbc.Jdbc;
import com.commanderepas.model.Commande;
import com.commanderepas.model.CommandeRepas;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/validerCommande")
public class ValiderCommande extends HttpServlet {

    private CommandeDAO commandeDAO;
    private CommandeRepasDAO commandeRepasDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        commandeDAO = new CommandeDAO();
        commandeRepasDAO = new CommandeRepasDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        try {
            // Désérialiser le JSON
            CommandeRequest commandeRequest = objectMapper.readValue(request.getReader(), CommandeRequest.class);

            // Validation des données
            if (commandeRequest.getIdClient() <= 0 || commandeRequest.getRepas() == null || commandeRequest.getRepas().isEmpty()) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Données de commande invalides");
                return;
            }

            // Démarrer une transaction
            Connection connection = new Jdbc().getConnection();
            try {
                connection.setAutoCommit(false);

                // 1. Créer la commande
                Commande commande = new Commande();
                commande.setIdClient(commandeRequest.getIdClient());

                if (!commandeDAO.ajouterCommande(commande)) {
                    sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la création de la commande");
                    connection.rollback();
                    return;
                }

                // 2. Ajouter les repas de la commande
                for (RepasCommande repasCmd : commandeRequest.getRepas()) {
                    CommandeRepas commandeRepas = new CommandeRepas();
                    commandeRepas.setIdCommande(commande.getIdCommande());
                    commandeRepas.setIdRepas(repasCmd.getIdRepas());
                    commandeRepas.setQuantite(repasCmd.getQuantite());

                    if (!commandeRepasDAO.ajouterCommandeRepas(commandeRepas)) {
                        sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Erreur lors de l'ajout du repas à la commande");
                        connection.rollback();
                        return;
                    }
                }

                // Tout s'est bien passé, valider la transaction
                connection.commit();

                response.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(response.getWriter(),
                    new SuccessResponse("Commande validée avec succès", commande.getIdCommande()));

            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur lors de la validation de la commande: " + e.getMessage());
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Erreur lors du rétablissement de autoCommit: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Erreur serveur: " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(message));
    }

    // Classes pour le JSON
    public static class CommandeRequest {
        private int idClient;
        private List<RepasCommande> repas;

        // Getters et setters
        public int getIdClient() { return idClient; }
        public void setIdClient(int idClient) { this.idClient = idClient; }
        public List<RepasCommande> getRepas() { return repas; }
        public void setRepas(List<RepasCommande> repas) { this.repas = repas; }
    }

    public static class RepasCommande {
        private int idRepas;
        private int quantite;

        // Getters et setters
        public int getIdRepas() { return idRepas; }
        public void setIdRepas(int idRepas) { this.idRepas = idRepas; }
        public int getQuantite() { return quantite; }
        public void setQuantite(int quantite) { this.quantite = quantite; }
    }

    private static class SuccessResponse {
        public String message;
        public int idCommande;

        public SuccessResponse(String message, int idCommande) {
            this.message = message;
            this.idCommande = idCommande;
        }
    }

    private static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}