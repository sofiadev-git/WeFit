document.querySelectorAll(".private-list-page").forEach(pagina => {

    const ricerca = pagina.querySelector("[data-exercise-search]");
    const categoria = pagina.querySelector("[data-exercise-category]");
    const righe = pagina.querySelectorAll("[data-exercise-row]");
    const nessunRisultato = pagina.querySelector("[data-no-filter-results]");

    if (!ricerca || !categoria || righe.length === 0) {
        return;
    }

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
