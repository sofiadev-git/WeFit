package it.uniroma3.siw.wefit.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RoutineFormUpdateDto(

        /* qui non viene passato l'id dell'eser e*/
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
