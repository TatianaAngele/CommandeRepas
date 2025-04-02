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

@WebServlet("/repas/add")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 1024 * 1024 * 10,       // 10 MB
    maxRequestSize = 1024 * 1024 * 50     // 50 MB
)
public class AjouterRepas extends HttpServlet {
    
    private static final String UPLOAD_DIRECTORY = "uploads";
    private RepasDAO repasDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        repasDAO = new RepasDAO(); // Initialisation du DAO
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        
        try {
            // Récupérer les parties du formulaire
            String nomRepas = request.getParameter("nomRepas");
            String prixStr = request.getParameter("prix");
            String description = request.getParameter("description");
            Part filePart = request.getPart("photo");

            // Validation des champs obligatoires
            if (nomRepas == null || nomRepas.isEmpty() || 
                prixStr == null || prixStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getWriter(), 
                    new ErrorResponse("Les champs nomRepas et prix sont obligatoires"));
                return;
            }

            int prix;
            try {
                prix = Integer.parseInt(prixStr);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getWriter(), 
                    new ErrorResponse("Le prix doit être un nombre entier"));
                return;
            }

            // Créer l'objet Repas
            Repas repas = new Repas();
            repas.setNomRepas(nomRepas);
            repas.setPrixRepas(prix);
            repas.setDescription(description);

            // Traitement du fichier photo
            if (filePart != null && filePart.getSize() > 0) {
                // Préparer le répertoire d'upload
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                // Générer un nom de fichier unique
                String originalFileName = getFileName(filePart);
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + fileExtension;

                // Sauvegarder le fichier
                try (InputStream fileContent = filePart.getInputStream()) {
                    Files.copy(fileContent, new File(uploadDir, fileName).toPath(), 
                           StandardCopyOption.REPLACE_EXISTING);
                }

                repas.setPhoto("/uploads/" + fileName);
            }

            // Ajouter le repas dans la base de données
            boolean success = repasDAO.ajouterRepas(repas);

            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(response.getWriter(), 
                    new SuccessResponse("Repas ajouté avec succès", repas.getIdRepas()));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(response.getWriter(), 
                    new ErrorResponse("Erreur lors de l'ajout du repas"));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), 
                new ErrorResponse("Erreur serveur: " + e.getMessage()));
        }
    }

    // Méthode utilitaire pour extraire le nom du fichier
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