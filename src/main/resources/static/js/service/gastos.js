// /js/service/gastos.js
// Helpers para consumir el backend de gastos con cookie HttpOnly.

const API_GASTOS = {
  crear: "/api/gastos",
  listar: "/api/gastos/listar",
  dia: "/api/gastos/dia", // requiere /dia/{fecha}
  semana: "/api/gastos/semana", // requiere /semana/{fecha}
  mes: "/api/gastos/mes", // requiere ?mes=x&anio=y
  actualizar: "/api/gastos", // requiere /{id}
  eliminar: "/api/gastos", // requiere /{id}
};

/** Crear gasto */
async function apiCrearGasto(gasto) {
  const res = await fetch(API_GASTOS.crear, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(gasto),
  });
  return res;
}

/** Listar gastos del usuario */
async function apiListarGastos() {
  const res = await fetch(API_GASTOS.listar, {
    method: "GET",
    credentials: "include",
  });
  return res.ok ? res.json() : [];
}

/** Listar gastos por día: fecha = "YYYY-MM-DD" */
async function apiGastosPorDia(fecha) {
  const res = await fetch(`${API_GASTOS.dia}/${fecha}`, {
    method: "GET",
    credentials: "include",
  });
  return res.ok ? res.json() : [];
}

/** Listar gastos por semana: fecha = "YYYY-MM-DD" */
async function apiGastosPorSemana(fecha) {
  const res = await fetch(`${API_GASTOS.semana}/${fecha}`, {
    method: "GET",
    credentials: "include",
  });
  return res.ok ? res.json() : [];
}

/** Listar gastos por mes */
async function apiGastosPorMes(mes, anio) {
  const url = `${API_GASTOS.mes}?mes=${mes}&anio=${anio}`;
  const res = await fetch(url, {
    method: "GET",
    credentials: "include",
  });
  return res.ok ? res.json() : [];
}

/** Actualizar gasto */
async function apiActualizarGasto(id, { monto, categoria, fecha }) {
  const res = await fetch(`${API_GASTOS.actualizar}/${id}`, {
    method: "PUT",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ monto, categoria, fecha }),
  });
  return res;
}

/** Eliminar gasto */
async function apiEliminarGasto(id) {
  const res = await fetch(`${API_GASTOS.eliminar}/${id}`, {
    method: "DELETE",
    credentials: "include",
  });
  return res;
}
