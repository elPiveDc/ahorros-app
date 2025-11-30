document.addEventListener("DOMContentLoaded", async () => {
  const user = await apiMe();

  if (!user) {
    window.location.href = "/ahorrapp/login";
    return;
  }

  // Mostrar nombre en navbar
  document.getElementById("nav-user-name").textContent = user.nombre;

  // Mostrar nombre en bienvenida
  document.getElementById("user-name").textContent = user.nombre;

  // Mostrar más datos
  document.getElementById("user-info").innerHTML = `
        <div class="card p-3">
            <p><strong>Correo:</strong> ${user.correo}</p>
            <p><strong>Monedas:</strong> ${user.monedas}</p>
            <p><strong>Tema actual:</strong> ${user.temaActual}</p>
        </div>
    `;

  // Acción al cerrar sesión
  document.getElementById("btn-logout").addEventListener("click", async () => {
    await apiLogout();
    window.location.href = "/ahorrapp/login";
  });
});
