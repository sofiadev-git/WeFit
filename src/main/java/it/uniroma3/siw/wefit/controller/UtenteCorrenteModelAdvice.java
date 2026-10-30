package it.uniroma3.siw.wefit.controller;

import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.service.UtenteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class UtenteCorrenteModelAdvice {

    private final UtenteService utenteService;

    public UtenteCorrenteModelAdvice(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @ModelAttribute("utente")
    public Utente utenteCorrente(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return utenteService.findUtenteByUsername(authentication.getName());
    }
}
