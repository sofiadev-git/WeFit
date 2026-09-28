package it.uniroma3.siw.wefit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    private String descrizione;

    /*ManyToOne lato proprietario*/
    @ManyToOne
    @JoinColumn(name = "autore_id",nullable = false)
    private Utente autore;

    @OneToMany(mappedBy = "routine",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EsercizioRoutine> eserciziDellaRoutine = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente autore) {
        this.autore = autore;
    }

    public List<EsercizioRoutine> getEserciziDellaRoutine() {
        return eserciziDellaRoutine;
    }

    public void setEserciziDellaRoutine(List<EsercizioRoutine> eserciziDellaRoutine) {
        this.eserciziDellaRoutine = eserciziDellaRoutine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Routine other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
