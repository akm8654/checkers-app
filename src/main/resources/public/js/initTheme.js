function initTheme() {
    console.log("initTheme Called.");

    var pinkThemeSelected = (localStorage.getItem('theme')) !== null;
    pinkThemeSelected ? document.documentElement.setAttribute('theme', 'pink')
        : document.documentElement.removeAttribute('theme');
}