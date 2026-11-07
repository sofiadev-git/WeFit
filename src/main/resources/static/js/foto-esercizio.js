const campoFoto = document.querySelector("#foto");
const anteprima = document.querySelector(".foto-anteprima");
let urlFoto;

campoFoto?.addEventListener("change", () => {
    if (urlFoto) URL.revokeObjectURL(urlFoto);
    anteprima.hidden = true;
    anteprima.removeAttribute("src");
    campoFoto.setCustomValidity("");

    const file = campoFoto.files[0];
    if (!file) return;
    if (file.size > 4 * 1024 * 1024) {
        campoFoto.setCustomValidity("Scegli una foto fino a 4 MB.");
        campoFoto.reportValidity();
        return;
    }
    urlFoto = URL.createObjectURL(file);
    anteprima.src = urlFoto;
    anteprima.hidden = false;
});
