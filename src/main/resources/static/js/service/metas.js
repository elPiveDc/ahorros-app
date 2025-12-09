// /js/service/metas.js

const API_METAS = {
  crear: "/api/metas",
  listar: "/api/metas",
  obtener: "/api/metas", // /{idMeta}
  actualizar: "/api/metas", // /{idMeta}
  eliminar: "/api/metas", // /{idMeta}
  cumplir: "/api/metas", // /{idMeta}/cumplir
};

/** Crear meta */
async function apiCrearMeta(meta) {
  const res = await fetch(API_METAS.crear, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(meta),
  });
  return res;
}

/** Listar metas usuario */
async function apiListarMetas() {
  const res = await fetch(API_METAS.listar, {
    method: "GET",
    credentials: "include",
  });
  return res.ok ? res.json() : [];
}

/** Obtener meta por ID */
async function apiObtenerMeta(id) {
  const res = await fetch(`${API_METAS.obtener}/${id}`, {
    method: "GET",
    credentials: "include",
  });
  return res.ok ? res.json() : null;
}

/** Actualizar meta */
async function apiActualizarMeta(id, meta) {
  const res = await fetch(`${API_METAS.actualizar}/${id}`, {
    method: "PUT",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(meta),
  });
  return res;
}

/** Eliminar meta */
async function apiEliminarMeta(id) {
  const res = await fetch(`${API_METAS.eliminar}/${id}`, {
    method: "DELETE",
    credentials: "include",
  });
  return res;
}

/** Marcar como cumplida */
async function apiCumplirMeta(id) {
  const res = await fetch(`${API_METAS.cumplir}/${id}/cumplir`, {
    method: "PUT",
    credentials: "include",
  });
  return res.ok ? res.json() : null;
}
