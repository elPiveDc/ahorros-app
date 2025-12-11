// /js/tienda.js
document.addEventListener("DOMContentLoaded", () => {
  // ------------------------------------------------------------
  // ELEMENTOS PARA TEMA
  // ------------------------------------------------------------
  const themeToggle = document.getElementById("theme-toggle");
  const themeIcon = document.getElementById("theme-icon");
  const body = document.getElementById("app-body");
  const nav = document.getElementById("main-nav");

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

  const itemsContainer = document.getElementById("items-container");
  const comprasContainer = document.getElementById("compras-container");
  const alertBox = document.getElementById("alert-box");

  cargarItems();
  cargarCompras();

  async function cargarItems() {
    try {
      const items = await apiListarItems();
      itemsContainer.innerHTML = "";

      if (!items || items.length === 0) {
        itemsContainer.innerHTML = "<p>No hay items en la tienda.</p>";
        return;
      }

      items.forEach((item) => {
        const card = document.createElement("div");
        card.className = "card mb-3";

        card.innerHTML = `
          <div class="row g-0">
            <div class="col-md-4">
              <img src="${item.imagenUrl || "/images/item-default.png"}"
                   class="img-fluid rounded-start"
                   alt="${item.nombre}">
            </div>
            <div class="col-md-8">
              <div class="card-body">
                <h5 class="card-title">${item.nombre}</h5>
                <p class="card-text">${item.descripcion || ""}</p>
                <p class="card-text">
                  <strong>Costo:</strong> ${item.costo}
                </p>
                <p class="card-text">
                  <small class="text-muted">Tipo: ${item.tipo}</small>
                </p>
                <button class="btn btn-primary">Comprar</button>
              </div>
            </div>
          </div>
        `;

        card
          .querySelector("button")
          .addEventListener("click", () => comprarItem(item.idItem));

        itemsContainer.appendChild(card);
      });
    } catch (e) {
      showError(e.message);
    }
  }

  async function comprarItem(idItem) {
    try {
      await apiComprarItem(idItem);
      showSuccess("Compra realizada exitosamente ✅");
      cargarCompras();
    } catch (e) {
      showError(e.message);
    }
  }

  async function cargarCompras() {
    try {
      const compras = await apiMisCompras();
      comprasContainer.innerHTML = "";

      if (!compras || compras.length === 0) {
        comprasContainer.innerHTML = "<li>No tienes compras.</li>";
        return;
      }

      compras.forEach((c) => {
        const li = document.createElement("li");

        const fecha = new Date(c.fechaCompra).toLocaleString();

        li.innerHTML = `
          <strong>${c.item.nombre}</strong>
          — ${c.costoPagado} créditos
          <br>
          <small class="text-muted">${fecha}</small>
        `;

        comprasContainer.appendChild(li);
      });
    } catch (e) {
      // opcional
    }
  }

  function showError(msg) {
    alertBox.textContent = msg;
    alertBox.className = "alert alert-danger";
    alertBox.classList.remove("d-none");
  }

  function showSuccess(msg) {
    alertBox.textContent = msg;
    alertBox.className = "alert alert-success";
    alertBox.classList.remove("d-none");
  }
});
