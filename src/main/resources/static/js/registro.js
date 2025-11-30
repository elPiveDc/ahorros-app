document.addEventListener("DOMContentLoaded", () => {
  // Si ya tiene sesión → redirect
  redirectIfAuthenticated();

  const form = document.getElementById("register-form");
  const alertBox = document.getElementById("alert-box");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    alertBox.classList.add("d-none");

    const nombre = document.getElementById("reg-nombre").value.trim();
    const correo = document.getElementById("reg-email").value.trim();
    const contrasena = document.getElementById("reg-pass").value;
    const contrasena2 = document.getElementById("reg-pass2").value;

    if (!nombre || !correo || !contrasena || !contrasena2) {
      return showError("Completa todos los campos.");
    }

    if (contrasena !== contrasena2) {
      return showError("Las contraseñas no coinciden.");
    }

    if (contrasena.length < 6) {
      return showError("La contraseña debe tener al menos 6 caracteres.");
    }

    try {
      const res = await apiRegister({ nombre, correo, contrasena });

      if (res.ok) {
        // Registro exitoso: redirige a login
        window.location.href = "/ahorrapp/login";
        return;
      }

      let msg = await safeParseJsonOrText(res);
      showError(msg || "Error al registrarse.");
    } catch (err) {
      console.error(err);
      showError("No se pudo conectar con el servidor.");
    }
  });

  function showError(msg) {
    alertBox.textContent = msg;
    alertBox.classList.remove("d-none");
  }

  async function safeParseJsonOrText(res) {
    try {
      const j = await res.json();
      return j.message || j.error || JSON.stringify(j);
    } catch {
      return res.text();
    }
  }
});
