
document.addEventListener("DOMContentLoaded", function () {

    const tabla = document.getElementById("poziciokTabla");
    const frissitesButton = document.getElementById("frissitesButton");
    const sikerUzenet = document.getElementById("sikerUzenet");
    const hibaUzenet = document.getElementById("hibaUzenet");
    const uresLista = document.getElementById("uresLista");

    let muveletFolyamatban = false;

    // Üzenetek elrejtése
    function uzenetekTorlese() {
        sikerUzenet.classList.add("d-none");
        hibaUzenet.classList.add("d-none");
        uresLista.classList.add("d-none");
    }

    // Hibaüzenet megjelenítése
    function hibaMegjelenitese(uzenet) {
        hibaUzenet.textContent = uzenet;
        hibaUzenet.classList.remove("d-none");
    }

    // Pozíciók lekérdezése
    async function poziciokBetoltese() {

        if (muveletFolyamatban) {
            return;
        }

        muveletFolyamatban = true;
        frissitesButton.disabled = true;
        frissitesButton.textContent = "Betöltés...";

        tabla.replaceChildren();
        uzenetekTorlese();

        try {
            const response = await fetch("/forex/positions");

            if (!response.ok) {
                throw new Error(
                    "A pozíciók lekérdezése sikertelen. HTTP: "
                    + response.status
                );
            }

            const poziciok = await response.json();

            if (!Array.isArray(poziciok)) {
                throw new Error("Érvénytelen API-válasz.");
            }

            if (poziciok.length === 0) {
                uresLista.classList.remove("d-none");
                return;
            }

            poziciok.forEach(function (pozicio) {

                const sor = document.createElement("tr");

                const adatok = [
                    pozicio.instrument,
                    pozicio.longUnits,
                    pozicio.shortUnits,
                    pozicio.longUnrealizedPL,
                    pozicio.shortUnrealizedPL,
                    pozicio.unrealizedPL
                ];

                adatok.forEach(function (adat, index) {

                    const cella = document.createElement("td");
                    cella.textContent = adat ?? "-";

                    if (index >= 3) {
                        const ertek = Number(adat);

                        if (ertek > 0) {
                            cella.classList.add("text-success");
                        } else if (ertek < 0) {
                            cella.classList.add("text-danger");
                        }
                    }

                    sor.appendChild(cella);
                });

                // Műveletek oszlop
                const muveletekCella = document.createElement("td");

                const longButton = document.createElement("button");
                longButton.type = "button";
                longButton.className = "btn btn-danger btn-sm me-2";
                longButton.textContent = "Long lezárása";

                const shortButton = document.createElement("button");
                shortButton.type = "button";
                shortButton.className = "btn btn-warning btn-sm";
                shortButton.textContent = "Short lezárása";

                // Csak létező pozíció zárható
                longButton.disabled = Number(pozicio.longUnits) <= 0;
                shortButton.disabled = Number(pozicio.shortUnits) >= 0;

                longButton.addEventListener("click", function () {
                    pozicioLezarasa(pozicio.instrument, "long");
                });

                shortButton.addEventListener("click", function () {
                    pozicioLezarasa(pozicio.instrument, "short");
                });

                muveletekCella.appendChild(longButton);
                muveletekCella.appendChild(shortButton);

                sor.appendChild(muveletekCella);
                tabla.appendChild(sor);
            });

        } catch (error) {
            hibaMegjelenitese(error.message);

        } finally {
            muveletFolyamatban = false;
            frissitesButton.disabled = false;
            frissitesButton.textContent = "Frissítés";
        }
    }

    // Long vagy Short pozíció lezárása
    async function pozicioLezarasa(devizapar, irany) {

        if (muveletFolyamatban) {
            return;
        }

        const iranySzoveg = irany === "long" ? "Long" : "Short";

        const megerosites = window.confirm(
            "Biztosan lezárod a(z) " + devizapar
            + " devizapár teljes " + iranySzoveg
            + " pozícióját az OANDA PRACTICE számlán?"
        );

        if (!megerosites) {
            return;
        }

        muveletFolyamatban = true;
        frissitesButton.disabled = true;
        uzenetekTorlese();

        // A többi lezáró gomb letiltása
        tabla.querySelectorAll("button").forEach(function (button) {
            button.disabled = true;
        });

        try {
            const url = "/forex/positions/close/"
                + irany
                + "?devizapar="
                + encodeURIComponent(devizapar);

            const response = await fetch(url, {
                method: "PUT"
            });

            const valaszSzoveg = await response.text();

            let adat;

            try {
                adat = JSON.parse(valaszSzoveg);
            } catch {
                throw new Error(
                    "Nem sikerült értelmezni az OANDA válaszát. "
                    + "Ellenőrizd a számla állapotát újrapróbálkozás előtt."
                );
            }

            if (!response.ok) {
                throw new Error(
                    "A pozíciózárás sikertelen. HTTP: "
                    + response.status
                    + ". Ellenőrizd a számla állapotát."
                );
            }

            // OANDA által visszaigazolt végrehajtás
            const vegrehajtva =
                irany === "long"
                    ? adat.longOrderFillTransaction
                    : adat.shortOrderFillTransaction;

            if (vegrehajtva) {

                // Lista újratöltése előtt feloldjuk a zárolást
                muveletFolyamatban = false;

                await poziciokBetoltese();

                sikerUzenet.textContent =
                    "A(z) " + devizapar + " "
                    + iranySzoveg
                    + " pozíció lezárását az OANDA visszaigazolta.";

                sikerUzenet.classList.remove("d-none");

            } else {

                throw new Error(
                    "Az OANDA nem igazolta vissza a pozíció lezárását. "
                    + "Ellenőrizd a számla állapotát."
                );
            }

        } catch (error) {
            hibaMegjelenitese(error.message);

        } finally {
            muveletFolyamatban = false;
            frissitesButton.disabled = false;

            // A lista frissítésével visszaállítjuk a gombok állapotát
            // akkor is, ha a zárás eredménye bizonytalan.
            await poziciokBetoltese();
        }
    }

    // Frissítés gomb
    frissitesButton.addEventListener(
        "click",
        poziciokBetoltese
    );

    // Automatikus betöltés
    poziciokBetoltese();
});
