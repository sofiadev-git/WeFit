package it.uniroma3.siw.wefit.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EsercizioFormDto(
        @NotBlank
        String nome,


        String descrizione,

        @NotBlank
        String categoria


) {
}
