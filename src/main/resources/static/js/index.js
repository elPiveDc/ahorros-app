// resources/static/js/index.js
// Versión mejorada: arregla contraste en modo claro, mejora navbar, animaciones y chart adaptativo.
// Respeta todos los IDs existentes.

document.addEventListener("DOMContentLoaded", async () => {
  // Elementos importantes (IDs que deben mantenerse)
  const elUserName = document.getElementById("user-name");
  const elUserInfo = document.getElementById("user-info");
  const btnLogout = document.getElementById("btn-logout");
  const tablaBody = document.getElementById("tabla-gastos");
  const chartCanvasId = "chart-month";
  const totalMesEl = document.getElementById("total-mes");
  const themeToggle = document.getElementById("theme-toggle");
  const themeIcon = document.getElementById("theme-icon");
  const body = document.getElementById("app-body");
  const heroCard = document.getElementById("hero-card");
  const nav = document.getElementById("main-nav");

  // -----------------------
  // Small utilities
  // -----------------------
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

  // Simple animate-in for elements (add .visible after small delay)
  function reveal(el, delay = 0) {
    if (!el) return;
    setTimeout(() => el.classList.add("visible"), delay);
  }

  // -----------------------
  // THEME: toggling + persistence + contrast fixes
  // -----------------------
  function setNavbarThemeClasses(theme) {
    // Manage navbar styles and toggler icon for accessibility
    if (!nav) return;
    if (theme === "dark") {
      nav.classList.remove("navbar-light");
      nav.classList.add("navbar-dark");
      // set a darker subtle gradient
      nav.style.background = "linear-gradient(90deg,#0f1724,#0b1220)";
    } else {
      nav.classList.remove("navbar-dark");
      nav.classList.add("navbar-light");
      nav.style.background = "var(--nav-light-bg)";
    }
  }

  function applyTheme(t) {
    body.setAttribute("data-bs-theme", t);
    setNavbarThemeClasses(t);

    // Toggle icon + button styles for clear affordance
    if (t === "dark") {
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

    // Adjust chart colors if present
    if (window.currentChartTheme && window.currentChartTheme !== t) {
      window.currentChartTheme = t;
      if (window.chartMonth) {
        // re-render chart using last data (we store it)
        renderChart(
          chartCanvasId,
          window.lastChartData || [],
          "Gastos del mes"
        );
      }
    } else {
      window.currentChartTheme = t;
    }
  }

  // Load persisted theme or default to light
  const savedTheme = localStorage.getItem("ahorra_theme") || "light";
  applyTheme(savedTheme);

  themeToggle.addEventListener("click", () => {
    const next =
      body.getAttribute("data-bs-theme") === "dark" ? "light" : "dark";
    applyTheme(next);
    localStorage.setItem("ahorra_theme", next);
  });

  // -----------------------
  // NAV behaviour on scroll (shadow)
  // -----------------------
  const handleNavScroll = () => {
    if (window.scrollY > 6) nav.classList.add("scrolled");
    else nav.classList.remove("scrolled");
  };
  window.addEventListener("scroll", handleNavScroll);
  handleNavScroll();

  // -----------------------
  // AUTH: get user (uses apiMe from /js/service/auth.js)
  // -----------------------
  let user = null;
  try {
    user = await apiMe();
  } catch (err) {
    console.error("Error apiMe:", err);
  }
  if (!user) {
    // not logged in -> redirect
    window.location.href = "/ahorrapp/login";
    return;
  }

  // Fill user UI
  elUserName.textContent = safe(user.nombre);

  // If backend has theme, apply it (and persist)
  if (user.temaActual) {
    const t = user.temaActual === "dark" ? "dark" : "light";
    applyTheme(t);
    localStorage.setItem("ahorra_theme", t);
  }

  elUserInfo.innerHTML = `
    <div class="d-flex flex-column">
      <div class="small text-muted">Correo</div>
      <div class="fw-semibold mb-2 text-truncate">${esc(
        user.correo ?? "-"
      )}</div>

      <div class="small text-muted">Monedas</div>
      <div class="fw-semibold mb-2">${esc(user.monedas ?? 0)} 🪙</div>

      <div class="small text-muted">Tema actual</div>
      <div class="fw-semibold">${esc(
        user.temaActual ?? body.getAttribute("data-bs-theme")
      )}</div>
    </div>
  `;

  btnLogout.addEventListener("click", async () => {
    try {
      await apiLogout();
    } catch (e) {
      console.warn("logout error", e);
    } finally {
      window.location.href = "/ahorrapp/login";
    }
  });

  // reveal hero + userInfo after fill
  reveal(heroCard, 50);
  reveal(elUserInfo, 100);

  // -----------------------
  // Charts + gastos logic
  // -----------------------
  let chartMonth = null;
  window.chartMonth = chartMonth;
  window.lastChartData = [];
  window.currentChartTheme = body.getAttribute("data-bs-theme");

  function renderList(gastos) {
    if (!tablaBody) return;
    if (!gastos || gastos.length === 0) {
      tablaBody.innerHTML = `<tr><td colspan="3" class="text-center text-muted">No hay registros.</td></tr>`;
      return;
    }

    const top10 = gastos.slice(0, 10);
    tablaBody.innerHTML = top10
      .map(
        (g) => `
      <tr class="align-middle">
        <td>${esc(g.categoria)}</td>
        <td class="fw-semibold">S/ ${Number(g.monto).toFixed(2)}</td>
        <td class="text-muted small">${
          g.fechaRegistro ? g.fechaRegistro.split("T")[0] : "-"
        }</td>
      </tr>
    `
      )
      .join("");
  }

  function themeIsDark() {
    return body.getAttribute("data-bs-theme") === "dark";
  }

  function getBarColors(values) {
    const max = Math.max(...values, 1);
    return values.map((v) => {
      const t = Math.min(1, v / max);
      // choose different palettes for dark/light for better contrast
      if (themeIsDark()) {
        const r = Math.round(120 + 80 * t);
        const g = Math.round(80 + 40 * t);
        const b = Math.round(200 - 40 * t);
        return `rgba(${r},${g},${b},0.9)`;
      } else {
        // lighter, more vivid on light background
        const r = Math.round(60 + 120 * t);
        const g = Math.round(100 - 30 * t);
        const b = Math.round(180 - 60 * t);
        return `rgba(${r},${g},${b},0.95)`;
      }
    });
  }

  function renderChart(canvasId, gastos, title) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;
    if (chartMonth) chartMonth.destroy();

    // prepare data
    const labels = gastos.map((g) => g.categoria);
    const values = gastos.map((g) => Number(g.monto) || 0);

    const bg = getBarColors(values);

    // store for re-render on theme change
    window.lastChartData = gastos;

    chartMonth = new Chart(ctx, {
      type: "bar",
      data: {
        labels,
        datasets: [
          {
            label: title,
            data: values,
            backgroundColor: bg,
            borderColor: bg.map((c) => c.replace(/0\.9\)|0\.95\)/, "1)")), // ensure border is solid
            borderWidth: 1,
            borderRadius: 8,
            barThickness: "flex",
          },
        ],
      },
      options: {
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx) => `S/ ${Number(ctx.parsed.y).toFixed(2)}`,
            },
          },
        },
        scales: {
          x: { ticks: { maxRotation: 40, minRotation: 0 } },
          y: { beginAtZero: true, ticks: { precision: 0 } },
        },
        animation: { duration: 700, easing: "easeOutQuart" },
        maintainAspectRatio: false,
        responsive: true,
      },
    });

    window.chartMonth = chartMonth;
  }

  // Loads only month summary using apiGastosPorMes (from /js/service/gastos.js)
  async function loadMonthAndRender() {
    const mesActual = new Date().getMonth() + 1;
    const anioActual = new Date().getFullYear();

    let data = [];
    try {
      data = await apiGastosPorMes(mesActual, anioActual);
    } catch (e) {
      console.error("Error cargando gastos del mes:", e);
      data = [];
    }

    // If API returns list of individual gastos -> group by categoria
    if (
      Array.isArray(data) &&
      data.length > 0 &&
      data[0] &&
      data.some((it) => it.fechaRegistro)
    ) {
      const agg = {};
      data.forEach((g) => {
        const cat = g.categoria ?? "Sin categoría";
        const monto = Number(g.monto) || 0;
        if (!agg[cat])
          agg[cat] = {
            categoria: cat,
            monto: 0,
            fechaRegistro: g.fechaRegistro,
          };
        agg[cat].monto += monto;
        if (
          !agg[cat].fechaRegistro ||
          new Date(g.fechaRegistro) > new Date(agg[cat].fechaRegistro)
        ) {
          agg[cat].fechaRegistro = g.fechaRegistro;
        }
      });
      data = Object.values(agg).sort((a, b) => b.monto - a.monto);
    } else if (Array.isArray(data) && data.length > 0) {
      data = data
        .map((d) => ({ ...d, monto: Number(d.monto) || 0 }))
        .sort((a, b) => b.monto - a.monto);
    } else {
      data = [];
    }

    // show total
    const total = data.reduce((s, x) => s + (Number(x.monto) || 0), 0);
    if (totalMesEl) {
      totalMesEl.textContent = total ? `Total: S/ ${total.toFixed(2)}` : "";
      // animate small bounce
      totalMesEl.animate(
        [
          { transform: "translateY(6px) scale(.995)" },
          { transform: "translateY(0) scale(1)" },
        ],
        { duration: 420, easing: "cubic-bezier(.2,.9,.3,1)" }
      );
    }

    // render chart + list
    renderChart(chartCanvasId, data, "Gastos del mes");
    renderList(data);

    // reveal table smoothly
    const table = tablaBody.closest("table");
    if (table) {
      table.classList.add("fade-in-up");
      setTimeout(() => table.classList.add("visible"), 240);
    }
  }

  // Small UX: animate nav links on collapse open
  const mainNavbarEl = document.getElementById("mainNavbar");
  if (mainNavbarEl) {
    mainNavbarEl.addEventListener("shown.bs.collapse", () => {
      const links = mainNavbarEl.querySelectorAll(".nav-link, .btn");
      links.forEach((lnk, i) => {
        lnk.style.transition = `opacity .22s ease ${
          i * 35
        }ms, transform .22s ease ${i * 35}ms`;
        lnk.style.opacity = 0;
        lnk.style.transform = "translateY(6px)";
        requestAnimationFrame(() => {
          lnk.style.opacity = 1;
          lnk.style.transform = "translateY(0)";
        });
      });
    });
  }

  // initial small delay for UX and then load data
  setTimeout(() => {
    loadMonthAndRender();
  }, 180);

  // Expose for debugging if needed
  window.ahorra = window.ahorra || {};
  window.ahorra.reloadMonth = loadMonthAndRender;
});
