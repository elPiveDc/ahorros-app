// /js/perfil.js
// Mejora: integra el sistema de temas (igual que index.js), mejoras responsive y UX.
// Mantiene todas las IDs usadas originalmente.

document.addEventListener("DOMContentLoaded", async () => {
  // Elementos del perfil
  const nombreEl = document.getElementById("perfil-nombre");
  const correoEl = document.getElementById("perfil-correo");
  const rolEl = document.getElementById("perfil-rol");
  const monedasEl = document.getElementById("perfil-monedas");
  const avatarEl = document.getElementById("perfil-avatar");
  const temaEl = document.getElementById("perfil-tema");
  const expiracionEl = document.getElementById("perfil-expiracion");

  const btnLogout = document.getElementById("btn-logout");
  const btnEditar = document.getElementById("btn-editar");

  const editNombre = document.getElementById("edit-nombre");
  const editAvatarUrl = document.getElementById("edit-avatar-url");
  const editAvatarFile = document.getElementById("edit-avatar-file");
  const editTema = document.getElementById("edit-tema");
  const formEditar = document.getElementById("form-editar-perfil");
  const avatarPreview = document.getElementById("edit-avatar-preview");

  // elementos para tema / nav
  const themeToggle = document.getElementById("theme-toggle");
  const themeIcon = document.getElementById("theme-icon");
  const body = document.getElementById("app-body");
  const nav = document.getElementById("main-nav");

  // utilidades
  const safe = (v) => (v === undefined || v === null ? "" : v);

  function esc(s) {
    if (s === undefined || s === null) return "";
    return String(s)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  // -----------------------
  // THEME: same approach as index.js (light/dark + navbar tweaks)
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
    // aceptar solo 'dark' o 'light' como efectos principales; otros valores se muestran pero no transforman completamente
    const themeNormalized = t === "dark" ? "dark" : "light";
    body.setAttribute("data-bs-theme", themeNormalized);
    setNavbarThemeClasses(themeNormalized);

    // icono y estado del botón
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

  // cargar tema persistido
  const savedTheme = localStorage.getItem("ahorra_theme") || "light";
  applyTheme(savedTheme);

  // toggle handler
  if (themeToggle) {
    themeToggle.addEventListener("click", () => {
      const next =
        body.getAttribute("data-bs-theme") === "dark" ? "light" : "dark";
      applyTheme(next);
      localStorage.setItem("ahorra_theme", next);
    });
  }

  // nav shadow on scroll (misma UX que index)
  function handleNavScroll() {
    if (!nav) return;
    if (window.scrollY > 6) nav.classList.add("scrolled");
    else nav.classList.remove("scrolled");
  }
  window.addEventListener("scroll", handleNavScroll);
  handleNavScroll();

  // -----------------------
  // Cargar user (usa apiMe de /js/service/auth.js)
  // -----------------------
  let userData = null;
  try {
    userData = await apiMe();
  } catch (err) {
    console.error("Error apiMe:", err);
  }
  if (!userData) {
    window.location.href = "/ahorrapp/login";
    return;
  }

  // Rellenar UI
  nombreEl.textContent = safe(userData.nombre) || "—";
  correoEl.textContent = safe(userData.correo) || "—";
  rolEl.textContent = safe(userData.rol) || "—";
  monedasEl.textContent = safe(userData.monedas ?? 0) + " 🪙";
  avatarEl.src = userData.avatarUrl || "/img/perfilgenerico.jpeg";
  temaEl.textContent =
    safe(userData.temaActual) || body.getAttribute("data-bs-theme") || "—";

  if (userData.expiracion) {
    const fecha = new Date(userData.expiracion);
    expiracionEl.textContent = fecha.toLocaleString();
  } else {
    expiracionEl.textContent = "—";
  }

  // Si el usuario tiene preferencia de tema almacenada en el backend, aplicarla
  if (userData.temaActual) {
    const t = userData.temaActual === "dark" ? "dark" : "light";
    applyTheme(t);
    localStorage.setItem("ahorra_theme", t);
  }

  // -----------------------------
  // Logout
  // -----------------------------
  if (btnLogout) {
    btnLogout.addEventListener("click", async () => {
      try {
        await apiLogout();
      } catch (e) {
        console.warn("logout error", e);
      } finally {
        window.location.href = "/ahorrapp/login";
      }
    });
  }

  // -----------------------------
  // Abrir modal de edición
  // -----------------------------
  if (btnEditar) {
    btnEditar.addEventListener("click", () => {
      editNombre.value = userData.nombre || "";
      // Normalizar valor de tema al select (si coincide)
      editTema.value =
        userData.temaActual || body.getAttribute("data-bs-theme") || "light";
      editAvatarUrl.value = userData.avatarUrl || "";

      avatarPreview.src = userData.avatarUrl || "/img/perfilgenerico.jpeg";
      editAvatarFile.value = "";

      const modal = new bootstrap.Modal(
        document.getElementById("modalEditarPerfil")
      );
      modal.show();
    });
  }

  // -----------------------------
  // Preview avatar por URL
  // -----------------------------
  if (editAvatarUrl) {
    editAvatarUrl.addEventListener("input", () => {
      if (editAvatarUrl.value.trim() !== "") {
        avatarPreview.src = editAvatarUrl.value.trim();
        editAvatarFile.value = "";
      }
    });
  }

  // -----------------------------
  // Preview avatar por archivo
  // -----------------------------
  if (editAvatarFile) {
    editAvatarFile.addEventListener("change", () => {
      const file = editAvatarFile.files[0];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = () => {
        avatarPreview.src = reader.result;
        editAvatarUrl.value = "";
      };
      reader.readAsDataURL(file);
    });
  }

  // -----------------------------
  // Guardar cambios del perfil
  // -----------------------------
  if (formEditar) {
    formEditar.addEventListener("submit", async (e) => {
      e.preventDefault();

      let finalAvatar = userData.avatarUrl || "/img/perfilgenerico.jpeg";

      if (editAvatarFile.files.length > 0) {
        finalAvatar = avatarPreview.src; // base64
      } else if (editAvatarUrl.value.trim() !== "") {
        finalAvatar = editAvatarUrl.value.trim();
      }

      // Normalizar tema antes de enviar: permitir 'dark'|'light'|'otros'
      let temaToSend = editTema.value;
      if (temaToSend !== "dark" && temaToSend !== "light") {
        // si es 'sistema', 'verde', etc. lo enviamos tal cual y backend puede mapearlo
        temaToSend = editTema.value;
      }

      const payload = {
        nombre: editNombre.value.trim(),
        avatarUrl: finalAvatar,
        temaActual: temaToSend,
      };

      try {
        const actualizado = await apiEditarPerfil(payload);

        // actualizar UI localmente
        userData = actualizado; // actualizar referencia
        nombreEl.textContent = actualizado.nombre || nombreEl.textContent;
        avatarEl.src = actualizado.avatarUrl || avatarEl.src;
        temaEl.textContent = actualizado.temaActual || temaEl.textContent;

        // aplicar tema si backend devolvió uno conocido
        if (actualizado.temaActual) {
          const t = actualizado.temaActual === "dark" ? "dark" : "light";
          applyTheme(t);
          localStorage.setItem("ahorra_theme", t);
        }

        bootstrap.Modal.getInstance(
          document.getElementById("modalEditarPerfil")
        ).hide();
      } catch (err) {
        console.error("Error editando perfil:", err);
        // UX mejorado: mostrar alerta Bootstrap
        const toast = document.createElement("div");
        toast.className = "alert alert-danger mt-2";
        toast.role = "alert";
        toast.textContent =
          "No se pudo actualizar el perfil. Intenta de nuevo.";
        document.querySelector("main .container")?.prepend(toast);
        setTimeout(() => toast.remove(), 4000);
      }
    });
  }

  // Exponer una función para debugging / recarga si es necesario
  window.ahorra = window.ahorra || {};
  window.ahorra.reloadProfile = async function () {
    try {
      const fresh = await apiMe();
      if (fresh) {
        userData = fresh;
        nombreEl.textContent = safe(fresh.nombre);
        correoEl.textContent = safe(fresh.correo);
        rolEl.textContent = safe(fresh.rol);
        monedasEl.textContent = safe(fresh.monedas ?? 0) + " 🪙";
        avatarEl.src = fresh.avatarUrl || "/img/perfilgenerico.jpeg";
        temaEl.textContent =
          safe(fresh.temaActual) || body.getAttribute("data-bs-theme");
      }
    } catch (e) {
      console.warn("reloadProfile error", e);
    }
  };
});
