package com.commanderepas.model;

public class Repas 
{
    private int idRepas; 
    private String nomRepas; 
    private int prixRepas; 
    private String description; 
    private String photo; 

    public Repas() {}

    public int getIdRepas() 
    {
        return idRepas;
    }

    public String getNomRepas() 
    {
        return nomRepas;
    }

    public int getPrixRepas() 
    {
        return prixRepas;
    }

    public String getDescription() 
    {
        return description;
    }

    public String getPhoto() 
    {
        return photo;
    }

    public void setIdRepas(int idRepas) 
    {
        this.idRepas = idRepas;
    }

    public void setNomRepas(String nomRepas) 
    {
        this.nomRepas = nomRepas;
    }

    public void setPrixRepas(int prixRepas) 
    {
        this.prixRepas = prixRepas;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public void setPhoto(String photo) 
    {
        this.photo = photo;
    }
}
