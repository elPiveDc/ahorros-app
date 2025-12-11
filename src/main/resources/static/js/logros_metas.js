document.addEventListener("DOMContentLoaded", async () => {
  // ------------------------------------------------------------
  // ELEMENTOS PARA TEMA
  // ------------------------------------------------------------
  const themeToggle = document.getElementById("theme-toggle");
  const themeIcon = document.getElementById("theme-icon");
  const body = document.getElementById("app-body");
  const nav = document.getElementById("main-nav");
  const btnLogout = document.getElementById("btn-logout");
  btnLogout.addEventListener("click", async () => {
    try {
      await apiLogout();
    } catch (e) {
      console.warn("logout error", e);
    } finally {
      window.location.href = "/ahorrapp/login";
    }
  });

  // -----------------------
  // THEME FUNCTIONS (IGUAL QUE perfil.js)
  // -----------------------
  function setNavbarThemeClasses(theme) {
    if (!nav) return;
    if (theme === "dark") {
      nav.classList.remove("navbar-light");
      nav.classList.add("navbar-dark");
      nav.style.background = "linear-gradient(90deg,#0f1724,#0b1220)";
    } else {
      nav.classList.remove("navbar-dark");
      nav.classList.add("navbar-light");
      nav.style.background = "var(--nav-light-bg)";
    }
  }

  function applyTheme(t) {
    if (!body) return;
    const themeNormalized = t === "dark" ? "dark" : "light";
    body.setAttribute("data-bs-theme", themeNormalized);
    setNavbarThemeClasses(themeNormalized);

    // icono
    if (themeNormalized === "dark") {
      themeIcon.className = "bi bi-sun-fill";
      themeToggle.classList.remove("btn-outline-secondary");
      themeToggle.classList.add("btn-warning");
      themeToggle.setAttribute("aria-pressed", "true");
    } else {
      themeIcon.className = "bi bi-moon-stars-fill";
      themeToggle.classList.remove("btn-warning");
      themeToggle.classList.add("btn-outline-secondary");
      themeToggle.setAttribute("aria-pressed", "false");
    }
  }

  // cargar tema guardado
  const savedTheme = localStorage.getItem("ahorra_theme") || "light";
  applyTheme(savedTheme);

  // toggle
  if (themeToggle) {
    themeToggle.addEventListener("click", () => {
      const next =
        body.getAttribute("data-bs-theme") === "dark" ? "light" : "dark";
      applyTheme(next);
      localStorage.setItem("ahorra_theme", next);
    });
  }

  // sombra navbar on scroll
  function handleNavScroll() {
    if (!nav) return;
    if (window.scrollY > 6) nav.classList.add("scrolled");
    else nav.classList.remove("scrolled");
  }
  window.addEventListener("scroll", handleNavScroll);
  handleNavScroll();

  const tablaMetas = document.getElementById("tabla-metas");
  const tablaLogros = document.getElementById("tabla-logros");

  cargarMetas();
  cargarLogros();

  // ---------------- METAS --------------------
  async function cargarMetas() {
    const metas = await apiListarMetas();

    tablaMetas.innerHTML =
      metas.length === 0
        ? `<tr><td colspan="6" class="text-center">No hay metas registradas</td></tr>`
        : metas
            .map(
              (m) => `
      <tr>
        <td>${m.titulo}</td>
        <td>${m.descripcion ?? "-"}</td>
        <td>S/ ${m.montoActual} / S/ ${m.montoObjetivo}</td>
        <td>${m.fechaInicio}</td>
        <td>${m.fechaFin}</td>
        <td>
          ${
            m.cumplida
              ? `<span class="badge bg-success">Cumplida</span>`
              : `<button class="btn btn-sm btn-success" onclick="cumplirMeta(${m.idMeta})">✔ Cumplir</button>`
          }
          <button class="btn btn-sm btn-danger ms-1" onclick="eliminarMeta(${
            m.idMeta
          })">🗑</button>
        </td>
      </tr>
    `
            )
            .join("");
  }

  // marcar como cumplida
  window.cumplirMeta = async function (id) {
    await apiCumplirMeta(id);
    cargarMetas();
  };

  // eliminar
  window.eliminarMeta = async function (id) {
    if (!confirm("¿Eliminar meta?")) return;
    await apiEliminarMeta(id);
    cargarMetas();
  };

  // ---------------- LOGROS --------------------
  async function cargarLogros() {
    const logros = await apiListarLogros();

    tablaLogros.innerHTML =
      logros.length === 0
        ? `<tr><td colspan="3" class="text-center">No hay logros aún</td></tr>`
        : logros
            .map(
              (l) => `
      <tr>
        <td>${l.nombre}</td>
        <td>${l.descripcion ?? "-"}</td>
        <td>${l.fechaLogro ?? "-"}</td>
      </tr>
    `
            )
            .join("");
  }
});
