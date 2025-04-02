package com.commanderepas.servlets;

import com.commanderepas.dao.RepasDAO;
import com.commanderepas.model.Repas;
import com.fasterxml.jackson.databind.ObjectMapper; // Pour convertir en JSON 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListeRepas extends HttpServlet
{
    @Override 
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        RepasDAO repasDAO = new RepasDAO(); 
        List<Repas> repas = new ArrayList<>(); 
        ObjectMapper objectMapper = new ObjectMapper(); 
        
        repas = repasDAO.listeRepas();
        String repasJson = objectMapper.writeValueAsString(repas);
        
        response.getWriter().write(repasJson);
    }
}
