  // ====== BUILD TILE RACK ======
  function buildRack() {
    const tileRack = document.getElementById('tile-rack'); // rack container
    let rackColumns = 10;
    tileRack.innerHTML = ''; // clear old cells

    const containerHeight = tileRack.clientHeight;
    const containerWidth = tileRack.clientWidth;

    let cellWidth = containerWidth / rackColumns; // width of each cell
    //the variable ASPECT RATIO is declared in the placematgridsetup.js script file
    let cellHeight = cellWidth / aspectRatio; // adjust height using ratio
    
    cellHeight = Math.min(cellHeight, containerHeight * 1.8 / 2)
    cellWidth = Math.min(cellWidth, cellHeight * aspectRatio)

    // setup CSS grid
    tileRack.style.gridTemplateColumns = `repeat(${rackColumns}, 1fr)`;
    tileRack.style.gridTemplateRows = `repeat(${2}, 1fr)`;

    // total number of cells
    const totalCells = rackColumns * 2;
    for (let i = 0; i < totalCells; i++) {
      const cell = document.createElement('div');
      cell.style.width = cellWidth;
      cell.style.height = cellHeight;
      cell.classList.add('rack-cell');
      cell.classList.add('grid-cell')
      tileRack.appendChild(cell);
    }

  }