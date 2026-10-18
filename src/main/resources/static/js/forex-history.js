
document.addEventListener("DOMContentLoaded", function () {

    const historyForm = document.getElementById("historyForm");
    const devizaparInput = document.getElementById("devizapar");
    const kezdoDatumInput = document.getElementById("kezdoDatum");
    const vegDatumInput = document.getElementById("vegDatum");
    const hibaUzenet = document.getElementById("hibaUzenet");

    const chartCanvas = document.getElementById("historyChart");

    // A korábbi grafikon példányát ebben tároljuk
    let historyChart = null;

    // Alapértelmezett dátumok beállítása
    const maiDatum = new Date();

    const vegDatum = new Date(maiDatum);
    vegDatum.setDate(vegDatum.getDate() - 1);

    const kezdoDatum = new Date(vegDatum);
    kezdoDatum.setDate(kezdoDatum.getDate() - 30);

    // Dátum formázása YYYY-MM-DD alakra, helyi idő szerint
    function datumFormazas(datum) {
        const ev = datum.getFullYear();
        const honap = String(datum.getMonth() + 1).padStart(2, "0");
        const nap = String(datum.getDate()).padStart(2, "0");

        return `${ev}-${honap}-${nap}`;
    }

    kezdoDatumInput.value = datumFormazas(kezdoDatum);
    vegDatumInput.value = datumFormazas(vegDatum);

    // Az űrlap elküldésének kezelése
    historyForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        hibaUzenet.classList.add("d-none");
        hibaUzenet.textContent = "";

        const devizapar = devizaparInput.value;
        const kezdoDatum = kezdoDatumInput.value;
        const vegDatum = vegDatumInput.value;

        // Dátumtartomány ellenőrzése
        if (kezdoDatum > vegDatum) {
            hibaMegjelenites(
                "A kezdő dátum nem lehet későbbi, mint a végső dátum."
            );
            return;
        }

        const params = new URLSearchParams({
            devizapar: devizapar,
            kezdoDatum: kezdoDatum,
            vegDatum: vegDatum
        });

        try {

            // Saját Spring Boot REST API meghívása
            const response = await fetch(
                `/forex/history?${params.toString()}`
            );

            if (!response.ok) {
                throw new Error(
                    `Sikertelen árfolyamlekérdezés. HTTP: ${response.status}`
                );
            }

            // JSON-válasz feldolgozása
            const arfolyamok = await response.json();

            if (!Array.isArray(arfolyamok) || arfolyamok.length === 0) {
                throw new Error(
                    "A kiválasztott időszakban nem található árfolyamadat."
                );
            }

            // Grafikon megjelenítése
            grafikonMegjelenites(arfolyamok, devizapar);

        } catch (error) {

            hibaMegjelenites(error.message);

        }
    });

    // Hibaüzenet megjelenítése
    function hibaMegjelenites(uzenet) {
        hibaUzenet.textContent = uzenet;
        hibaUzenet.classList.remove("d-none");
    }

    // Történelmi árfolyamgrafikon kirajzolása
    function grafikonMegjelenites(arfolyamok, devizapar) {

        // Csak a lezárt gyertyákat használjuk
        const lezartArfolyamok = arfolyamok.filter(
            arfolyam => arfolyam.complete
        );

        if (lezartArfolyamok.length === 0) {
            throw new Error("Nincsenek lezárt gyertyaadatok.");
        }

        // Időpontok előkészítése
        const datumok = lezartArfolyamok.map(
            arfolyam => new Date(arfolyam.time).toLocaleDateString("hu-HU")
        );

        // Záróárfolyamok előkészítése
        const zaroArfolyamok = lezartArfolyamok.map(
            arfolyam => Number(arfolyam.close)
        );

        // Korábbi grafikon törlése
        if (historyChart !== null) {
            historyChart.destroy();
        }

        // Új Chart.js grafikon létrehozása
        historyChart = new Chart(chartCanvas, {

            type: "line",

            data: {
                labels: datumok,

                datasets: [{
                    label: `${devizapar.replace("_", "/")} záróárfolyam`,
                    data: zaroArfolyamok,
                    borderColor: "#0d6efd",
                    backgroundColor: "rgba(13, 110, 253, 0.1)",
                    borderWidth: 2,
                    tension: 0.2,
                    pointRadius: 3,
                    fill: true
                }]
            },

            options: {
                responsive: true,
                maintainAspectRatio: false,

                plugins: {
                    legend: {
                        display: true
                    },

                    tooltip: {
                        mode: "index",
                        intersect: false
                    }
                },

                scales: {
                    x: {
                        title: {
                            display: true,
                            text: "Dátum"
                        }
                    },

                    y: {
                        title: {
                            display: true,
                            text: "Záróárfolyam"
                        }
                    }
                }
            }
        });
    }
});
