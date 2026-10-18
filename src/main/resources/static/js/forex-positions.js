
document.addEventListener("DOMContentLoaded", function () {

    const tabla = document.getElementById("poziciokTabla");
    const frissitesButton = document.getElementById("frissitesButton");
    const hibaUzenet = document.getElementById("hibaUzenet");
    const uresLista = document.getElementById("uresLista");

    // Pozíciók lekérdezése
    async function poziciokBetoltese() {

        tabla.replaceChildren();
        hibaUzenet.classList.add("d-none");
        uresLista.classList.add("d-none");

        frissitesButton.disabled = true;
        frissitesButton.textContent = "Betöltés...";

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

            // Ha nincs nyitott pozíció
            if (poziciok.length === 0) {
                uresLista.classList.remove("d-none");
                return;
            }

            // Pozíciók megjelenítése
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

                    // Eredmények színezése
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

                tabla.appendChild(sor);
            });

        } catch (error) {

            hibaUzenet.textContent = error.message;
            hibaUzenet.classList.remove("d-none");

        } finally {

            frissitesButton.disabled = false;
            frissitesButton.textContent = "Frissítés";
        }
    }

    // Frissítés gomb
    frissitesButton.addEventListener(
        "click",
        poziciokBetoltese
    );

    // Automatikus betöltés oldalnyitáskor
    poziciokBetoltese();
});
