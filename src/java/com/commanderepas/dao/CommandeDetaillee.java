package com.commanderepas.dao;

import java.time.LocalDateTime;
import java.util.List;

public class CommandeDetaillee 
{
    private int idCommande;
    private LocalDateTime dateCommande;
    private List<RepasCommandeDetail> repas;

    // Getters et setters
    public int getIdCommande() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }
    public List<RepasCommandeDetail> getRepas() { return repas; }
    public void setRepas(List<RepasCommandeDetail> repas) { this.repas = repas; }
}
