/* Ricerca e filtri della lista: nessuna richiesta al server quando cambia il filtro. */
document.addEventListener("DOMContentLoaded", () => {
    const catalogo = document.querySelector("[data-wf-catalog]");
    if (!catalogo) return;

    const ricerca = catalogo.querySelector("[data-wf-search]");
    const filtri = [...catalogo.querySelectorAll("[data-wf-filter]")];
    const esercizi = [...catalogo.querySelectorAll("[data-wf-exercise]")];
    const conteggio = catalogo.querySelector("[data-wf-count]");
    const nessunRisultato = catalogo.querySelector("[data-wf-empty]");
    let filtroAttivo = "tutti";

    const normalizza = valore => (valore || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

    function aggiornaCatalogo() {
        const testo = normalizza(ricerca.value.trim());
        let visibili = 0;

        esercizi.forEach(esercizio => {
            const tipo = esercizio.dataset.wfKind;
            const contenuto = normalizza(`${esercizio.dataset.wfName} ${esercizio.dataset.wfCategory}`);
            const mostra = (filtroAttivo === "tutti" || filtroAttivo === tipo) && contenuto.includes(testo);
            esercizio.hidden = !mostra;
            if (mostra) visibili++;
        });

        conteggio.textContent = `${visibili} ${visibili === 1 ? "esercizio" : "esercizi"}`;
        nessunRisultato.hidden = visibili !== 0;
    }

    filtri.forEach(bottone => {
        bottone.addEventListener("click", () => {
            filtroAttivo = bottone.dataset.wfFilter;
            filtri.forEach(filtro => filtro.setAttribute("aria-pressed", String(filtro === bottone)));
            aggiornaCatalogo();
        });
    });

    ricerca.addEventListener("input", aggiornaCatalogo);
    aggiornaCatalogo();
});
