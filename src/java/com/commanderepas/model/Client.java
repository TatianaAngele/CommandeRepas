package com.commanderepas.model;

public class Client 
{
    private int idClient; 
    private String nom; 
    private String prenom; 
    private int numero; 
    private String email; 
    private String motDePasse; 
    private String adresse; 
    private String photo; 

    public Client() {}

    public int getIdClient() 
    {
        return idClient;
    }

    public String getNom() 
    {
        return nom;
    }

    public String getPrenom() 
    {
        return prenom;
    }

    public int getNumero() 
    {
        return numero;
    }

    public String getEmail() 
    {
        return email;
    }

    public String getMotDePasse() 
    {
        return motDePasse;
    }

    public String getAdresse() 
    {
        return adresse;
    }

    public String getPhoto() 
    {
        return photo;
    }

    public void setIdClient(int idClient) 
    {
        this.idClient = idClient;
    }

    public void setNom(String nom) 
    {
        this.nom = nom;
    }

    public void setPrenom(String prenom) 
    {
        this.prenom = prenom;
    }

    public void setNumero(int numero) 
    {
        this.numero = numero;
    }

    public void setEmail(String email) 
    {
        this.email = email;
    }

    public void setMotDePasse(String motDePasse)
    {
        this.motDePasse = motDePasse;
    }

    public void setAdresse(String adresse)
    {
        this.adresse = adresse;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }   
}
