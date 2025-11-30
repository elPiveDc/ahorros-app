// /js/service/auth.js
// Helpers para autenticación con backend basado en cookie HttpOnly.

const API = {
  login: "/auth/publico/login",
  register: "/auth/publico/registro",
  logout: "/auth/usuario/logout",
  me: "/auth/usuario/me",
  editar: "/auth/usuario/editar",
};

/**
 * Envío de login. El backend debe setear cookie HttpOnly (AUTH_TOKEN).
 * Usar credentials: 'include' para enviar/recibir cookies.
 * Devuelve Response (no token porque se usa cookie).
 */
async function apiLogin({ correo, contrasena }) {
  const res = await fetch(API.login, {
    method: "POST",
    credentials: "include", // importante: enviar/recibir cookie HttpOnly
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ correo, contrasena }),
  });
  return res;
}

/** Registro (si tu /auth/registro devuelve AuthResponseDTO con token, lo ignoramos si preferimos cookie) */
async function apiRegister({ nombre, correo, contrasena }) {
  const res = await fetch(API.register, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nombre, correo, contrasena }),
  });
  return res;
}

/** Logout: llama al endpoint que borra cookie en backend */
async function apiLogout() {
  const res = await fetch(API.logout, {
    method: "POST",
    credentials: "include",
  });
  return res;
}

/**
 * Obtener usuario actual. Endpoint protegido que usará la cookie HttpOnly para autenticar.
 * Devuelve objeto usuario JSON o lanza si 401.
 */
async function apiMe() {
  const res = await fetch(API.me, {
    method: "GET",
    credentials: "include",
  });

  if (res.status === 401 || res.status === 403) {
    return null;
  }

  if (!res.ok) {
    throw new Error("Error al obtener usuario: " + res.status);
  }

  return res.json();
}

/** Redirige a / si ya está autenticado (usa apiMe) */
async function redirectIfAuthenticated() {
  try {
    const user = await apiMe();
    if (user) {
      window.location.href = "/ahorrapp";
    }
  } catch (e) {
    // ignore
  }
}

/** Edita el perfil del usuario autenticado */
async function apiEditarPerfil({ nombre, avatarUrl, temaActual }) {
  const res = await fetch(API.editar, {
    method: "PUT",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nombre, avatarUrl, temaActual }),
  });

  if (!res.ok) {
    throw new Error("Error al editar perfil: " + res.status);
  }

  return res.json();
}
