// js/main.js - updated
const STORAGE = {
  userKey: "aa_user",
  coinsKey: "aa_coins",
  goalsKey: "aa_goals",
  appsKey: "aa_apps",
  expensesKey: "aa_expenses",
};

function ensureDemo() {
  if (!localStorage.getItem(STORAGE.userKey)) {
    const demo = {
      name: "Juan Pérez",
      email: "juan@demo.com",
      avatar: "JP",
      photo: null,
    };
    localStorage.setItem(STORAGE.userKey, JSON.stringify(demo));
  }
  if (!localStorage.getItem(STORAGE.coinsKey))
    localStorage.setItem(STORAGE.coinsKey, "120");
  if (!localStorage.getItem(STORAGE.appsKey))
    localStorage.setItem(
      STORAGE.appsKey,
      JSON.stringify(["Banco Ejemplar", "Nubank"])
    );
  if (!localStorage.getItem(STORAGE.goalsKey))
    localStorage.setItem(
      STORAGE.goalsKey,
      JSON.stringify([
        {
          id: 1,
          title: "Ahorrar $0.50 diarios por 7 días",
          reward: 10,
          progress: 0,
          daysRequired: 7,
          accepted: false,
          completed: false,
        },
      ])
    );
  if (!localStorage.getItem(STORAGE.expensesKey))
    localStorage.setItem(
      STORAGE.expensesKey,
      JSON.stringify([
        { amount: 5, category: "Comida", date: new Date().toISOString() },
      ])
    );
}

function loadNavIfNeeded() {
  const path = window.location.pathname;
  const base = path.substring(path.lastIndexOf("/") + 1);
  if (base === "login.html" || base === "registro_usuario.html") return;
  fetch("/components/nav.html")
    .then((r) => r.text())
    .then((html) => {
      const temp = document.createElement("div");
      temp.innerHTML = html;
      document.body.prepend(temp);
      attachNavHandlers();
    });
}

function attachNavHandlers() {
  const logout = document.getElementById("btn-logout");
  if (logout)
    logout.addEventListener("click", () => {
      alert("Sesión cerrada (demo)");
      window.location.href = "/login.html";
    });

  const hb = document.querySelectorAll(".btn-hamburger");
  hb.forEach((b) => {
    if (window.innerWidth < 768) b.style.display = "none";
    else b.style.display = "inline-block";
  });
}

function getCoins() {
  return parseInt(localStorage.getItem(STORAGE.coinsKey) || "0", 10);
}
function setCoins(n) {
  localStorage.setItem(STORAGE.coinsKey, String(n));
  updateCoinsUI();
}
function addCoins(n) {
  setCoins(getCoins() + n);
}
function updateCoinsUI() {
  document
    .querySelectorAll(".coins-amount")
    .forEach((el) => (el.textContent = getCoins()));
}

function showYouTubeAd() {
  const modal = document.createElement("div");
  modal.className =
    "modal-backdrop d-flex align-items-center justify-content-center";
  modal.style.zIndex = 2000;
  modal.innerHTML = `
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Anuncio - Ver para ganar +5 monedas</h5>
        <button type="button" class="btn-close ad-close"></button>
      </div>
      <div class="modal-body">
        <div class="ratio ratio-16x9">
          <iframe
            id="yt-iframe"
            src="/media/roll.mp4"
            title="YouTube video"
            allow="autoplay; encrypted-media"
            allowfullscreen
          ></iframe>
        </div>
        <div class="mt-2 small text-muted">
          Necesitas ver el vídeo completo para recibir la recompensa.
        </div>
      </div>
    </div>
  </div>
`;
  document.body.appendChild(modal);
  const closeBtn = modal.querySelector(".ad-close");
  closeBtn.addEventListener("click", () => modal.remove());
  const iframe = modal.querySelector("#yt-iframe");
  let rewarded = false;
  setTimeout(() => {
    if (!rewarded) {
      addCoins(5);
      rewarded = true;
      alert("+5 monedas añadidas");
      modal.remove();
    }
  }, 7500);
}

