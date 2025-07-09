package com.example.mindfuture.dto;



import java.util.Date;

import lombok.Data;

@Data
public class EmocionRespuestaDTO {
    private String emocionDetectada;
    private Integer nivelEstres;
    private String recomendacion;
    private Date fechaRegistro;
}
