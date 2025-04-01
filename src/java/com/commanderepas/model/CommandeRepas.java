package com.commanderepas.model;

public class CommandeRepas 
{
    private int idCommande; 
    private int idRepas; 
    private int quantite; 

    public CommandeRepas() {}

    public int getIdCommande() 
    {
        return idCommande;
    }

    public int getIdRepas() 
    {
        return idRepas;
    }

    public int getQuantite() 
    {
        return quantite;
    }

    public void setIdCommande(int idCommande) 
    {
        this.idCommande = idCommande;
    }

    public void setIdRepas(int idRepas) 
    {
        this.idRepas = idRepas;
    }

    public void setQuantite(int quantite) 
    {
        this.quantite = quantite;
    }
}
