package it.uniroma3.siw.wefit.controller;

import it.uniroma3.siw.wefit.dto.EsercizioFormDto;
import it.uniroma3.siw.wefit.model.Esercizio;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.service.EsercizioService;
import it.uniroma3.siw.wefit.service.UtenteService;
import jakarta.validation.Valid;
import it.uniroma3.siw.wefit.exception.NotValidException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class EsercizioController {

    @Autowired
    EsercizioService esercizioService;
    @Autowired
    UtenteService utenteService;

    @GetMapping("/esercizi")
    public String visualizzaEsercizi(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String categoria, @RequestParam(defaultValue = "") String q, Authentication authentication, Model model) {
        Utente utente = getUtenteCorrente(authentication);
        if (page < 0) {
            page = 0;
        }
        model.addAttribute("utente", utente);
        model.addAttribute("categoriaSelezionata", categoria.trim());
        model.addAttribute("ricerca", q.trim());
        model.addAttribute("categorie", esercizioService.getCategorieDefault());
        model.addAttribute("eserciziDefault", esercizioService.getEserciziDefault(page, categoria.trim(), q.trim()));

        return "paginaEsercizi";
    }

    @GetMapping("/esercizi/{id}")
    public String visualizzaEsercizio(@PathVariable Long id, Authentication authentication,Model model){
        Utente utente= getUtenteCorrente(authentication);
        Esercizio esercizio= esercizioService.getEsercizioDefaultOrAutore(id,utente);
        model.addAttribute("utente", utente);
        model.addAttribute("esercizio",esercizio);
        return "paginaDettaglioEsercizio";
    }

    @GetMapping("/esercizi/new")
    public String compilaNuovoEsercizio(Authentication authentication,Model model){
        Utente utente = getUtenteCorrente(authentication);
        model.addAttribute("utente", utente);
        model.addAttribute("esercizio",new EsercizioFormDto("","",""));
        return "formEsercizio";
    }

    @PostMapping("/esercizi")
    public String creaEsercizio(@Valid @ModelAttribute("esercizio") EsercizioFormDto dto, BindingResult bindingResult, @RequestParam(required = false) MultipartFile foto, Authentication authentication, Model model){
        Utente utente = getUtenteCorrente(authentication);
        if (!bindingResult.hasErrors()) {
            try {
                Esercizio esercizio = new Esercizio();
                esercizio.setNome(dto.nome());
                esercizio.setDescrizione(dto.descrizione());
                esercizio.setCategoria(dto.categoria());
                Esercizio e= esercizioService.createEsercizio(esercizio,utente,foto);
                return "redirect:/esercizi/" + e.getId();
            } catch (NotValidException e) {
                bindingResult.reject("foto.nonValida", e.getMessage());
            }
        }
        model.addAttribute("utente", utente);
        return "formEsercizio";
    }

    @GetMapping("/esercizi/{id}/edit")
    public String modificaEsercizio(@PathVariable Long id, Authentication authentication,Model model){
        Utente utente = getUtenteCorrente(authentication);
        Esercizio esercizio = esercizioService.getEsercizioAutore(id,utente);
        model.addAttribute("utente", utente);
        model.addAttribute("esercizio", new EsercizioFormDto(esercizio.getNome(),esercizio.getDescrizione(),esercizio.getCategoria()));
        model.addAttribute("esercizioId",id);
        model.addAttribute("nomeFoto", esercizio.getPercorsoImmagine());
        model.addAttribute("isDefault", esercizio.isEsercizioDefault());
        return "formModificaEsercizio";
    }

    @PostMapping("/esercizi/{id}")
    public String aggiornaEsercizio(@PathVariable Long id, @Valid @ModelAttribute("esercizio") EsercizioFormDto dto, BindingResult bindingResult, @RequestParam(required = false) MultipartFile foto, Authentication authentication,Model model){ // "esercizio ha ora i nuovi valori
        Utente utente = getUtenteCorrente(authentication);
        model.addAttribute("esercizioId", id);
        if (!bindingResult.hasErrors()) {
            try {
                Esercizio esercizio = new Esercizio();
                esercizio.setNome(dto.nome());
                esercizio.setDescrizione(dto.descrizione());
                esercizio.setCategoria(dto.categoria());
                Esercizio e = esercizioService.updateEsercizio(esercizio,id,utente,foto);
                return "redirect:/esercizi/" + e.getId();
            } catch (NotValidException e) {
                bindingResult.reject("foto.nonValida", e.getMessage());
            }
        }
        Esercizio esercizioOriginale = esercizioService.getEsercizioAutore(id,utente);
        model.addAttribute("nomeFoto", esercizioOriginale.getPercorsoImmagine());
        model.addAttribute("isDefault", esercizioOriginale.isEsercizioDefault());
        model.addAttribute("utente", utente);
        return "formModificaEsercizio";
    }

    @PostMapping("/esercizi/{id}/delete")
    public String eliminaEsercizio(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes){
        Utente utente = getUtenteCorrente(authentication);
        try {
            esercizioService.deleteEsercizio(id,utente);
        } catch (NotValidException e) {
            redirectAttributes.addFlashAttribute("erroreEliminazione", e.getMessage());
            return "redirect:/esercizi/" + id;
        }
        return "redirect:/esercizi";
    }


    private Utente getUtenteCorrente(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        return utenteService.findUtenteByUsername(authentication.getName());
    }

}
