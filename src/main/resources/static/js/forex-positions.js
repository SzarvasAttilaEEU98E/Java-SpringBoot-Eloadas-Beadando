
document.addEventListener("DOMContentLoaded", function () {

    const tradekTabla =
        document.getElementById("tradekTabla");

    const frissitesButton =
        document.getElementById("frissitesButton");

    const sikerUzenet =
        document.getElementById("sikerUzenet");

    const hibaUzenet =
        document.getElementById("hibaUzenet");

    const uresLista =
        document.getElementById("uresLista");

    const tradeCloseForm =
        document.getElementById("tradeCloseForm");

    const tradeIdInput =
        document.getElementById("tradeId");

    const tradeCloseButton =
        document.getElementById("tradeCloseButton");

    const closeResult =
        document.getElementById("closeResult");

    let muveletFolyamatban = false;
    let nyitottTradeIdk = new Set();

    // Üzenetek törlése
    function uzenetekTorlese() {
        sikerUzenet.textContent = "";
        hibaUzenet.textContent = "";

        sikerUzenet.classList.add("d-none");
        hibaUzenet.classList.add("d-none");
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

    // Gombok engedélyezése vagy letiltása
    function gombokAllapota(folyamatban) {

        muveletFolyamatban = folyamatban;

        frissitesButton.disabled = folyamatban;
        tradeCloseButton.disabled = folyamatban;

        tradekTabla.querySelectorAll("button")
            .forEach(function (button) {

                const alapbolTiltott =
                    button.dataset.alapbolTiltott === "true";

                button.disabled =
                    folyamatban || alapbolTiltott;
            });
    }

    // Táblázatcella létrehozása
    function cellaHozzaadasa(sor, ertek, eredmeny = false) {

        const cella = document.createElement("td");
        cella.textContent = ertek ?? "-";

        if (eredmeny && ertek !== null
            && ertek !== undefined) {

            const szam = Number(ertek);

            if (szam > 0) {
                cella.classList.add("text-success");
            } else if (szam < 0) {
                cella.classList.add("text-danger");
            }
        }

        sor.appendChild(cella);

        return cella;
    }

    // Gomb létrehozása
    function gombLetrehozasa(
        felirat,
        cssOsztaly,
        kattintas,
        tiltott = false
    ) {

        const button = document.createElement("button");

        button.type = "button";
        button.className = cssOsztaly;
        button.textContent = felirat;

        button.dataset.alapbolTiltott =
            String(tiltott);

        button.disabled = tiltott || muveletFolyamatban;

        button.addEventListener("click", kattintas);

        return button;
    }

    // Nyitott ügyletek megjelenítése
    function tradekMegjelenitese(tradek) {

        tradekTabla.replaceChildren();
        uresLista.classList.add("d-none");

        nyitottTradeIdk = new Set();

        if (tradek.length === 0) {
            uresLista.classList.remove("d-none");
            return;
        }

        tradek.forEach(function (trade) {

            const tradeId = String(trade.id);
            const devizapar = trade.instrument;
            const egysegek = Number(trade.currentUnits);

            nyitottTradeIdk.add(tradeId);

            const sor = document.createElement("tr");

            cellaHozzaadasa(sor, tradeId);
            cellaHozzaadasa(sor, devizapar);

            // Irány
            let irany = "Nincs nyitott egység";

            if (egysegek > 0) {
                irany = "Long";
            } else if (egysegek < 0) {
                irany = "Short";
            }

            const iranyCella =
                cellaHozzaadasa(sor, irany);

            if (irany === "Long") {
                iranyCella.classList.add(
                    "text-success",
                    "fw-bold"
                );
            } else if (irany === "Short") {
                iranyCella.classList.add(
                    "text-danger",
                    "fw-bold"
                );
            }

            // Egység és eredmény
            cellaHozzaadasa(sor, trade.currentUnits);

            cellaHozzaadasa(
                sor,
                trade.unrealizedPL,
                true
            );

            // Műveleti gombok
            const muveletekCella =
                document.createElement("td");

            const gombKontener =
                document.createElement("div");

            gombKontener.className =
                "d-flex flex-wrap gap-2";

            // Long pozíció lezárása
            const longButton = gombLetrehozasa(
                "Long lezárása",
                "btn btn-danger btn-sm",
                function () {
                    pozicioLezarasa(devizapar, "long");
                },
                egysegek <= 0
            );

            // Short pozíció lezárása
            const shortButton = gombLetrehozasa(
                "Short lezárása",
                "btn btn-warning btn-sm",
                function () {
                    pozicioLezarasa(devizapar, "short");
                },
                egysegek >= 0
            );

            // Trade ID kiválasztása
            const kivalasztasButton = gombLetrehozasa(
                "Kiválasztás",
                "btn btn-outline-primary btn-sm",
                function () {

                    if (muveletFolyamatban) {
                        return;
                    }

                    tradeIdInput.value = tradeId;

                    tradeIdInput.scrollIntoView({
                        behavior: "smooth",
                        block: "center"
                    });

                    tradeIdInput.focus();
                }
            );

            gombKontener.appendChild(longButton);
            gombKontener.appendChild(shortButton);
            gombKontener.appendChild(kivalasztasButton);

            muveletekCella.appendChild(gombKontener);

            sor.appendChild(muveletekCella);
            tradekTabla.appendChild(sor);
        });
    }

    // Ügyletek lekérdezése
    async function tradekBetoltese(torolUzenetek = true) {

        if (muveletFolyamatban) {
            return false;
        }

        gombokAllapota(true);
        frissitesButton.textContent = "Betöltés...";

        if (torolUzenetek) {
            uzenetekTorlese();
        }

        try {

            const response = await fetch("/forex/trades");

            if (!response.ok) {
                throw new Error(
                    "A nyitott ügyletek lekérdezése sikertelen. HTTP: "
                    + response.status
                );
            }

            const adat = await response.json();

            if (!adat || !Array.isArray(adat.trades)) {
                throw new Error(
                    "Érvénytelen ügyletlekérdezési válasz."
                );
            }

            tradekMegjelenitese(adat.trades);

            return true;

        } catch (error) {

            tradekTabla.replaceChildren();
            nyitottTradeIdk.clear();
            uresLista.classList.add("d-none");

            hibaMegjelenitese(error.message);

            return false;

        } finally {

            gombokAllapota(false);
            frissitesButton.textContent = "Frissítés";
        }
    }

    // API-válasz ellenőrzése
    async function valaszFeldolgozasa(response) {

        const valaszSzoveg = await response.text();

        let adat;

        try {
            adat = JSON.parse(valaszSzoveg);
        } catch {

            closeResult.textContent = valaszSzoveg;

            throw new Error(
                "Nem sikerült értelmezni az API válaszát."
            );
        }

        closeResult.textContent =
            JSON.stringify(adat, null, 2);

        if (!response.ok) {
            throw new Error(
                "A zárási művelet sikertelen. HTTP: "
                + response.status
                + ". Ellenőrizd a számla állapotát."
            );
        }

        return adat;
    }

    // Közös lezárás utáni frissítés
    async function zarasUtaniFrissites() {

        gombokAllapota(false);

        return await tradekBetoltese(false);
    }

    // Long vagy Short pozíció teljes lezárása
    async function pozicioLezarasa(devizapar, irany) {

        if (muveletFolyamatban) {
            return;
        }

        const iranySzoveg =
            irany === "long" ? "Long" : "Short";

        const megerosites = window.confirm(
            "FIGYELEM!\n\n"
            + "A(z) " + devizapar + " devizapár "
            + "TELJES " + iranySzoveg
            + " pozícióját lezárod.\n\n"
            + "Ez több nyitott ügyletet is érinthet.\n\n"
            + "Biztosan folytatod az OANDA PRACTICE számlán?"
        );

        if (!megerosites) {
            return;
        }

        gombokAllapota(true);
        uzenetekTorlese();

        closeResult.textContent =
            "Pozíciózárás folyamatban...";

        let sikeresZaras = false;
        let zarasiHiba = null;

        try {

            const url =
                "/forex/positions/close/"
                + irany
                + "?devizapar="
                + encodeURIComponent(devizapar);

            const response = await fetch(url, {
                method: "PUT"
            });

            const adat =
                await valaszFeldolgozasa(response);

            const vegrehajtva =
                irany === "long"
                    ? adat.longOrderFillTransaction
                    : adat.shortOrderFillTransaction;

            if (!vegrehajtva) {
                throw new Error(
                    "Az OANDA nem igazolta vissza "
                    + "a pozíció lezárását."
                );
            }

            sikeresZaras = true;

        } catch (error) {
            zarasiHiba = error.message;
        }

        const frissitesSikeres =
            await zarasUtaniFrissites();

        if (sikeresZaras) {

            sikerMegjelenitese(
                "A(z) " + devizapar + " "
                + iranySzoveg
                + " pozíció lezárását az OANDA visszaigazolta."
                + (frissitesSikeres
                    ? ""
                    : " A lista frissítése nem sikerült.")
            );

            tradeIdInput.value = "";

        } else {

            hibaMegjelenitese(
                zarasiHiba
                + " Ellenőrizd a számla állapotát "
                + "újrapróbálkozás előtt."
            );
        }
    }

    // Egyetlen ügylet lezárása Trade ID alapján
    tradeCloseForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            if (muveletFolyamatban) {
                return;
            }

            if (!tradeCloseForm.reportValidity()) {
                return;
            }

            const tradeIdSzoveg =
                tradeIdInput.value.trim();

            const tradeId = Number(tradeIdSzoveg);

            if (!/^[1-9]\d*$/.test(tradeIdSzoveg)
                || !Number.isSafeInteger(tradeId)) {

                hibaMegjelenitese(
                    "A Trade ID pozitív egész szám legyen."
                );

                return;
            }

            if (!nyitottTradeIdk.has(tradeIdSzoveg)) {

                hibaMegjelenitese(
                    "A megadott Trade ID nem szerepel "
                    + "a betöltött nyitott ügyletek között. "
                    + "Kattints a Frissítés gombra."
                );

                return;
            }

            const megerosites = window.confirm(
                "Biztosan lezárod a(z) "
                + tradeIdSzoveg
                + " azonosítójú ügyletet "
                + "az OANDA PRACTICE számlán?\n\n"
                + "Ez csak az adott ügyletet érinti."
            );

            if (!megerosites) {
                return;
            }

            gombokAllapota(true);
            uzenetekTorlese();

            closeResult.textContent =
                "Ügyletzárás folyamatban...";

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

                const adat =
                    await valaszFeldolgozasa(response);

                if (!adat.orderFillTransaction) {
                    throw new Error(
                        "Az OANDA nem igazolta vissza "
                        + "az ügylet lezárását."
                    );
                }

                sikeresZaras = true;

            } catch (error) {
                zarasiHiba = error.message;
            }

            const frissitesSikeres =
                await zarasUtaniFrissites();

            if (sikeresZaras) {

                sikerMegjelenitese(
                    "A(z) " + tradeIdSzoveg
                    + " azonosítójú ügyletet "
                    + "az OANDA sikeresen lezárta."
                    + (frissitesSikeres
                        ? ""
                        : " A lista frissítése nem sikerült.")
                );

                tradeIdInput.value = "";

            } else {

                hibaMegjelenitese(
                    zarasiHiba
                    + " Ellenőrizd a számla állapotát "
                    + "újrapróbálkozás előtt."
                );
            }
        }
    );

    // Frissítés gomb
    frissitesButton.addEventListener("click", function () {
        tradekBetoltese();
    });

    // Automatikus betöltés
    tradekBetoltese();
});
