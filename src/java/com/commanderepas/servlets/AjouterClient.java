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

@WebServlet("/client/add")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 50    // 50 MB
)
public class AjouterClient extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "client_uploads";
    private ClientDAO clientDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        clientDAO = new ClientDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        try {
            // Récupérer les données du formulaire
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String numeroStr = request.getParameter("numero");
            String email = request.getParameter("email");
            String motDePasse = request.getParameter("motDePasse");
            String adresse = request.getParameter("adresse");
            Part filePart = request.getPart("photo");

            // Validation des champs obligatoires
            if (nom == null || nom.isEmpty() ||
                prenom == null || prenom.isEmpty() ||
                numeroStr == null || numeroStr.isEmpty() ||
                email == null || email.isEmpty() ||
                motDePasse == null || motDePasse.isEmpty()) {
                
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Les champs nom, prenom, numero, email et motDePasse sont obligatoires");
                return;
            }

            // Conversion et validation du numéro
            int numero;
            try {
                numero = Integer.parseInt(numeroStr);
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Le numéro doit être un entier valide");
                return;
            }

            // Validation de l'email
            if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Format d'email invalide");
                return;
            }

            // Création de l'objet Client
            Client client = new Client();
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setNumero(numero);
            client.setEmail(email);
            client.setMotDePasse(motDePasse);
            client.setAdresse(adresse != null ? adresse : "");

            // Traitement de la photo
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = saveUploadedFile(filePart);
                client.setPhoto(fileName);
            } else {
                client.setPhoto(null);
            }

            // Ajout du client via le DAO
            boolean success = clientDAO.ajouterClient(client);

            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(response.getWriter(),
                    new SuccessResponse("Client ajouté avec succès", client.getIdClient()));
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur lors de l'ajout du client");
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