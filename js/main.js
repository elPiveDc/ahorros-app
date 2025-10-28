// main.js - carga nav y tema
document.addEventListener("DOMContentLoaded", async () => {
  // cargar nav reutilizable
  const res = await fetch("nav.html");
  const navHtml = await res.text();
  document
    .querySelectorAll("[data-insert-nav]")
    .forEach((el) => (el.innerHTML = navHtml));
  // activar handlers nav
  document
    .querySelectorAll("#nav-reusable [data-theme-toggle]")
    .forEach((btn) => {
      btn.addEventListener("click", toggleTema);
    });
  // set active nav by path
  const path = location.pathname.split("/").pop() || "index.html";
  document.querySelectorAll(".nav-link").forEach((a) => {
    if (a.getAttribute("href") === path) a.classList.add("active");
  });
  // aplicar tema guardado
  const tema = localStorage.getItem("tema") || "dark";
  document.documentElement.setAttribute("data-tema", tema);
});

function toggleTema() {
  const cur =
    document.documentElement.getAttribute("data-tema") === "light"
      ? "dark"
      : "light";
  document.documentElement.setAttribute("data-tema", cur);
  localStorage.setItem("tema", cur);
}