function renderWeekChart() {
  const el = document.getElementById("chart-week");
  if (!el) return;
  new Chart(el.getContext("2d"), {
    type: "bar",
    data: {
      labels: ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"],
      datasets: [
        {
          label: "Gastos",
          data: [5, 8, 6, 12, 3, 9, 4],
          backgroundColor: "rgba(13,110,253,0.8)",
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
    },
  });
}
function renderDayChart() {
  const el = document.getElementById("chart-day");
  if (!el) return;
  new Chart(el.getContext("2d"), {
    type: "line",
    data: {
      labels: ["0", "4", "8", "12", "16", "20", "24"],
      datasets: [
        {
          label: "Hoy",
          data: [1, 2, 1, 3, 2, 1, 0.5],
          fill: true,
          tension: 0.3,
        },
      ],
    },
    options: { responsive: true, maintainAspectRatio: false },
  });
}
function renderMonthChart() {
  const el = document.getElementById("chart-month");
  if (!el) return;
  new Chart(el.getContext("2d"), {
    type: "bar",
    data: {
      labels: ["S1", "S2", "S3", "S4"],
      datasets: [
        {
          label: "Mes",
          data: [120, 90, 140, 100],
          backgroundColor: "rgba(13,110,253,0.8)",
        },
      ],
    },
    options: { responsive: true, maintainAspectRatio: false },
  });
}

function setupVoiceRecording() {
  const startBtn = document.getElementById("start-voice");
  const stopBtn = document.getElementById("stop-voice");
  const display = document.getElementById("voice-text");
  if (!startBtn || !stopBtn || !display) return;
  let recognition = null;
  if ("webkitSpeechRecognition" in window || "SpeechRecognition" in window) {
    const SR = window.SpeechRecognition || window.webkitSpeechRecognition;
    recognition = new SR();
    recognition.lang = "es-ES";
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;
    recognition.addEventListener("result", (e) => {
      const t = e.results[0][0].transcript;
      display.textContent = t;
      const m = t.match(/(\d+(?:[.,]\d{1,2})?)/);
      const amount = m ? parseFloat(m[0].replace(",", ".")) : 0;
      const cat = (t.match(/en\s+(\w+)/i) || [])[1] || "General";
      if (amount > 0) {
        addExpense({ amount, category: cat, note: "Registro por voz: " + t });
        alert("Gasto registrado: $" + amount);
      }
    });
  } else {
    startBtn.disabled = true;
    stopBtn.disabled = true;
    display.textContent = "Reconocimiento no soportado";
  }
  if (recognition) {
    startBtn.addEventListener("click", () => recognition.start());
    stopBtn.addEventListener("click", () => recognition.stop());
  }
}

function addExpense(obj) {
  const ex = JSON.parse(localStorage.getItem(STORAGE.expensesKey) || "[]");
  ex.push({
    id: Date.now(),
    amount: obj.amount,
    category: obj.category,
    note: obj.note,
    date: new Date().toISOString(),
  });
  localStorage.setItem(STORAGE.expensesKey, JSON.stringify(ex));
}

function setupGoalCreator() {
  const container = document.getElementById("goal-creator");
  if (!container) return;
  container.innerHTML = `
    <div class="card-custom p-3">
      <h6>Crear meta</h6>
      <div class="mb-2">
        <input id="goal-title" class="form-control mb-2" placeholder="Título de la meta">
        <select id="goal-period" class="form-select mb-2"><option value="day">Diario</option><option value="month">Mensual</option><option value="year">Anual</option></select>
        <input id="goal-target" class="form-control mb-2" placeholder="Meta (ej. ahorrar 50)">
        <div class="progress mb-2"><div id="goal-progress" class="progress-bar" style="width:20%">20%</div></div>
        <button id="btn-create-goal" class="btn btn-primary">Crear (estético)</button>
      </div>
    </div>
  `;
  document.getElementById("btn-create-goal").addEventListener("click", () => {
    const title = document.getElementById("goal-title").value || "Nueva meta";
    const val =
      parseInt(document.getElementById("goal-target").value || "0", 10) || 0;

    const bar = document.getElementById("goal-progress");
    bar.style.width = "0%";
    bar.textContent = "0%";
    let p = 0;
    const t = setInterval(() => {
      p += 5;
      bar.style.width = p + "%";
      bar.textContent = p + "%";
      if (p >= 100) {
        clearInterval(t);
        alert("Meta creada (visual)");
      }
    }, 80);
  });
}

function renderProfile() {
  const user = JSON.parse(localStorage.getItem(STORAGE.userKey) || "{}");
  if (!user || !user.name) return;
  document.getElementById("profile-name").textContent = user.name;
  document.getElementById("profile-email").textContent = user.email;
  document.getElementById("profile-name-2").textContent = user.name;
  document.getElementById("profile-email-2").textContent = user.email;
  document.getElementById("avatar-box").textContent =
    user.avatar ||
    user.name
      .split(" ")
      .map((s) => s[0])
      .join("")
      .slice(0, 2)
      .toUpperCase();
  updateCoinsUI();

  const apps = JSON.parse(localStorage.getItem(STORAGE.appsKey) || "[]");
  const appsList = document.getElementById("apps-list");
  appsList.innerHTML = "";
  apps.forEach((a, i) => {
    const div = document.createElement("div");
    div.className = "app-item";
    div.innerHTML = `<div><i class="bi bi-bank2 me-2"></i><strong>${a}</strong></div><div><button class="btn btn-sm btn-outline-danger remove-app" data-index="${i}">Quitar</button></div>`;
    appsList.appendChild(div);
  });
  document.getElementById("btn-add-app").addEventListener("click", () => {
    const n = prompt("Nombre de la app/banco");
    if (n) {
      apps.push(n);
      localStorage.setItem(STORAGE.appsKey, JSON.stringify(apps));
      renderProfile();
    }
  });
  document.querySelectorAll(".remove-app").forEach((b) =>
    b.addEventListener("click", (e) => {
      const idx = parseInt(e.target.dataset.index, 10);
      apps.splice(idx, 1);
      localStorage.setItem(STORAGE.appsKey, JSON.stringify(apps));
      renderProfile();
    })
  );
}

function registerUser() {
  const n = document.getElementById("reg-name").value.trim();
  const e = document.getElementById("reg-email").value.trim();
  const p = document.getElementById("reg-pass").value.trim();
  if (!n || !e || !p) {
    alert("Completa los campos");
    return;
  }
  localStorage.setItem(
    STORAGE.userKey,
    JSON.stringify({
      name: n,
      email: e,
      avatar: n
        .split(" ")
        .map((s) => s[0])
        .join("")
        .slice(0, 2)
        .toUpperCase(),
    })
  );
  localStorage.setItem(STORAGE.coinsKey, "50");
  window.location.href = "/index.html";
}

document.addEventListener("DOMContentLoaded", () => {
  ensureDemo();
  loadNavIfNeeded();
  renderWeekChart();
  renderDayChart();
  renderMonthChart();
  setupVoiceRecording();
  setupGoalCreator();
  if (document.getElementById("profile-area")) renderProfile();
});
