
document.addEventListener("DOMContentLoaded", function () {

    const tabla = document.getElementById("poziciokTabla");
    const frissitesButton = document.getElementById("frissitesButton");

    const sikerUzenet = document.getElementById("sikerUzenet");
    const hibaUzenet = document.getElementById("hibaUzenet");
    const uresLista = document.getElementById("uresLista");

    const tradeCloseForm = document.getElementById("tradeCloseForm");
    const tradeIdInput = document.getElementById("tradeId");
    const tradeCloseButton = document.getElementById("tradeCloseButton");
    const closeResult = document.getElementById("closeResult");

    let muveletFolyamatban = false;

    // Siker- és hibaüzenetek törlése
    function uzenetekTorlese() {
        sikerUzenet.classList.add("d-none");
        hibaUzenet.classList.add("d-none");
        sikerUzenet.textContent = "";
        hibaUzenet.textContent = "";
    }

    function sikerMegjelenitese(uzenet) {
        hibaUzenet.classList.add("d-none");
        sikerUzenet.textContent = uzenet;
        sikerUzenet.classList.remove("d-none");
    }

    function hibaMegjelenitese(uzenet) {
        sikerUzenet.classList.add("d-none");
        hibaUzenet.textContent = uzenet;
        hibaUzenet.classList.remove("d-none");
    }

    // Gombok zárolása a művelet alatt
    function gombokAllapota(folyamatban) {

        muveletFolyamatban = folyamatban;

        frissitesButton.disabled = folyamatban;
        tradeCloseButton.disabled = folyamatban;

        tabla.querySelectorAll("button").forEach(function (button) {
            button.disabled = folyamatban;
        });
    }

    // Nyitott pozíciók lekérdezése
    async function poziciokBetoltese(torolUzenetek = true) {

        if (muveletFolyamatban) {
            return;
        }

        gombokAllapota(true);

        frissitesButton.textContent = "Betöltés...";
        tabla.replaceChildren();
        uresLista.classList.add("d-none");

        if (torolUzenetek) {
            uzenetekTorlese();
        }

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

            gombokAllapota(false);
            frissitesButton.textContent = "Frissítés";

            // Az eredetileg nem létező pozíciók gombjai
            // továbbra is tiltva maradnak.
            tabla.querySelectorAll("tr").forEach(function (sor) {
                const cellak = sor.querySelectorAll("td");
                const gombok = sor.querySelectorAll("button");

                if (cellak.length < 3 || gombok.length < 2) {
                    return;
                }

                gombok[0].disabled = Number(cellak[1].textContent) <= 0;
                gombok[1].disabled = Number(cellak[2].textContent) >= 0;
            });
        }
    }

    // Közös válaszfeldolgozás
    async function valaszFeldolgozasa(response) {

        const valaszSzoveg = await response.text();

        let adat;

        try {
            adat = JSON.parse(valaszSzoveg);
        } catch {
            throw new Error(
                "Nem sikerült értelmezni az OANDA válaszát. "
                + "Ellenőrizd a számla állapotát."
            );
        }

        closeResult.textContent = JSON.stringify(adat, null, 2);

        if (!response.ok) {
            throw new Error(
                "A zárási művelet sikertelen. HTTP: "
                + response.status
                + ". Ellenőrizd a számla állapotát."
            );
        }

        return adat;
    }

    // Long vagy Short pozíció lezárása devizapár alapján
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

        gombokAllapota(true);
        uzenetekTorlese();

        closeResult.textContent = "Pozíciózárás folyamatban...";

        let sikeresZaras = false;
        let zarasiHiba = null;

        try {

            const url = "/forex/positions/close/"
                + irany
                + "?devizapar="
                + encodeURIComponent(devizapar);

            const response = await fetch(url, {
                method: "PUT"
            });

            const adat = await valaszFeldolgozasa(response);

            const vegrehajtva = irany === "long"
                ? adat.longOrderFillTransaction
                : adat.shortOrderFillTransaction;

            if (!vegrehajtva) {
                throw new Error(
                    "Az OANDA nem igazolta vissza a pozíció lezárását."
                );
            }

            sikeresZaras = true;

        } catch (error) {
            zarasiHiba = error.message;
        }

        gombokAllapota(false);

        // Frissítjük a listát, de nem töröljük a művelet üzenetét
        await poziciokBetoltese(false);

        if (sikeresZaras) {
            sikerMegjelenitese(
                "A(z) " + devizapar + " "
                + iranySzoveg
                + " pozíció lezárását az OANDA visszaigazolta."
            );
        } else {
            hibaMegjelenitese(
                zarasiHiba
                + " Ellenőrizd a számla állapotát újrapróbálkozás előtt."
            );
        }
    }

    // ÚJ: konkrét ügylet lezárása tradeId alapján
    tradeCloseForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        if (muveletFolyamatban) {
            return;
        }

        if (!tradeCloseForm.reportValidity()) {
            return;
        }

        const tradeIdSzoveg = tradeIdInput.value.trim();
        const tradeId = Number(tradeIdSzoveg);

        if (!/^[1-9]\d*$/.test(tradeIdSzoveg)
            || !Number.isSafeInteger(tradeId)) {

            hibaMegjelenitese(
                "A tradeId csak pozitív egész szám lehet."
            );
            return;
        }

        const megerosites = window.confirm(
            "Biztosan lezárod a(z) "
            + tradeIdSzoveg
            + " azonosítójú ügyletet az OANDA PRACTICE számlán?"
        );

        if (!megerosites) {
            return;
        }

        gombokAllapota(true);
        uzenetekTorlese();

        closeResult.textContent = "Ügyletzárás folyamatban...";

        let sikeresZaras = false;
        let zarasiHiba = null;

        try {

            const response = await fetch(
                "/forex/trades/"
                + encodeURIComponent(tradeIdSzoveg)
                + "/close",
                {
                    method: "PUT"
                }
            );

            const adat = await valaszFeldolgozasa(response);

            // Az OANDA trade-close válaszában
            // az orderFillTransaction igazolja a teljesítést.
            if (!adat.orderFillTransaction) {
                throw new Error(
                    "Az OANDA nem igazolta vissza az ügylet lezárását."
                );
            }

            sikeresZaras = true;

        } catch (error) {
            zarasiHiba = error.message;
        }

        gombokAllapota(false);

        // A pozíciók újratöltése nem törli az eredményüzenetet
        await poziciokBetoltese(false);

        if (sikeresZaras) {
            sikerMegjelenitese(
                "A(z) " + tradeIdSzoveg
                + " azonosítójú ügyletet az OANDA sikeresen lezárta."
            );

            tradeIdInput.value = "";

        } else {
            hibaMegjelenitese(
                zarasiHiba
                + " Ellenőrizd a számla állapotát újrapróbálkozás előtt."
            );
        }
    });

    // Frissítés gomb
    frissitesButton.addEventListener("click", function () {
        poziciokBetoltese();
    });

    // Automatikus betöltés
    poziciokBetoltese();
});
