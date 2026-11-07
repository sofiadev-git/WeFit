const menuAccount = document.querySelector(".account-menu");
if (menuAccount) {
    document.addEventListener("pointerdown", event => {
        if (menuAccount.open && !menuAccount.contains(event.target)) menuAccount.open = false;
    });
    menuAccount.addEventListener("keydown", event => {
        if (event.key === "Escape" && menuAccount.open) {
            menuAccount.open = false;
            menuAccount.querySelector("summary").focus();
        }
    });
    menuAccount.addEventListener("focusout", event => {
        if (event.relatedTarget && !menuAccount.contains(event.relatedTarget)) menuAccount.open = false;
    });
}
