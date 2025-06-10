package com.example.mindfuture.dto;

public class EmocionDTO {
    private String emocionDetectada;
    private Integer nivelEstres;
    private String recomendacion;
    private String fuente; // Debe coincidir con el enum: "voz", "texto", "biometria"
    private String emailUsuario; // Para identificar al usuario desde el frontend

    // Getters y Setters
    public String getEmocionDetectada() {
        return emocionDetectada;
    }

    public void setEmocionDetectada(String emocionDetectada) {
        this.emocionDetectada = emocionDetectada;
    }

    public Integer getNivelEstres() {
        return nivelEstres;
    }

    public void setNivelEstres(Integer nivelEstres) {
        this.nivelEstres = nivelEstres;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
}
