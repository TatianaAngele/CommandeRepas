package com.commanderepas.servlets;

import com.commanderepas.dao.CommandeDAO;
import com.commanderepas.model.Commande;
import com.commanderepas.model.CommandeRepas;
import com.commanderepas.model.Repas;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/commande/client/*")
public class CommandeClient extends HttpServlet {

    private CommandeDAO commandeDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        commandeDAO = new CommandeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        try {
            // Extraire l'ID du client depuis l'URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "ID du client manquant dans l'URL");
                return;
            }

            String[] splits = pathInfo.split("/");
            if (splits.length < 2) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Format d'URL invalide");
                return;
            }

            int idClient;
            try {
                idClient = Integer.parseInt(splits[1]);
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "ID du client doit être un nombre");
                return;
            }

            // Récupérer les commandes avec les détails des repas
            List<Commande> commandes = commandeDAO.getCommandesByClient(idClient);
            Map<Integer, CommandeDetaillee> commandesDetaillees = new HashMap<>();

            // Pour chaque commande, récupérer les repas associés
            for (Commande commande : commandes) {
                CommandeDetaillee commandeDetaillee = new CommandeDetaillee();
                commandeDetaillee.setIdCommande(commande.getIdCommande());
                commandeDetaillee.setDateCommande(commande.getDate());
                commandeDetaillee.setRepas(new ArrayList<>());

                List<CommandeRepas> commandeRepasList = commandeDAO.getRepasByCommande(commande.getIdCommande());
                for (CommandeRepas commandeRepas : commandeRepasList) {
                    Repas repas = commandeDAO.getRepasById(commandeRepas.getIdRepas());
                    
                    RepasCommandeDetail repasDetail = new RepasCommandeDetail();
                    repasDetail.setIdRepas(repas.getIdRepas());
                    repasDetail.setNomRepas(repas.getNomRepas());
                    repasDetail.setPrixRepas(repas.getPrixRepas());
                    repasDetail.setDescription(repas.getDescription());
                    repasDetail.setPhoto(repas.getPhoto());
                    repasDetail.setQuantite(commandeRepas.getQuantite());

                    commandeDetaillee.getRepas().add(repasDetail);
                }

                commandesDetaillees.put(commande.getIdCommande(), commandeDetaillee);
            }

            if (commandesDetaillees.isEmpty()) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Aucune commande trouvée pour ce client");
                return;
            }

            // Formater la réponse
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), new ArrayList<>(commandesDetaillees.values()));

        } catch (SQLException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Erreur serveur: " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(message));
    }

    // Classes pour la réponse
    public static class CommandeDetaillee {
        private int idCommande;
        private String dateCommande;
        private List<RepasCommandeDetail> repas;

        // Getters et setters
        public int getIdCommande() { return idCommande; }
        public void setIdCommande(int idCommande) { this.idCommande = idCommande; }
        public String getDateCommande() { return dateCommande; }
        public void setDateCommande(String dateCommande) { this.dateCommande = dateCommande; }
        public List<RepasCommandeDetail> getRepas() { return repas; }
        public void setRepas(List<RepasCommandeDetail> repas) { this.repas = repas; }
    }

    public static class RepasCommandeDetail {
        private int idRepas;
        private String nomRepas;
        private int prixRepas;
        private String description;
        private String photo;
        private int quantite;

        // Getters et setters
        public int getIdRepas() { return idRepas; }
        public void setIdRepas(int idRepas) { this.idRepas = idRepas; }
        public String getNomRepas() { return nomRepas; }
        public void setNomRepas(String nomRepas) { this.nomRepas = nomRepas; }
        public int getPrixRepas() { return prixRepas; }
        public void setPrixRepas(int prixRepas) { this.prixRepas = prixRepas; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPhoto() { return photo; }
        public void setPhoto(String photo) { this.photo = photo; }
        public int getQuantite() { return quantite; }
        public void setQuantite(int quantite) { this.quantite = quantite; }
    }

    private static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}