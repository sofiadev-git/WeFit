package it.uniroma3.siw.wefit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class EsercizioRoutine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    @JoinColumn(name = "esercizio_id", nullable = false)
    private Esercizio esercizio;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @NotNull
    @Min(1)
    @Max(1000)
    @Column(nullable = false)
    private Integer ripetizioni;

    @NotNull
    @Min(1)
    @Max(20)
    @Column(nullable = false)
    private Integer numeroSet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Esercizio getEsercizio() {
        return esercizio;
    }

    public void setEsercizio(Esercizio esercizio) {
        this.esercizio = esercizio;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public Integer getRipetizioni() {
        return ripetizioni;
    }

    public void setRipetizioni(Integer ripetizioni) {
        this.ripetizioni = ripetizioni;
    }

    public Integer getNumeroSet() {
        return numeroSet;
    }

    public void setNumeroSet(Integer numeroSet) {
        this.numeroSet = numeroSet;
    }
}
