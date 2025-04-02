package com.commanderepas.servlets;

import com.commanderepas.dao.RepasDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/repas/delete/*")
public class SupprimerRepas extends HttpServlet {

    private RepasDAO repasDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        repasDAO = new RepasDAO();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        try {
            // Extraire l'ID du repas à supprimer depuis l'URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "ID du repas manquant dans l'URL");
                return;
            }

            String[] splits = pathInfo.split("/");
            if (splits.length < 2) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Format d'URL invalide");
                return;
            }

            int idRepas;
            try {
                idRepas = Integer.parseInt(splits[1]);
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "ID du repas doit être un nombre");
                return;
            }

            // Supprimer le repas en utilisant la méthode DAO
            boolean success = repasDAO.supprimerRepas(idRepas);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(),
                        new SuccessResponse("Repas supprimé avec succès", idRepas));
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Aucun repas trouvé avec l'ID: " + idRepas);
            }

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur: " + e.getMessage());
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