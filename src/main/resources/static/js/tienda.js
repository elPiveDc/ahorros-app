// /js/tienda.js
document.addEventListener("DOMContentLoaded", () => {
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
