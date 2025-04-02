package com.commanderepas.servlets;

import com.commanderepas.dao.RepasDAO;
import com.commanderepas.model.Repas;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@WebServlet("/repas/update/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 50    // 50 MB
)
public class ModifierRepas extends HttpServlet {
    
    private static final String UPLOAD_DIRECTORY = "uploads";
    private RepasDAO repasDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        repasDAO = new RepasDAO();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        
        try {
            // Extraire l'ID du repas depuis l'URL
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

            // Récupérer les données du formulaire
            String nomRepas = request.getParameter("nomRepas");
            String prixStr = request.getParameter("prix");
            String description = request.getParameter("description");
            Part filePart = request.getPart("photo");

            // Créer l'objet Repas à mettre à jour
            Repas repas = new Repas();
            repas.setIdRepas(idRepas);
            
            // Valider et mettre à jour les champs
            if (nomRepas == null || nomRepas.isEmpty()) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Le nom du repas est obligatoire");
                return;
            }
            repas.setNomRepas(nomRepas);
            
            if (prixStr == null || prixStr.isEmpty()) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Le prix est obligatoire");
                return;
            }
            
            try {
                repas.setPrixRepas(Integer.parseInt(prixStr));
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Le prix doit être un nombre entier");
                return;
            }
            
            repas.setDescription(description != null ? description : "");

            // Traitement de la photo
            if (filePart != null && filePart.getSize() > 0) {
                // Sauvegarder la nouvelle photo
                String fileName = saveUploadedFile(filePart);
                repas.setPhoto(fileName);
            } else {
                // Garder la photo existante si aucune nouvelle n'est fournie
                repas.setPhoto(""); // Ou récupérer la photo existante depuis la base si nécessaire
            }

            // Appeler la méthode DAO pour la mise à jour
            boolean success = repasDAO.modifierRepas(repas);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(), 
                    new SuccessResponse("Repas mis à jour avec succès", idRepas));
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du repas");
            }

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur: " + e.getMessage());
        }
    }

    private String saveUploadedFile(Part filePart) throws IOException {
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        String originalFileName = getFileName(filePart);
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, new File(uploadDir, fileName).toPath(), 
                     StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "";
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