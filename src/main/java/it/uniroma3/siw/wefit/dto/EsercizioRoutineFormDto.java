package it.uniroma3.siw.wefit.dto;
/* questo dto serve per inserire i dati del form, è un'alternativa più sicura a @ModleAttribute*/
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EsercizioRoutineFormDto(
        //non serve routineId perché già lo si ha tramite @PathVariable

        @NotNull
        Long esercizioId,

        @NotNull
        @Min(1)
        @Max(20)
        Integer numeroSet,

        @NotNull
        @Min(1)
        @Max((1000))
        Integer ripetizioni
) {

}
