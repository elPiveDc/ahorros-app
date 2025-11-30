// /js/perfil.js

document.addEventListener("DOMContentLoaded", async () => {
  const nombreEl = document.getElementById("perfil-nombre");
  const correoEl = document.getElementById("perfil-correo");
  const rolEl = document.getElementById("perfil-rol");
  const monedasEl = document.getElementById("perfil-monedas");
  const avatarEl = document.getElementById("perfil-avatar");
  const temaEl = document.getElementById("perfil-tema");
  const expiracionEl = document.getElementById("perfil-expiracion");

  const btnLogout = document.getElementById("btn-logout");
  const btnEditar = document.getElementById("btn-editar");

  const editNombre = document.getElementById("edit-nombre");
  const editAvatarUrl = document.getElementById("edit-avatar-url");
  const editAvatarFile = document.getElementById("edit-avatar-file");
  const editTema = document.getElementById("edit-tema");
  const formEditar = document.getElementById("form-editar-perfil");
  const avatarPreview = document.getElementById("edit-avatar-preview");

  let userData = null;

  try {
    userData = await apiMe();

    if (!userData) {
      window.location.href = "/ahorrapp/login";
      return;
    }

    nombreEl.textContent = userData.nombre;
    correoEl.textContent = userData.correo;
    rolEl.textContent = userData.rol;
    monedasEl.textContent = userData.monedas;
    avatarEl.src = userData.avatarUrl || "/img/perfilgenerico.jpeg";
    temaEl.textContent = userData.temaActual;

    const fecha = new Date(userData.expiracion);
    expiracionEl.textContent = fecha.toLocaleString();
  } catch (err) {
    console.error("Error al cargar perfil:", err);
    window.location.href = "/ahorrapp/login";
  }

  // -----------------------------
  // Abrir modal de edición
  // -----------------------------
  btnEditar.addEventListener("click", () => {
    editNombre.value = userData.nombre;
    editTema.value = userData.temaActual;
    editAvatarUrl.value = userData.avatarUrl || "";

    avatarPreview.src = userData.avatarUrl || "/img/perfilgenerico.jpeg";
    editAvatarFile.value = "";

    const modal = new bootstrap.Modal(
      document.getElementById("modalEditarPerfil")
    );
    modal.show();
  });

  // -----------------------------
  // Vista previa cuando se escribe URL
  // -----------------------------
  editAvatarUrl.addEventListener("input", () => {
    if (editAvatarUrl.value.trim() !== "") {
      avatarPreview.src = editAvatarUrl.value.trim();
      editAvatarFile.value = ""; // borrar archivo si escribe URL
    }
  });

  // -----------------------------
  // Vista previa al subir archivo
  // -----------------------------
  editAvatarFile.addEventListener("change", () => {
    const file = editAvatarFile.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => {
      avatarPreview.src = reader.result; // Base64
      editAvatarUrl.value = ""; // borrar URL si usó archivo
    };
    reader.readAsDataURL(file);
  });

  // -----------------------------
  // Guardar cambios del perfil
  // -----------------------------
  formEditar.addEventListener("submit", async (e) => {
    e.preventDefault();

    let finalAvatar = userData.avatarUrl; // por defecto, mantiene el actual

    if (editAvatarFile.files.length > 0) {
      finalAvatar = avatarPreview.src; // base64
    } else if (editAvatarUrl.value.trim() !== "") {
      finalAvatar = editAvatarUrl.value.trim();
    }

    const payload = {
      nombre: editNombre.value,
      avatarUrl: finalAvatar,
      temaActual: editTema.value,
    };

    try {
      const actualizado = await apiEditarPerfil(payload);

      nombreEl.textContent = actualizado.nombre;
      avatarEl.src = actualizado.avatarUrl || "/img/perfilgenerico.jpeg";
      temaEl.textContent = actualizado.temaActual;

      bootstrap.Modal.getInstance(
        document.getElementById("modalEditarPerfil")
      ).hide();
    } catch (err) {
      console.error("Error editando perfil:", err);
      alert("No se pudo actualizar el perfil");
    }
  });

  btnLogout.addEventListener("click", async () => {
    await apiLogout();
    window.location.href = "/ahorrapp/login";
  });
});
