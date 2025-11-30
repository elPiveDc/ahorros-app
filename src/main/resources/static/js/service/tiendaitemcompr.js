// /js/service/tiendaitemcompr.js
// Servicio para tienda y compras (usa auth por cookie HttpOnly)

const TIENDA_API = {
  items: "/api/tienda",
  compras: "/api/tienda/compras",
};

/** Listar items de la tienda */
async function apiListarItems() {
  const res = await fetch(TIENDA_API.items, {
    method: "GET",
    credentials: "include",
  });

  if (!res.ok) {
    throw new Error("Error al listar items: " + res.status);
  }

  return res.json();
}

/** Listar mis compras */
async function apiMisCompras() {
  const res = await fetch(TIENDA_API.compras, {
    method: "GET",
    credentials: "include",
  });

  if (!res.ok) {
    throw new Error("Error al listar compras: " + res.status);
  }

  return res.json();
}

/** Comprar un item por id */
async function apiComprarItem(idItem) {
  const res = await fetch(`${TIENDA_API.compras}/${idItem}`, {
    method: "POST",
    credentials: "include",
  });

  if (!res.ok) {
    let msg;
    try {
      msg = await res.text();
    } catch {
      msg = "Error al comprar item";
    }
    throw new Error(msg);
  }

  return res.json();
}
