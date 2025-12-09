const API_LOGROS = {
  listar: "/api/logros",
  crearManual: "/api/logros/manual",
};

async function apiListarLogros() {
  const res = await fetch(API_LOGROS.listar, {
    method: "GET",
    credentials: "include",
  });
  return res.ok ? res.json() : [];
}

async function apiCrearLogroManual(logro) {
  const res = await fetch(API_LOGROS.crearManual, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(logro),
  });
  return res;
}
