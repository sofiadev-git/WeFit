package it.uniroma3.siw.wefit.dto;
import java.util.List;

public record RoutineDetailsDto (
        Long id,
        String nome,
        String descrizione,
        String nomeUtente,
        Integer numeroEsercizi,
        boolean bookmarked,
        List<EsercizioDellaDto> esercizi

) {
}
