
document.addEventListener("DOMContentLoaded", function () {

    const orderForm = document.getElementById("orderForm");
    const devizaparInput = document.getElementById("devizapar");
    const unitsInput = document.getElementById("units");

    const orderButton = document.getElementById("orderButton");
    const iranyInformacio = document.getElementById("iranyInformacio");

    const sikerUzenet = document.getElementById("sikerUzenet");
    const hibaUzenet = document.getElementById("hibaUzenet");
    const orderResult = document.getElementById("orderResult");

    // A mennyiség alapján megjelenítjük a kereskedési irányt
    function iranyFrissitese() {

        const units = Number(unitsInput.value);

        iranyInformacio.classList.remove(
            "alert-info",
            "alert-success",
            "alert-danger",
            "alert-warning"
        );

        if (unitsInput.value.trim() === "" ||
            !Number.isInteger(units) ||
            units === 0 ||
            units < -100 ||
            units > 100) {

            iranyInformacio.textContent =
                "Adj meg egy -100 és 100 közötti egész számot, nulla kivételével.";

            iranyInformacio.classList.add("alert-warning");

        } else if (units > 0) {

            iranyInformacio.textContent =
                "Kiválasztott irány: Long (vétel)";

            iranyInformacio.classList.add("alert-success");

        } else {

            iranyInformacio.textContent =
                "Kiválasztott irány: Short (eladás)";

            iranyInformacio.classList.add("alert-danger");
        }
    }

    // Mennyiség módosításakor frissítjük az irányt
    unitsInput.addEventListener("input", iranyFrissitese);

    // Kezdeti irány megjelenítése
    iranyFrissitese();

    // Az űrlap elküldése
    orderForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        sikerUzenet.classList.add("d-none");
        hibaUzenet.classList.add("d-none");

        if (!orderForm.reportValidity()) {
            return;
        }

        const devizapar = devizaparInput.value;
        const units = Number(unitsInput.value);

        // Mennyiség ellenőrzése
        if (unitsInput.value.trim() === "" ||
            !Number.isInteger(units) ||
            units === 0 ||
            units < -100 ||
            units > 100) {

            hibaMegjelenites(
                "Az egységek száma -100 és 100 közötti egész szám lehet, nulla kivételével."
            );

            return;
        }

        const iranySzoveg = units > 0
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

        // Többszöri kattintás megakadályozása
        orderButton.disabled = true;
        orderButton.textContent = "Megbízás küldése...";

        orderResult.textContent = "Megbízás feldolgozása...";

        try {

            const params = new URLSearchParams({
                devizapar: devizapar,
                units: units.toString()
            });

            // Az előjeles mennyiséget kezelő REST végpont
            const response = await fetch(
                `/forex/order/signed?${params.toString()}`,
                {
                    method: "POST",
                    headers: {
                        "Accept": "application/json"
                    }
                }
            );

            const valaszSzoveg = await response.text();

            if (!response.ok) {
                throw new Error(
                    `A megbízás sikertelen. HTTP státuszkód: ${response.status}. ` +
                    valaszSzoveg.slice(0, 300)
                );
            }

            // OANDA JSON-válasz feldolgozása
            const eredmeny = JSON.parse(valaszSzoveg);

            // Teljes válasz megjelenítése
            orderResult.textContent = JSON.stringify(
                eredmeny,
                null,
                2
            );

            // A HTTP 200 önmagában nem jelent végrehajtott megbízást
            if (eredmeny.orderFillTransaction) {

                sikerUzenet.textContent =
                    `${iranySzoveg} megbízás végrehajtva a demo számlán.`;

                sikerUzenet.classList.remove("d-none");

            } else if (eredmeny.orderCancelTransaction) {

                const ok = eredmeny.orderCancelTransaction.reason
                    || "Ismeretlen ok";

                hibaMegjelenites(
                    `Az OANDA törölte a megbízást. Ok: ${ok}. ` +
                    "Részletek az API-válaszban."
                );

            } else if (eredmeny.orderRejectTransaction) {

                const ok = eredmeny.orderRejectTransaction.rejectReason
                    || "Ismeretlen ok";

                hibaMegjelenites(
                    `Az OANDA elutasította a megbízást. Ok: ${ok}.`
                );

            } else {

                hibaMegjelenites(
                    "A megbízás állapota nem egyértelmű. " +
                    "Ellenőrizd az OANDA-választ és a számla állapotát."
                );
            }

        } catch (error) {

            // Nem próbálkozunk automatikus újraküldéssel
            orderResult.textContent =
                "A kérés eredménye nem ellenőrizhető.";

            hibaMegjelenites(
                error.message +
                " Ellenőrizd a számla állapotát, mielőtt újra próbálkozol."
            );

        } finally {

            orderButton.disabled = false;
            orderButton.textContent = "Pozíció nyitása";
        }
    });

    // Hibaüzenet megjelenítése
    function hibaMegjelenites(uzenet) {
        hibaUzenet.textContent = uzenet;
        hibaUzenet.classList.remove("d-none");
    }
});
