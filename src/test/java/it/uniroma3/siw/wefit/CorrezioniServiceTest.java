package it.uniroma3.siw.wefit;

import org.springframework.mock.web.MockMultipartFile;
import it.uniroma3.siw.wefit.exception.NotValidException;
import it.uniroma3.siw.wefit.model.Esercizio;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.repository.EsercizioRepository;
import it.uniroma3.siw.wefit.repository.EsercizioRoutineRepository;
import it.uniroma3.siw.wefit.repository.RoutineRepository;
import it.uniroma3.siw.wefit.service.EsercizioService;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import it.uniroma3.siw.wefit.service.RoutineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrezioniServiceTest {

    @Mock EsercizioRepository esercizi;
    @Mock EsercizioRoutineRepository collegamenti;
    @Mock RoutineRepository routines;
    @TempDir Path cartella;
    @InjectMocks EsercizioService esercizioService;
    @InjectMocks RoutineService routineService;

    @Test
    void conservaEsercizioEFotoQuandoUnaRoutineLoUsa() {
        Utente autore = new Utente();
        autore.setId(5L);
        Esercizio e = new Esercizio();
        e.setAutore(autore);
        e.setPercorsoImmagine("precedente.jpg");
        when(esercizi.findPerModifica(9L)).thenReturn(Optional.of(e));
        when(collegamenti.existsByEsercizio_Id(9L)).thenReturn(true);
        assertThrows(NotValidException.class, () -> esercizioService.deleteEsercizio(9L, autore));
        verify(esercizi, never()).delete(e);
        assertEquals("precedente.jpg", e.getPercorsoImmagine());
    }

    @Test
    void modificaConservaLaFotoFinoAllaSostituzione() throws Exception {
        ReflectionTestUtils.setField(esercizioService, "cartellaFoto", cartella.toString());
        Utente autore = new Utente();
        autore.setId(5L);
        Esercizio esistente = new Esercizio();
        esistente.setAutore(autore);
        esistente.setPercorsoImmagine("precedente.jpg");
        when(esercizi.findPerModifica(9L)).thenReturn(Optional.of(esistente));

        Esercizio modifica = new Esercizio();
        modifica.setNome("Nuovo nome");
        esercizioService.updateEsercizio(modifica, 9L, autore, null);
        assertEquals("precedente.jpg", esistente.getPercorsoImmagine());

        MockMultipartFile nuova = new MockMultipartFile("foto", "foto.png", "image/png", new byte[]{56, 78});
        esercizioService.updateEsercizio(modifica, 9L, autore, nuova);
        assertTrue(esistente.getPercorsoImmagine().endsWith(".png"));
        assertArrayEquals(nuova.getBytes(), Files.readAllBytes(cartella.resolve(esistente.getPercorsoImmagine())));
    }

    @Test
    void ricercaRoutineNellaQueryPaginata() {
        routineService.getAllRoutines(1, "forza");
        verify(routines).findByNomeContainingIgnoreCaseOrAutore_UsernameContainingIgnoreCase("forza", "forza", PageRequest.of(1, 2, Sort.by("id")));
    }

    @Test
    void inizialeAccountUsaIlPrimoCarattereCompleto() {
        Utente utente = new Utente();
        assertEquals("U", utente.getIniziale());
        utente.setUsername("alessia");
        assertEquals("A", utente.getIniziale());
        utente.setUsername("🏋️utente");
        assertEquals("🏋️", utente.getIniziale());
    }
}
