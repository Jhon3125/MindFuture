package com.example.mindfuture.dto;

public class EmocionDTO {
    private String emocionDetectada;
    private Integer nivelEstres;
    private String recomendacion;

    // Getters
    public String getEmocionDetectada() {
        return emocionDetectada;
    }

    public Integer getNivelEstres() {
        return nivelEstres;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    // Setters
    public void setEmocionDetectada(String emocionDetectada) {
        this.emocionDetectada = emocionDetectada;
    }

    public void setNivelEstres(Integer nivelEstres) {
        this.nivelEstres = nivelEstres;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }
}
