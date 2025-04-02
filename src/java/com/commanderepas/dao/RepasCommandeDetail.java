package com.commanderepas.dao;

public class RepasCommandeDetail 
{
    private int idRepas;
    private String nomRepas;
    private int prixRepas;
    private String description;
    private String photo;
    private int quantite;

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