document.addEventListener("DOMContentLoaded", async () => {
  const tablaMetas = document.getElementById("tabla-metas");
  const tablaLogros = document.getElementById("tabla-logros");

  cargarMetas();
  cargarLogros();

  // ---------------- METAS --------------------
  async function cargarMetas() {
    const metas = await apiListarMetas();

    tablaMetas.innerHTML =
      metas.length === 0
        ? `<tr><td colspan="6" class="text-center">No hay metas registradas</td></tr>`
        : metas
            .map(
              (m) => `
      <tr>
        <td>${m.titulo}</td>
        <td>${m.descripcion ?? "-"}</td>
        <td>S/ ${m.montoActual} / S/ ${m.montoObjetivo}</td>
        <td>${m.fechaInicio}</td>
        <td>${m.fechaFin}</td>
        <td>
          ${
            m.cumplida
              ? `<span class="badge bg-success">Cumplida</span>`
              : `<button class="btn btn-sm btn-success" onclick="cumplirMeta(${m.idMeta})">✔ Cumplir</button>`
          }
          <button class="btn btn-sm btn-danger ms-1" onclick="eliminarMeta(${
            m.idMeta
          })">🗑</button>
        </td>
      </tr>
    `
            )
            .join("");
  }

  // marcar como cumplida
  window.cumplirMeta = async function (id) {
    await apiCumplirMeta(id);
    cargarMetas();
  };

  // eliminar
  window.eliminarMeta = async function (id) {
    if (!confirm("¿Eliminar meta?")) return;
    await apiEliminarMeta(id);
    cargarMetas();
  };

  // ---------------- LOGROS --------------------
  async function cargarLogros() {
    const logros = await apiListarLogros();

    tablaLogros.innerHTML =
      logros.length === 0
        ? `<tr><td colspan="3" class="text-center">No hay logros aún</td></tr>`
        : logros
            .map(
              (l) => `
      <tr>
        <td>${l.nombre}</td>
        <td>${l.descripcion ?? "-"}</td>
        <td>${l.fechaLogro ?? "-"}</td>
      </tr>
    `
            )
            .join("");
  }
});
