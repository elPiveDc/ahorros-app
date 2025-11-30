// /js/login.js
document.addEventListener("DOMContentLoaded", () => {
  // Si ya tenemos sesión válida, redirigimos
  redirectIfAuthenticated();

  const form = document.getElementById("login-form");
  const alertBox = document.getElementById("alert-box");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    alertBox.classList.add("d-none");
    const correo = document.getElementById("user-email").value.trim();
    const contrasena = document.getElementById("user-pass").value;

    if (!correo || !contrasena) {
      showError("Completa correo y contraseña.");
      return;
    }

    try {
      const res = await apiLogin({ correo, contrasena });

      if (res.status === 200 || res.status === 204) {
        // El backend debe haber agregado cookie HttpOnly; redirigimos a '/'
        window.location.href = "/ahorrapp";
        return;
      }

      // Manejo de errores habituales
      if (res.status === 401) {
        showError("Credenciales incorrectas.");
        return;
      }

      // Si el backend retorna JSON con mensaje de error
      let txt = await safeParseJsonOrText(res);
      showError(txt || "Error al iniciar sesión.");
    } catch (err) {
      console.error(err);
      showError("Error de conexión con el servidor.");
    }
  });

  function showError(msg) {
    alertBox.textContent = msg;
    alertBox.classList.remove("d-none");
  }

  async function safeParseJsonOrText(res) {
    try {
      const j = await res.json();
      if (j && typeof j === "object") {
        return j.message || (j.error ? j.error : JSON.stringify(j));
      }
      return String(j);
    } catch (e) {
      try {
        return await res.text();
      } catch (e2) {
        return null;
      }
    }
  }
});
