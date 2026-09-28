package it.uniroma3.siw.wefit.dto;

public record EsercizioDellaDto (
        Long id,
        String nomeEsercizio,
        String descrizioneEsercizio,
        Integer numeroSet,
        Integer ripetizioni
) {
}
