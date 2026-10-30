/* Aggiorna solo l'anteprima visuale. La scelta effettiva viene inviata tramite il campo del form. */
document.addEventListener("DOMContentLoaded", () => {
    const selezione = document.querySelector("[data-wf-exercise-select]");
    const nome = document.querySelector("[data-wf-selected-name]");
    const categoria = document.querySelector("[data-wf-selected-category]");
    if (!selezione || !nome || !categoria) return;

    function aggiornaAnteprima() {
        const opzione = selezione.selectedOptions[0];
        nome.textContent = opzione?.value ? opzione.dataset.wfName : "Nessun esercizio selezionato";
        categoria.textContent = opzione?.value ? `${opzione.dataset.wfCategory} · ${opzione.dataset.wfKind}` : "Scegli un esercizio dalla lista";
    }

    selezione.addEventListener("change", aggiornaAnteprima);
    aggiornaAnteprima();
});
