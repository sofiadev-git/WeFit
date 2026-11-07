document.querySelectorAll(".private-list-page").forEach(pagina => {

    const ricerca = pagina.querySelector("[data-exercise-search]");
    const categoria = pagina.querySelector("[data-exercise-category]");
    const righe = pagina.querySelectorAll("[data-exercise-row]");
    const nessunRisultato = pagina.querySelector("[data-no-filter-results]");

    if (!ricerca || !categoria) {
        return;
    }

    preparaFiltroGruppo(categoria);

    function filtraEsercizi() {
        const testo = ricerca.value.trim().toLowerCase();
        const gruppo = categoria.value;

        let visibili = 0;

        righe.forEach(riga => {
            const nome = (riga.dataset.name || "").toLowerCase();
            const categoriaEsercizio = riga.dataset.category || "";

            const corrispondeNome = nome.includes(testo);
            const corrispondeCategoria =
                gruppo === "" || categoriaEsercizio === gruppo;

            const mostra = corrispondeNome && corrispondeCategoria;

            riga.hidden = !mostra;

            if (mostra) {
                visibili++;
            }
        });

        if (nessunRisultato) {
            nessunRisultato.hidden = visibili > 0;
        }
    }

    ricerca.addEventListener("input", filtraEsercizi);
    categoria.addEventListener("change", filtraEsercizi);
});

function preparaFiltroGruppo(select) {
    const filtro = document.createElement("div");
    filtro.className = "group-picker";
    const pulsante = document.createElement("button");
    pulsante.type = "button";
    pulsante.className = "group-picker-toggle";

    const elenco = document.createElement("div");
    elenco.className = "group-picker-panel";
    elenco.id = select.id + "-opzioni";
    elenco.setAttribute("role", "radiogroup");
    elenco.setAttribute("aria-label", "Gruppo muscolare");
    elenco.hidden = true;
    pulsante.setAttribute("aria-controls", elenco.id);
    pulsante.setAttribute("aria-expanded", "false");

    const opzioni = new Map();
    function sincronizza() {
        const scelta = opzioni.get(select.value);
        if (!scelta) return;
        scelta.checked = true;
        pulsante.textContent = select.selectedOptions[0].textContent;
        pulsante.setAttribute("aria-label", "Gruppo muscolare: " + pulsante.textContent);
    }

    function chiudi(ripristinaFocus) {
        elenco.hidden = true;
        pulsante.setAttribute("aria-expanded", "false");
        if (ripristinaFocus) pulsante.focus();
    }

    for (const option of select.options) {
        if (opzioni.has(option.value)) continue;
        const riga = document.createElement("label");
        const radio = document.createElement("input");
        radio.type = "radio";
        radio.name = elenco.id;
        radio.value = option.value;
        opzioni.set(option.value, radio);
        riga.append(radio, document.createTextNode(option.textContent));
        elenco.append(riga);
        radio.addEventListener("change", () => {
            select.value = radio.value;
            select.dispatchEvent(new Event("change", {bubbles: true}));
        });
        radio.addEventListener("click", event => {
            if (event.detail > 0) chiudi(true);
        });
    }

    function apri() {
        elenco.hidden = false;
        pulsante.setAttribute("aria-expanded", "true");
        opzioni.get(select.value)?.focus();
    }

    pulsante.addEventListener("click", () => elenco.hidden ? apri() : chiudi(false));
    pulsante.addEventListener("keydown", event => {
        if (event.key === "ArrowDown") {
            event.preventDefault();
            apri();
        }
    });
    elenco.addEventListener("keydown", event => {
        if (event.key === "Escape" || event.key === "Enter") {
            event.preventDefault();
            chiudi(true);
        }
    });
    document.addEventListener("pointerdown", event => {
        if (!elenco.hidden && !filtro.contains(event.target)) chiudi(false);
    });
    filtro.addEventListener("focusout", event => {
        if (!filtro.contains(event.relatedTarget)) chiudi(false);
    });
    select.addEventListener("change", sincronizza);
    select.after(filtro);
    filtro.append(pulsante, elenco);
    select.hidden = true;
    sincronizza();
}
