
const historyForm = document.getElementById("historyForm");
const devizaparSelect = document.getElementById("devizapar");
const granularitySelect = document.getElementById("granularity");
const hibaUzenet = document.getElementById("hibaUzenet");
const historyTabla = document.getElementById("historyTabla");
const lekerdezesGomb = document.getElementById("lekerdezesGomb");

let historyChart = null;

function hibaMegjelenitese(uzenet) {
    hibaUzenet.textContent = uzenet;
    hibaUzenet.classList.remove("d-none");
}

function hibaElrejtese() {
    hibaUzenet.textContent = "";
    hibaUzenet.classList.add("d-none");
}

function tablaMegjelenitese(arfolyamok) {
    historyTabla.innerHTML = "";

    if (arfolyamok.length === 0) {
        const sor = document.createElement("tr");
        const cella = document.createElement("td");

        cella.colSpan = 5;
        cella.className = "text-center";
        cella.textContent = "Nem található historikus adat.";

        sor.appendChild(cella);
        historyTabla.appendChild(sor);
        return;
    }

    arfolyamok.forEach(arfolyam => {
        const sor = document.createElement("tr");

        const ertekek = [
            new Date(arfolyam.time).toLocaleString("hu-HU"),
            arfolyam.open,
            arfolyam.high,
            arfolyam.low,
            arfolyam.close
        ];

        ertekek.forEach(ertek => {
            const cella = document.createElement("td");
            cella.textContent = ertek ?? "-";
            sor.appendChild(cella);
        });

        historyTabla.appendChild(sor);
    });
}

function grafikonMegjelenitese(arfolyamok) {
    const canvas = document.getElementById("historyChart");

    if (historyChart !== null) {
        historyChart.destroy();
        historyChart = null;
    }

    if (arfolyamok.length === 0) {
        return;
    }

    const idopontok = arfolyamok.map(arfolyam =>
        new Date(arfolyam.time).toLocaleString("hu-HU")
    );

    const zaroArak = arfolyamok.map(arfolyam =>
        Number(arfolyam.close)
    );

    historyChart = new Chart(canvas, {
        type: "line",
        data: {
            labels: idopontok,
            datasets: [{
                label: "Záróár",
                data: zaroArak,
                borderColor: "#1abc9c",
                backgroundColor: "rgba(26, 188, 156, 0.1)",
                tension: 0.2,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: false
                }
            }
        }
    });
}

async function historikusArfolyamokLekerdezese(event) {
    event.preventDefault();

    hibaElrejtese();

    const devizapar = devizaparSelect.value;
    const granularity = granularitySelect.value;

    const params = new URLSearchParams({
        devizapar: devizapar,
        granularity: granularity
    });

    lekerdezesGomb.disabled = true;
    lekerdezesGomb.textContent = "Lekérdezés folyamatban...";

    try {
        const response = await fetch(
            `/forex/history?${params.toString()}`
        );

        if (!response.ok) {
            throw new Error(
                `Sikertelen lekérdezés. HTTP: ${response.status}`
            );
        }

        const arfolyamok = await response.json();

        if (!Array.isArray(arfolyamok)) {
            throw new Error("Érvénytelen API-válasz.");
        }

        // Az OANDA időrendben adja vissza a gyertyákat.
        // A legutolsó 10 gyertyát jelenítjük meg.
        const utolsoTiz = arfolyamok.slice(-10);

        tablaMegjelenitese(utolsoTiz);
        grafikonMegjelenitese(utolsoTiz);

    } catch (error) {
        historyTabla.innerHTML = "";

        if (historyChart !== null) {
            historyChart.destroy();
            historyChart = null;
        }

        hibaMegjelenitese(error.message);

    } finally {
        lekerdezesGomb.disabled = false;
        lekerdezesGomb.textContent =
            "Utolsó 10 ár lekérdezése";
    }
}

historyForm.addEventListener(
    "submit",
    historikusArfolyamokLekerdezese
);
