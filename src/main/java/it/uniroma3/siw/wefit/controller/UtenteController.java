package it.uniroma3.siw.wefit.controller;

import it.uniroma3.siw.wefit.exception.NotValidException;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    /*Il post viene gestito da spring security ancora prima di raggiungere il controller, più sicuro. */

    @GetMapping("/register")
    public String getRegisterPage(Model model){
        model.addAttribute("credenziali",new Utente()); //mi serve per moter raccoglier i dati comodamente nel post mapping
        return "register";
    }

    /*@RequestParam è utile quando ricevi un parametro singolo che non appartiene all'oggetto principale della form.*/


    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("credenziali") Utente utente, BindingResult bindingResult){
        /*ModelAttribute fa un operazione di binding tra i dati del form (inviati nella richiesta HTTP) e l'oggetto java definito nel get,
        * quindi usa i dati della richiesta per popolare l'oggetto java.*/
        //nta: la striga di ModelAttribute deve corrispondere a quella di addAttribute nel GET

        /*Valid invece serve per il Bean Validation, ovvero controlla quelle annotazione come @Notblan @Min ecc, ma non gli errori come runTimeExeption.
        * Se ci sono errori di quel tipo nei campi inseriti, allora non si lascia la pagina*/
        if(bindingResult.hasErrors()){
            return "/register";
        }
        try{
            utenteService.registraUtente(utente.getUsername(), utente.getPassword());
            return "redirect:/login?registered=true";
        }
        catch (NotValidException e){
            bindingResult.reject("utente.notValid", e.getMessage()); // permette di trattare un'eccezione come un'eccezione del Bean Validation, così da non finire su una pagina d'errore
            return "register";
        }

    }

    /*Il CSRF token è un valore casuale che il server genera
        e inserisce nella form per verificare che una richiesta POST arrivi davvero dalla pagina legittima del tuo sito.*/


}
