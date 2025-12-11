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
});

document.getElementById("formGasto").addEventListener("submit", async (e) => {
  e.preventDefault();
  const monto = parseFloat(document.getElementById("monto").value);
  const categoria = document.getElementById("categoria").value;
  const tipoRegistro = document.getElementById("tipoRegistro").value;
  const descripcion = document.getElementById("descripcion").value;

  const gasto = {
    monto,
    categoria,
    descripcion,
    tipoRegistro,
    archivoUrl: null,
    fecha: new Date().toISOString().split("T")[0],
  };

  const res = await apiCrearGasto(gasto);
  alert(res.ok ? "✅ Gasto registrado!" : "❌ Error al registrar gasto");
});

/** OCR con foto (subida o cámara) */
document.getElementById("btnOCR").addEventListener("click", async () => {
  const file = document.getElementById("fotoBoleta").files[0];
  if (!file) return alert("Toma o selecciona una foto primero");

  // Mostrar modal de cargando
  const loadingModal = new bootstrap.Modal(
    document.getElementById("loadingModal")
  );
  loadingModal.show();

  const reader = new FileReader();
  reader.onload = async () => {
    try {
      const {
        data: { text },
      } = await Tesseract.recognize(reader.result, "spa");
      document.getElementById("ocrResultado").innerText =
        "Texto detectado: " + text;

      // Buscar monto en el texto (ejemplo simple: primer número con decimales)
      const match = text.match(/\d+(\.\d{1,2})?/);
      const monto = match ? parseFloat(match[0]) : null;

      if (monto) {
        const categoria = document.getElementById("categoria").value || "otros";
        const gasto = {
          monto,
          categoria,
          descripcion: "OCR boleta",
          tipoRegistro: "foto",
          archivoUrl: "localfile://" + file.name,
          fecha: new Date().toISOString().split("T")[0],
        };
        const res = await apiCrearGasto(gasto);
        alert(
          res.ok
            ? "📸 Gasto registrado desde foto!"
            : "❌ Error al registrar gasto"
        );
      } else {
        alert("⚠️ No se pudo detectar monto en la boleta");
      }
    } finally {
      // Ocultar modal al terminar
      loadingModal.hide();
    }
  };
  reader.readAsDataURL(file);
});

/** Registro por voz */
document.getElementById("btnVoz").addEventListener("click", () => {
  const SpeechRecognition =
    window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRecognition)
    return alert("Tu navegador no soporta reconocimiento de voz");

  const recognition = new SpeechRecognition();
  recognition.lang = "es-ES";
  recognition.start();

  recognition.onresult = async (event) => {
    const texto = event.results[0][0].transcript;
    document.getElementById("vozResultado").innerText =
      "Texto detectado: " + texto;

    // Buscar monto en el dictado
    const match = texto.match(/\d+(\.\d{1,2})?/);
    const monto = match ? parseFloat(match[0]) : null;

    // Buscar categoría básica en el dictado
    let categoria = "otros";
    if (texto.includes("comida")) categoria = "alimentacion";
    else if (texto.includes("bus") || texto.includes("taxi"))
      categoria = "transporte";
    else if (texto.includes("luz") || texto.includes("agua"))
      categoria = "servicios";

    if (monto) {
      const gasto = {
        monto,
        categoria,
        descripcion: texto,
        tipoRegistro: "voz",
        archivoUrl: null,
        fecha: new Date().toISOString().split("T")[0],
      };
      const res = await apiCrearGasto(gasto);
      alert(
        res.ok ? "🎙️ Gasto registrado por voz!" : "❌ Error al registrar gasto"
      );
    } else {
      alert("⚠️ No se detectó monto en el dictado");
    }
  };
});
