document.addEventListener("DOMContentLoaded", () => {
    const ricerca = document.querySelector("[data-my-routines-search]");
    const righe = document.querySelectorAll("[data-my-routine-row]");
    const nessunRisultato = document.querySelector("[data-my-routines-empty-filter]");

    if (!ricerca) return;

    ricerca.addEventListener("input", () => {
        const filtro = ricerca.value.trim().toLocaleLowerCase();
        let visibili = 0;
        righe.forEach(riga => {
            const testo = `${riga.dataset.name || ""} ${riga.dataset.description || ""}`.toLocaleLowerCase();
            riga.hidden = !testo.includes(filtro);
            if (!riga.hidden) visibili++;
        });
        if (nessunRisultato) nessunRisultato.hidden = visibili !== 0;
    });
});
