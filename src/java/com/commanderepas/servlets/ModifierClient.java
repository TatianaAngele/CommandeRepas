package com.commanderepas.servlets;

import com.commanderepas.dao.ClientDAO;
import com.commanderepas.model.Client;
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

@WebServlet("/client/update/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 50    // 50 MB
)
public class ModifierClient extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "client_uploads";
    private ClientDAO clientDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        clientDAO = new ClientDAO();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
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

            // Récupérer les données du formulaire
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String numeroStr = request.getParameter("numero");
            String email = request.getParameter("email");
            String motDePasse = request.getParameter("motDePasse");
            String adresse = request.getParameter("adresse");
            Part filePart = request.getPart("photo");

            // Créer l'objet Client à mettre à jour
            Client client = new Client();
            client.setIdClient(idClient);

            // Mettre à jour les champs fournis
            if (nom != null) client.setNom(nom);
            if (prenom != null) client.setPrenom(prenom);
            if (numeroStr != null) {
                try {
                    client.setNumero(Integer.parseInt(numeroStr));
                } catch (NumberFormatException e) {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                        "Le numéro doit être un entier valide");
                    return;
                }
            }
            if (email != null) client.setEmail(email);
            if (motDePasse != null) client.setMotDePasse(motDePasse);
            if (adresse != null) client.setAdresse(adresse);

            // Traitement de la photo
            if (filePart != null && filePart.getSize() > 0) {
                
                // Sauvegarder la nouvelle photo
                String fileName = saveUploadedFile(filePart);
                client.setPhoto(fileName);
            }

            // Mettre à jour le client via le DAO
            boolean success = clientDAO.modifierClient(client);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(),
                    new SuccessResponse("Client mis à jour avec succès", idClient));
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Aucun client trouvé avec l'ID: " + idClient);
            }

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Erreur serveur: " + e.getMessage());
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

    private void deleteFile(String fileName) {
        String filePath = getServletContext().getRealPath("") + File.separator + 
                         UPLOAD_DIRECTORY + File.separator + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
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