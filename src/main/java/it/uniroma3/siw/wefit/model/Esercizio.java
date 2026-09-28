package it.uniroma3.siw.wefit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Esercizio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String categoria;

    private String descrizione;

    @Column(nullable = false)
    private boolean esercizioDefault = false;
    /* Per evitare che gli utenti creino molteplici istanze di stessi esercizi, quelli più comuni,
    * gli esercizi creati da un admin sono settati su true, e sono disponibili per tutti gli utenti.
    * Non viene usata nel html una cosa come  <input type = "hidden" name="isEsercizioDefault" value="true",
    * perché può essere modificato con lo strumento ispeziona. Avviene con springSecurity*/

    @ManyToOne
    @JoinColumn(name="utente_id")
    private Utente autore;

    @OneToMany(mappedBy = "esercizio")
    private List<EsercizioRoutine> eserciziRoutines = new ArrayList<>();


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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<EsercizioRoutine> getEserciziRoutines() {

        return eserciziRoutines;
    }

    public void setEserciziRoutines(List<EsercizioRoutine> eserciziRoutines) {
        this.eserciziRoutines = eserciziRoutines;
    }

    public boolean isEsercizioDefault() {
        return esercizioDefault;
    }

    public void setEsercizioDefault(boolean esercizioDefault) {
        this.esercizioDefault = esercizioDefault;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente autore) {
        this.autore = autore;
    }
}
