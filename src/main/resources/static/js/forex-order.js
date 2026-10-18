
document.addEventListener("DOMContentLoaded", function () {

    const orderForm = document.getElementById("orderForm");
    const devizaparInput = document.getElementById("devizapar");
    const unitsInput = document.getElementById("units");

    const longButton = document.getElementById("longButton");
    const shortButton = document.getElementById("shortButton");

    const sikerUzenet = document.getElementById("sikerUzenet");
    const hibaUzenet = document.getElementById("hibaUzenet");
    const orderResult = document.getElementById("orderResult");

    // Megakadályozzuk az űrlap hagyományos elküldését
    orderForm.addEventListener("submit", function (event) {
        event.preventDefault();
    });

    // Long gomb eseménykezelője
    longButton.addEventListener("click", function () {
        megbizasKuldes("long");
    });

    // Short gomb eseménykezelője
    shortButton.addEventListener("click", function () {
        megbizasKuldes("short");
    });

    // Megbízás feldolgozása és elküldése
    async function megbizasKuldes(irany) {

        sikerUzenet.classList.add("d-none");
        hibaUzenet.classList.add("d-none");

        const devizapar = devizaparInput.value;
        const units = Number(unitsInput.value);

        // Űrlap ellenőrzése
        if (!orderForm.reportValidity()) {
            return;
        }

        if (!Number.isInteger(units) || units < 1 || units > 100) {
            hibaMegjelenites(
                "Az egységek száma 1 és 100 közötti egész szám lehet."
            );
            return;
        }

        const iranySzoveg = irany === "long"
            ? "Long (vételi)"
            : "Short (eladási)";

        // Felhasználói megerősítés
        const megerosites = window.confirm(
            `Biztosan elküldöd a következő DEMO megbízást?\n\n` +
            `Irány: ${iranySzoveg}\n` +
            `Devizapár: ${devizapar.replace("_", "/")}\n` +
            `Mennyiség: ${units} egység\n\n` +
            `A megbízás az OANDA Practice számlára kerül.`
        );

        if (!megerosites) {
            return;
        }

        // Kétszeri kattintás megakadályozása
        longButton.disabled = true;
        shortButton.disabled = true;

        orderResult.textContent = "Megbízás feldolgozása...";

        try {

            const params = new URLSearchParams({
                devizapar: devizapar,
                units: units.toString()
            });

            // Spring Boot REST API meghívása
            const response = await fetch(
                `/forex/order/${irany}?${params.toString()}`,
                {
                    method: "POST",
                    headers: {
                        "Accept": "application/json"
                    }
                }
            );

            if (!response.ok) {
                throw new Error(
                    `A megbízás sikertelen. HTTP státuszkód: ${response.status}`
                );
            }

            // OANDA válasz feldolgozása
            const eredmeny = await response.json();

            // Teljes válasz megjelenítése
            orderResult.textContent = JSON.stringify(
                eredmeny,
                null,
                2
            );

            // Végrehajtás ellenőrzése
            if (eredmeny.orderFillTransaction) {

                sikerUzenet.textContent =
                    `${iranySzoveg} megbízás végrehajtva a demo számlán.`;

                sikerUzenet.classList.remove("d-none");

            } else if (eredmeny.orderCancelTransaction) {

                hibaMegjelenites(
                    "Az OANDA törölte a megbízást. " +
                    "Részletek az API-válaszban."
                );

            } else {

                hibaMegjelenites(
                    "A megbízás állapota nem egyértelmű. " +
                    "Ellenőrizd az OANDA-választ."
                );
            }

        } catch (error) {

            orderResult.textContent =
                "A kérés eredménye nem ellenőrizhető.";

            hibaMegjelenites(
                error.message +
                " Ellenőrizd a számla állapotát, mielőtt újra próbálkozol."
            );

        } finally {

            // Gombok újbóli engedélyezése
            longButton.disabled = false;
            shortButton.disabled = false;
        }
    }

    // Hibaüzenet megjelenítése
    function hibaMegjelenites(uzenet) {
        hibaUzenet.textContent = uzenet;
        hibaUzenet.classList.remove("d-none");
    }
});
