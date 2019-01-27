package fr.wildcodeschool.airbusproject.Model;

public class ProfilModel {

    private String prénom;
    private String nom;
    private String compétence;

    public ProfilModel(String prénom, String nom, String compétence, String photo, String ville) {
        this.prénom = prénom;
        this.nom = nom;
        this.compétence = compétence;
        this.photo = photo;
        this.ville = ville;
    }

    private String photo;
    private String ville;


    public ProfilModel() {
    }

    public String getPrénom() {
        return prénom;
    }

    public void setPrénom(String prénom) {
        this.prénom = prénom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCompétence() {
        return compétence;
    }

    public void setCompétence(String compétence) {
        this.compétence = compétence;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }
}

