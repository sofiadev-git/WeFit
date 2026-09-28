package it.uniroma3.siw.wefit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Utente {
    public static final String DEFAULT_ROLE = "USER";  // accesso limitato
    public static final String ADMIN_ROLE = "ADMIN"; // accesso completo

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @OneToMany(mappedBy = "autore", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<Routine> routine = new HashSet<>();

    @OneToMany(mappedBy = "autore")
    private List<Esercizio> esercizi = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_bookmarked_routines",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "routine_id")
    )
    private Set<Routine> bookmarkedRoutines = new HashSet<>();

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = false)
    private String password;

    @Column(nullable = false)
    private String role =DEFAULT_ROLE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Routine> getRoutine() {
        return routine;
    }

    public void setRoutine(Set<Routine> routine) {
        this.routine = routine;
    }

    public List<Esercizio> getEsercizi() {
        return esercizi;
    }

    public void setEsercizi(List<Esercizio> esercizi) {
        this.esercizi = esercizi;
    }

    public Set<Routine> getBookmarkedRoutines() {
        return bookmarkedRoutines;
    }

    public void setBookmarkedRoutines(Set<Routine> bookmarkedRoutines) {
        this.bookmarkedRoutines = bookmarkedRoutines;
    }
}
