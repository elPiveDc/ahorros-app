document.addEventListener("DOMContentLoaded", () => {
  const ctx1 = document.getElementById("chart-daily")?.getContext("2d");
  if (ctx1) {
    new Chart(ctx1, {
      type: "bar",
      data: {
        labels: ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"],
        datasets: [
          {
            label: "Gastos S/",
            data: [45, 80, 60, 70, 120, 30, 90],
            borderRadius: 6,
            backgroundColor: "rgba(255,255,255,0.12)",
          },
        ],
      },
      options: {
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, ticks: { color: "#bbb" } },
          x: { ticks: { color: "#bbb" } },
        },
      },
    });
  }

  const ctx2 = document.getElementById("chart-month")?.getContext("2d");
  if (ctx2) {
    new Chart(ctx2, {
      type: "doughnut",
      data: {
        labels: ["Compras", "Transporte", "Comida", "Otros"],
        datasets: [
          {
            data: [40, 25, 20, 15],
            backgroundColor: ["#7c3aed", "#06b6d4", "#10b981", "#f97316"],
          },
        ],
      },
      options: {
        plugins: { legend: { position: "bottom", labels: { color: "#ddd" } } },
      },
    });
  }
});
