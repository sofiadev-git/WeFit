package it.uniroma3.siw.wefit;

import it.uniroma3.siw.wefit.model.Routine;
import it.uniroma3.siw.wefit.model.Utente;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginazioneTemplateTest {

    @Test
    void leMieRoutineGeneraIlLinkSuccessivoSenzaRicerca() {
        String html = render("paginaMieRoutine", "/area-personale/routines", 0, 3, null);
        assertTrue(html.contains("href=\"/area-personale/routines?page=1\""));
        assertTrue(html.contains("Pagina 1 di 3"));
    }

    @Test
    void laRicercaDelCatalogoSiMantieneInEntrambeLeDirezioni() {
        String html = render("paginaListaRoutines", "/routines", 1, 3, "forza");
        assertTrue(html.contains("href=\"/routines?page=0&amp;q=forza\""));
        assertTrue(html.contains("href=\"/routines?page=2&amp;q=forza\""));
    }

    @Test
    void ultimaPaginaHaSoloIlLinkPrecedente() {
        String html = render("paginaMieRoutine", "/area-personale/routines", 2, 3, null);
        assertTrue(html.contains("href=\"/area-personale/routines?page=1\""));
        assertFalse(html.contains("href=\"/area-personale/routines?page=3\""));
    }

    private String render(String template, String uri, int numero, int totale, String ricerca) {
        MockServletContext servlet = new MockServletContext();
        MockHttpServletRequest request = new MockHttpServletRequest(servlet, "GET", uri);
        if (ricerca != null) {
            request.setParameter("q", ricerca);
        }
        var exchange = JakartaServletWebApplication.buildApplication(servlet)
                .buildExchange(request, new MockHttpServletResponse());

        Utente utente = new Utente();
        utente.setId(1L);
        utente.setUsername("alessia");
        Routine routine = new Routine();
        routine.setId(7L);
        routine.setNome("Forza");
        routine.setAutore(utente);

        Map<String, Object> modello = new HashMap<>();
        modello.put("utente", utente);
        modello.put("routines", new PageImpl<>(List.of(routine), PageRequest.of(numero, 1), totale));
        modello.put("bookmarkedIds", Set.of());
        if (ricerca != null) {
            modello.put("ricerca", ricerca);
        }
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine.process(template, new WebContext(exchange, Locale.ITALIAN, modello));
    }
}
