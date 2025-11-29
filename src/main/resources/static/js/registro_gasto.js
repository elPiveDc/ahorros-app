// /js/registro_gasto.js
// Conecta el HTML con el backend usando js/service/gastos.js

/** Guardar gasto desde formulario */
document.getElementById("formGasto").addEventListener("submit", async (e) => {
  e.preventDefault();
  const monto = parseFloat(document.getElementById("monto").value);
  const categoria = document.getElementById("categoria").value;
  const tipoRegistro = document.getElementById("tipoRegistro").value;
  const descripcion = document.getElementById("descripcion").value;

  const gasto = {
    monto,
    categoria,
    descripcion,
    tipoRegistro,
    archivoUrl: null,
    fecha: new Date().toISOString().split("T")[0],
  };

  const res = await apiCrearGasto(gasto);
  alert(res.ok ? "✅ Gasto registrado!" : "❌ Error al registrar gasto");
});

/** OCR con foto (subida o cámara) */
document.getElementById("btnOCR").addEventListener("click", async () => {
  const file = document.getElementById("fotoBoleta").files[0];
  if (!file) return alert("Toma o selecciona una foto primero");

  // Mostrar modal de cargando
  const loadingModal = new bootstrap.Modal(
    document.getElementById("loadingModal")
  );
  loadingModal.show();

  const reader = new FileReader();
  reader.onload = async () => {
    try {
      const {
        data: { text },
      } = await Tesseract.recognize(reader.result, "spa");
      document.getElementById("ocrResultado").innerText =
        "Texto detectado: " + text;

      // Buscar monto en el texto (ejemplo simple: primer número con decimales)
      const match = text.match(/\d+(\.\d{1,2})?/);
      const monto = match ? parseFloat(match[0]) : null;

      if (monto) {
        const categoria = document.getElementById("categoria").value || "otros";
        const gasto = {
          monto,
          categoria,
          descripcion: "OCR boleta",
          tipoRegistro: "foto",
          archivoUrl: "localfile://" + file.name,
          fecha: new Date().toISOString().split("T")[0],
        };
        const res = await apiCrearGasto(gasto);
        alert(
          res.ok
            ? "📸 Gasto registrado desde foto!"
            : "❌ Error al registrar gasto"
        );
      } else {
        alert("⚠️ No se pudo detectar monto en la boleta");
      }
    } finally {
      // Ocultar modal al terminar
      loadingModal.hide();
    }
  };
  reader.readAsDataURL(file);
});

/** Registro por voz */
document.getElementById("btnVoz").addEventListener("click", () => {
  const SpeechRecognition =
    window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRecognition)
    return alert("Tu navegador no soporta reconocimiento de voz");

  const recognition = new SpeechRecognition();
  recognition.lang = "es-ES";
  recognition.start();

  recognition.onresult = async (event) => {
    const texto = event.results[0][0].transcript;
    document.getElementById("vozResultado").innerText =
      "Texto detectado: " + texto;

    // Buscar monto en el dictado
    const match = texto.match(/\d+(\.\d{1,2})?/);
    const monto = match ? parseFloat(match[0]) : null;

    // Buscar categoría básica en el dictado
    let categoria = "otros";
    if (texto.includes("comida")) categoria = "alimentacion";
    else if (texto.includes("bus") || texto.includes("taxi"))
      categoria = "transporte";
    else if (texto.includes("luz") || texto.includes("agua"))
      categoria = "servicios";

    if (monto) {
      const gasto = {
        monto,
        categoria,
        descripcion: texto,
        tipoRegistro: "voz",
        archivoUrl: null,
        fecha: new Date().toISOString().split("T")[0],
      };
      const res = await apiCrearGasto(gasto);
      alert(
        res.ok ? "🎙️ Gasto registrado por voz!" : "❌ Error al registrar gasto"
      );
    } else {
      alert("⚠️ No se detectó monto en el dictado");
    }
  };
});
