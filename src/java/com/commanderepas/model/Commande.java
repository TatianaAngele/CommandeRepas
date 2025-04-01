package com.commanderepas.model;

public class Commande 
{
    private int idCommande; 
    private int idClient; 
    private String date; 

    public Commande() {}

    public int getIdCommande() 
    {
        return idCommande;
    }

    public int getIdClient() 
    {
        return idClient;
    }

    public String getDate() 
    {
        return date;
    }

    public void setIdCommande(int idCommande) 
    {
        this.idCommande = idCommande;
    }

    public void setIdClient(int idClient) 
    {
        this.idClient = idClient;
    }

    public void setDate(String date) 
    {
        this.date = date;
    }
}
