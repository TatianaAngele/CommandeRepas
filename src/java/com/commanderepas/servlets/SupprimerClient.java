package com.commanderepas.servlets;

import com.commanderepas.dao.ClientDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/client/delete/*")
public class SupprimerClient extends HttpServlet {

    private ClientDAO clientDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        clientDAO = new ClientDAO();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
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

            // Supprimer le client via le DAO
            boolean success = clientDAO.supprimerClient(idClient);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(),
                    new SuccessResponse("Client supprimé avec succès", idClient));
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Aucun client trouvé avec l'ID: " + idClient);
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

    // Classes pour les réponses JSON
    private static class SuccessResponse {
        public String message;
        public int id;

        public SuccessResponse(String message, int id) {
            this.message = message;
            this.id = id;
        }
    }

    private static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}