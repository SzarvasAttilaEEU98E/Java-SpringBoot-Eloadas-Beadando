
/*
 * MNB SOAP árfolyamlekérdezés
 * Bootstrap táblázat és Chart.js grafikon
 */

// HTML-elemek megkeresése
const arfolyamForm = document.getElementById("arfolyamForm");
const arfolyamTabla = document.getElementById("arfolyamTabla");
const hibaUzenet = document.getElementById("hibaUzenet");

// A jelenleg megjelenített grafikon
let arfolyamChart = null;


// Űrlap elküldésének figyelése
arfolyamForm.addEventListener("submit", async function (event) {

    // Megakadályozzuk az oldal újratöltését
    event.preventDefault();

    // Felhasználó által kiválasztott adatok
    const deviza = document.getElementById("deviza").value;
    const kezdoDatum = document.getElementById("kezdoDatum").value;
    const vegDatum = document.getElementById("vegDatum").value;

    // Korábbi táblázat törlése
    arfolyamTabla.innerHTML = "";

    // Korábbi hibaüzenet elrejtése
    hibaUzenet.classList.add("d-none");

    // Korábbi grafikon törlése
    if (arfolyamChart !== null) {
        arfolyamChart.destroy();
        arfolyamChart = null;
    }

    // Dátumtartomány ellenőrzése
    if (kezdoDatum > vegDatum) {

        hibaUzenet.textContent =
            "A kezdődátum nem lehet későbbi a végdátumnál.";

        hibaUzenet.classList.remove("d-none");

        return;
    }

    // Spring Boot API URL összeállítása
    const url = `/soap?deviza=${encodeURIComponent(deviza)}`
        + `&kezdoDatum=${encodeURIComponent(kezdoDatum)}`
        + `&vegDatum=${encodeURIComponent(vegDatum)}`;

    try {

        // HTTP GET-kérés a Spring Boot backend felé
        const response = await fetch(url);

        // HTTP-válasz ellenőrzése
        if (!response.ok) {
            throw new Error(
                "Az árfolyamlekérdezés sikertelen."
            );
        }

        // JSON-válasz feldolgozása
        const arfolyamok = await response.json();

        // Üres eredmény ellenőrzése
        if (arfolyamok.length === 0) {

            hibaUzenet.textContent =
                "A megadott időszakra nem található árfolyam.";

            hibaUzenet.classList.remove("d-none");

            return;
        }

        // Táblázat feltöltése
        arfolyamok.forEach(function (arfolyam) {

            // Új táblázatsor létrehozása
            const sor = document.createElement("tr");

            // Dátum cella
            const datumCella = document.createElement("td");
            datumCella.textContent = arfolyam.datum;

            // Deviza cella
            const devizaCella = document.createElement("td");
            devizaCella.textContent = arfolyam.deviza;

            // Árfolyam cella
            const arfolyamCella = document.createElement("td");
            arfolyamCella.textContent = arfolyam.arfolyam;

            // Cellák hozzáadása a sorhoz
            sor.appendChild(datumCella);
            sor.appendChild(devizaCella);
            sor.appendChild(arfolyamCella);

            // Sor hozzáadása a táblázathoz
            arfolyamTabla.appendChild(sor);
        });

        // Grafikon megjelenítése
        grafikonRajzolas(arfolyamok, deviza);

    } catch (error) {

        // Hibaüzenet megjelenítése
        hibaUzenet.textContent = error.message;
        hibaUzenet.classList.remove("d-none");

        // Hiba kiírása a böngésző konzoljára
        console.error("MNB SOAP hiba:", error);
    }
});


// Chart.js grafikon elkészítése
function grafikonRajzolas(arfolyamok, deviza) {

    // Árfolyamok rendezése dátum szerint
    const rendezettArfolyamok = [...arfolyamok].sort(
        (a, b) => a.datum.localeCompare(b.datum)
    );

    // Dátumok kigyűjtése
    const datumok = rendezettArfolyamok.map(
        arfolyam => arfolyam.datum
    );

    // Árfolyamértékek kigyűjtése
    const ertekek = rendezettArfolyamok.map(
        arfolyam => arfolyam.arfolyam
    );

    // Canvas elem megkeresése
    const canvas = document.getElementById("arfolyamGrafikon");

    // Chart.js grafikon létrehozása
    arfolyamChart = new Chart(canvas, {

        // Vonaldiagram
        type: "line",

        // Grafikon adatai
        data: {

            // X tengely: dátumok
            labels: datumok,

            datasets: [{

                // Grafikon megnevezése
                label: `${deviza}/HUF árfolyam`,

                // Y tengely: árfolyamok
                data: ertekek,

                // Vonal színe
                borderColor: "rgb(13, 110, 253)",

                // Grafikon alatti terület színe
                backgroundColor: "rgba(13, 110, 253, 0.1)",

                // Vonal vastagsága
                borderWidth: 2,

                // Vonal enyhe görbítése
                tension: 0.2,

                // Vonal alatti terület kitöltése
                fill: true
            }]
        },

        // Grafikon beállításai
        options: {

            // Reszponzív megjelenítés
            responsive: true,

            // Tengelyek beállítása
            scales: {

                // Függőleges tengely
                y: {
                    title: {
                        display: true,
                        text: "Árfolyam (HUF)"
                    }
                },

                // Vízszintes tengely
                x: {
                    title: {
                        display: true,
                        text: "Dátum"
                    }
                }
            }
        }
    });
}
