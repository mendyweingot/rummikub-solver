// ====== BUILD GRID ======

  const aspectRatio = 1 / 1.33; // tile width/height ratio 
  const placemat = document.getElementById('placemat'); // grid container
  const minColumns = 14; // minimum number of columns per row
  let columns = minColumns; // start value

function buildGrid() {
  placemat.innerHTML = '';

  const containerWidth = placemat.clientWidth;
  const containerHeight = placemat.clientHeight;

  const cellWidth = containerWidth / columns;
  const cellHeight = cellWidth / aspectRatio;

  // Determine number of rows that *fit* within placemat
  let rows = Math.floor(containerHeight / cellHeight);
  rows += (containerHeight - (cellHeight * rows + 5 * rows)) < 0? -1: 0;

  // Calculate total used height by the grid
  const totalGridHeight = rows * cellHeight;

  // Calculate leftover space and distribute it as gap
  const verticalGap = Math.max(((containerHeight - totalGridHeight) / (rows - 1)), 5)

  // Apply fixed pixel sizes (preserves aspect ratio)
  placemat.style.gridTemplateColumns = `repeat(${columns}, 1fr)`;
  placemat.style.gridTemplateRows = `repeat(${rows}, ${cellHeight}px)`;
  placemat.style.rowGap = `${verticalGap}px`;

  placemat.style.alignContent = 'space-evenly';

  const totalCells = columns * rows;
  for (let i = 0; i < totalCells; i++) {
    const cell = document.createElement('div');
    cell.classList.add('grid-cell', 'placemat-cell');
    placemat.appendChild(cell);
  }
}


  //old code
  function buildGid() {
    placemat.innerHTML = ''; // clear old cells

    const containerHeight = placemat.clientHeight;
    const containerWidth = placemat.clientWidth;

    const cellWidth = containerWidth / columns; // width of each cell
    const cellHeight = cellWidth / aspectRatio; // adjust height using ratio

    const rows = Math.floor(containerHeight / cellHeight); // number of rows that fit

    // setup CSS grid
    placemat.style.gridTemplateColumns = `repeat(${columns}, 1fr)`;
    placemat.style.gridTemplateRows = `repeat(${rows}, 1fr)`;

    // total number of cells
    const totalCells = columns * rows;
    for (let i = 0; i < totalCells; i++) {
      const cell = document.createElement('div');
      cell.classList.add('grid-cell');
      cell.classList.add('placemat-cell')
      placemat.appendChild(cell);
    }
  }

  // ====== GRID EXPANSION ======
  function checkAndExpandGrid() {
    const allRows = getGridRows();
    let shouldExpand = false;

    // Check each row
    allRows.forEach(row => {
      const filledCells = row.filter(cell => cell.querySelector('img')).length;
      const threshold = columns - Math.floor(columns / 3); // new rule
      if (filledCells >= threshold) {
        shouldExpand = true;
      }
    });

    // Expand if needed
    if (shouldExpand) {
      rebuildPreservingTiles(3);
    }
  }

  // ====== GET ROWS ======
  function getGridRows() {
    const cells = Array.from(document.querySelectorAll('.placemat-cell'));
    const rows = [];
    for (let i = 0; i < cells.length; i += columns) {
      rows.push(cells.slice(i, i + columns));
    }
    return rows;
  }

  // ====== REBUILD GRID WHILE KEEPING TILES ======
  function rebuildPreservingTiles(numberOfAddedColumns) {
    columns += numberOfAddedColumns;
    const oldColumns = columns - numberOfAddedColumns; // the number before we increased
    const oldCells = Array.from(document.querySelectorAll('.placemat-cell'));

    // store current tiles with their positions
    const existingTiles = [];
    oldCells.forEach((cell, index) => {
      const img = cell.querySelector('img');
      if (img) {
        existingTiles.push({ src: img.src, id: img.id, oldIndex: index });
      }
    });

    buildGrid(); // rebuild grid with new column count

    const newCells = Array.from(document.querySelectorAll('.placemat-cell'));

    // place tiles back in the same logical position
    existingTiles.forEach(({ src, id, oldIndex }) => {
      const oldRow = Math.floor(oldIndex / oldColumns);
      const oldCol = oldIndex % oldColumns;
      const newIndex = oldRow * columns + oldCol;

      if (newIndex < newCells.length) {
        const img = document.createElement('img');
        img.src = src;
        img.classList.add('tile-img');
        img.id = id;
        img.draggable = true;
        newCells[newIndex].appendChild(img);
      }
    });

    setupDragAndDrop(); // re-enable drag/drop on new elements
  }

