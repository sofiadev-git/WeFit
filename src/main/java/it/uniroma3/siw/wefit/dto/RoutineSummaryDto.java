package it.uniroma3.siw.wefit.dto;



/* Quando React fa la sua richiesta http, il RestController (API)
 comunica con il Service (che recupera l'intera entità e crea il Dto con le informazioni necessarie)
 e il REstController restituisce le informazioni sotto forma di JSON.*/


/* Questo Dto contiene le informazioni necessarie per mostrare le informazioni di base delle routines
 nella pagina della lista delle routines. Viene passato anche il nome utente per eventualmente usarlo come filtro di ricerca.*/

/*Un Dto viene implementato come un Java Record poiché:
* - Immutabile, assicura che durante la conversione in Json, non sia possibile che (anche solo erroneamente)
*   i dati vengano modificati o sovrascritti.
* - I record sono supportati nativamente da Jackson, libreria per trasformare oggetti in JSON. */

public record RoutineSummaryDto(
    Long id,
    String nome,
    String nomeUtente,
    Integer numeroEsercizi,
    boolean bookmarked
    )
{}
