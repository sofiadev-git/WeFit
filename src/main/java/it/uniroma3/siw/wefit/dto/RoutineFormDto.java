package it.uniroma3.siw.wefit.dto;

import jakarta.validation.constraints.NotBlank;

public record RoutineFormDto(
        @NotBlank
        String nome,

        /*L'autore non viene passato per form*/


        String descrizione

        /*l'aggiunta degli esercizi avviene in un secondo momento rispetto alla creazione della routine.*/
) {
}
