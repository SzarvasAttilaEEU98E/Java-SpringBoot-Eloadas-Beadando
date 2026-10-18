
/*
 * OANDA Forex számlaadatok megjelenítése
 * REST API lekérdezés JavaScript fetch() segítségével
 */

// Az oldal betöltése után lekérjük a számlaadatokat
document.addEventListener("DOMContentLoaded", async function () {

    try {

        // HTTP GET-kérés a Spring Boot REST API felé
        const response = await fetch("/forex/account/summary");

        // HTTP-válasz ellenőrzése
        if (!response.ok) {
            throw new Error(
                "Az OANDA számlaadatok lekérdezése sikertelen."
            );
        }

        // JSON-válasz átalakítása JavaScript objektummá
        const account = await response.json();

        // Számlaadatok megjelenítése a HTML-táblázatban
        document.getElementById("accountId").textContent =
            account.id;

        document.getElementById("currency").textContent =
            account.currency;

        document.getElementById("balance").textContent =
            account.balance + " " + account.currency;

        document.getElementById("nav").textContent =
            account.nav + " " + account.currency;

        document.getElementById("marginAvailable").textContent =
            account.marginAvailable + " " + account.currency;

        document.getElementById("openTradeCount").textContent =
            account.openTradeCount;

        document.getElementById("openPositionCount").textContent =
            account.openPositionCount;

    } catch (error) {

        // Hiba kiírása a böngésző konzoljára
        console.error("OANDA számlaadatok hiba:", error);

        // Hiba megjelenítése a felhasználónak
        alert(error.message);
    }
});
