function ChangeTheme() {
    console.log("Switching Theme")

    let theme = localStorage.getItem('theme');
    if(!theme){
        theme = 'pink';
        localStorage.setItem('theme', 'pink');
        document.documentElement.setAttribute('theme', 'pink')
    } else {
        localStorage.removeItem('theme');
        document.documentElement.removeAttribute('theme')
    }
}