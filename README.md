# Rummikub Solver

A Java Spring Boot backend + HTML/CSS/JavaScript frontend that simulates parts of the Rummikub game.
Uses a backtracking algorithm to determine whether/how a given tile can be played, based on the player's rack and the current sets on the table.

---

## Features

- Domain-driven backend design (Tile, TileSet, TilePile) for clean extension and full game logic.
- REST API (Spring Boot)
   - POST /api/validatesetsinorder — validates submitted sets.
   - POST /api/tilesolver — returns a new valid table configuration after solving for a specific tile (if possible).
- Interactive frontend with Drag-and-drop tile placement
- Animations to visualize the solver’s result
- Images currently configured for a Rummikub variant with 13 Jewish months (easily replaceable for standard tiles)

---

## Tech Stack
- **Backend:** Java 21, Spring Boot, Maven  
- **Frontend:** HTML, CSS, JavaScript (Fetch API)

---

## Run Locally

**Backend**
```bash
cd backend
mvn spring-boot:run
```
(Default port: 8080)
If port binding issues occur, remove:
```bash
server.port=${PORT}
```
from `backend/src/main/resources/application.properties`

**Frontend**
Open index.html in any modern browser.
Replace:
```js
http://the-backend-endpoint/
```
with:
```js
http://localhost:8080/
```
Locations to update:
- `index.html` — line 272
- `scripts/verifysets.js` — line 47

---

## Demo

[Watch the demo (MP4)](frontend/rummi/images/demo.mp4)
