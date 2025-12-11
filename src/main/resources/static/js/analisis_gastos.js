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

  // ------------------------------------------------------------
  // GASTOS / ESTADÍSTICAS ORIGINAL
  // ------------------------------------------------------------
  const tablaBody = document.getElementById("tabla-gastos");

  let chartDay = null;
  let chartWeek = null;
  let chartMonth = null;

  // ---------------------- FECHAS ----------------------
  function getFechaLocal() {
    const hoy = new Date();
    const y = hoy.getFullYear();
    const m = String(hoy.getMonth() + 1).padStart(2, "0");
    const d = String(hoy.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
  }

  function getMonday(date) {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(d.setDate(diff));
  }

  // ---------------------- CARGA INICIAL ----------------------
  setTimeout(() => {
    const hoy = getFechaLocal();
    const semanaInicio = getMonday(new Date()).toISOString().slice(0, 10);
    const mesActual = new Date().getMonth() + 1;
    const anioActual = new Date().getFullYear();

    loadDay(hoy);
    loadWeek(semanaInicio);
    loadMonth(mesActual, anioActual);
  }, 350);

  // ---------------------- BOTONES TABLA ----------------------
  document.getElementById("btn-dia").addEventListener("click", async () => {
    activarBoton("btn-dia");
    const data = await apiGastosPorDia(getFechaLocal());
    renderList(data);
  });

  document.getElementById("btn-semana").addEventListener("click", async () => {
    activarBoton("btn-semana");
    const semana = getMonday(new Date()).toISOString().slice(0, 10);
    const data = await apiGastosPorSemana(semana);
    renderList(data);
  });

  document.getElementById("btn-mes").addEventListener("click", async () => {
    activarBoton("btn-mes");
    const mes = new Date().getMonth() + 1;
    const anio = new Date().getFullYear();
    const data = await apiGastosPorMes(mes, anio);
    renderList(data);
  });

  function activarBoton(id) {
    document.querySelectorAll(".btn-group button").forEach((btn) => {
      btn.classList.remove("active");
    });
    document.getElementById(id).classList.add("active");
  }

  // ---------------------- LOADS ----------------------
  async function loadDay(hoy) {
    const data = await apiGastosPorDia(hoy);
    chartDay = renderChart("chart-day", data, "Gastos del día", chartDay);
    renderList(data);
  }

  async function loadWeek(semanaInicio) {
    const data = await apiGastosPorSemana(semanaInicio);
    chartWeek = renderChart(
      "chart-week",
      data,
      "Gastos de la semana",
      chartWeek
    );
  }

  async function loadMonth(mesActual, anioActual) {
    const data = await apiGastosPorMes(mesActual, anioActual);
    chartMonth = renderChart("chart-month", data, "Gastos del mes", chartMonth);
  }

  // ---------------------- CHART ----------------------
  function renderChart(canvasId, gastos, title, oldChart) {
    if (oldChart) oldChart.destroy();

    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const labels = gastos.map((g) => g.categoria);
    const values = gastos.map((g) => g.monto);

    return new Chart(ctx, {
      type: "bar",
      data: {
        labels,
        datasets: [
          {
            label: title,
            data: values,
            borderWidth: 1,
            backgroundColor: "rgba(65, 181, 235, 0.84)",
            borderColor: "white",
          },
        ],
      },
      options: {
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } },
      },
    });
  }

  // ---------------------- LISTA TOP 10 ----------------------
  async function renderList(gastos) {
    if (!tablaBody) return;

    if (!gastos || gastos.length === 0) {
      tablaBody.innerHTML = `
        <tr><td colspan="3" class="text-center">No hay registros.</td></tr>`;
      return;
    }

    const top10 = gastos.slice(0, 10);

    tablaBody.innerHTML = top10
      .map(
        (g) => `
      <tr>
        <td>${g.categoria}</td>
        <td>S/ ${g.monto}</td>
        <td>${g.fechaRegistro ? g.fechaRegistro.split("T")[0] : "-"}</td>
      </tr>`
      )
      .join("");
  }
});
