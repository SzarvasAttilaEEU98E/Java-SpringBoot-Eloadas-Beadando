
document.addEventListener("DOMContentLoaded", async function () {

    const tabla = document.getElementById("arfolyamTabla");
    const hibaUzenet = document.getElementById("hibaUzenet");

    try {

        // Árfolyamok lekérdezése a Spring Boot REST API-ból
        const response = await fetch("/forex/pricing");

        if (!response.ok) {
            throw new Error("Az árfolyamok lekérdezése sikertelen.");
        }

        // JSON-válasz átalakítása JavaScript objektumlistává
        const arfolyamok = await response.json();

        // A betöltési felirat eltávolítása
        tabla.replaceChildren();

        // Ha nincs visszakapott árfolyam
        if (arfolyamok.length === 0) {
            const sor = tabla.insertRow();
            const cella = sor.insertCell();
            cella.colSpan = 6;
            cella.textContent = "Nem érhető el árfolyamadat.";
            return;
        }

        // Végigmegyünk a devizapárokon
        arfolyamok.forEach(function (arfolyam) {

            const sor = tabla.insertRow();

            const adatok = [
                arfolyam.instrument.replace("_", "/"),
                arfolyam.bid ?? "-",
                arfolyam.ask ?? "-",
                new Date(arfolyam.time).toLocaleString("hu-HU"),
                arfolyam.status,
                arfolyam.tradeable ? "Igen" : "Nem"
            ];

            adatok.forEach(function (adat) {
                const cella = sor.insertCell();
                cella.textContent = adat;
            });
        });

    } catch (error) {

        console.error("OANDA árfolyamhiba:", error);

        tabla.replaceChildren();

        hibaUzenet.textContent = error.message;
        hibaUzenet.classList.remove("d-none");
    }
});
