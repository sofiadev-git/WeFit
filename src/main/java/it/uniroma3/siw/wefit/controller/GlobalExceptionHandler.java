package it.uniroma3.siw.wefit.controller;


import it.uniroma3.siw.wefit.exception.NotAllowedException;
import it.uniroma3.siw.wefit.exception.NotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
/* gli errori 404 e 403 generati dalle eccezioni riportano alla pagina principale con un messaggio di errore personalizzato.*/
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFotoTroppoGrande(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "La foto supera il limite. Scegli un file fino a 4 MB.");
        return "redirect:/";
    }

    @ExceptionHandler(NotAllowedException.class)
    public String handleNotAllowed(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Non hai l'autorizzazione necessaria per questa azione"
        );

        return "redirect:/";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Questa azione non ha riportato nessun risultato"
        );

        return "redirect:/";
    }
}
