// Durante la digitazione filtra la pagina corrente. Il form cerca in tutto il catalogo.
document.addEventListener('DOMContentLoaded', () => {
    const input = document.querySelector('[data-routine-filter]');
    const cards = [...document.querySelectorAll('.routine-card')];
    const count = document.querySelector('[data-visible-count]');
    const label = document.querySelector('[data-count-label]');
    const empty = document.querySelector('[data-no-results]');
    if (!input || cards.length === 0) return;

    // La ricerca nella navbar invia /routines?q=... ; usiamo lo stesso filtro.
    const params = new URLSearchParams(window.location.search);
    if (params.has('q')) input.value = params.get('q') || '';

    const normalize = text => (text || '').toLocaleLowerCase('it').normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '').trim();

    function filterRoutines() {
        const query = normalize(input.value);
        let visible = 0;
        cards.forEach(card => {
            const searchable = normalize(`${card.dataset.name || ''} ${card.dataset.author || ''}`);
            const show = searchable.includes(query);
            card.hidden = !show;
            if (show) visible++;
        });
        if (count) count.textContent = String(visible);
        if (label) label.textContent = visible === 1 ? 'routine disponibile' : 'routine disponibili';
        if (empty) empty.hidden = visible > 0;
    }

    input.addEventListener('input', filterRoutines);
    if (input.value === "") filterRoutines();
});
