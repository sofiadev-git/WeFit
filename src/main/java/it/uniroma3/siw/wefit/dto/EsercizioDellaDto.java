package it.uniroma3.siw.wefit.dto;

public record EsercizioDellaDto (
        Long id,
        String nomeEsercizio,
        String descrizioneEsercizio,
        String categoria,
        Integer numeroSet,
        Integer ripetizioni
) {
}
