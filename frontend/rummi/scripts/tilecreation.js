const numberInput = document.getElementById('numberDropdown');
  const colorInput = document.getElementById('colorDropdown');
  const outputCell = document.getElementById('outputCell');

  function checkSelections() {
    const number = numberInput.value.trim();
    const color = colorInput.value.trim();
    if (number && color) {
      handleSelections(number, color);
      numberDropdown.selectedIndex = 0;
      colorDropdown.selectedIndex = 0;
    }
  }

  function handleSelections(number, color) {
    let c;
    let cValue;
    switch (color) {
      case "RED": c = "r"; cValue = 0; break;
      case "GREEN": c = "g"; cValue = 1; break;
      case "BLUE": c = "b"; cValue = 2; break;
      case "BLACK": c = "k"; cValue = 3; break;
    }
    let dupNum = 1;
    document.querySelectorAll('.tile-img').forEach(tile => {
      if (tile.id.startsWith(number + "-" + cValue)) {
        dupNum += 1;
      }
    });

    const imgPath = "images/tiles/" + number + "-" + c + ".png";
    const newTile = document.createElement('img');
    newTile.src = imgPath;
    newTile.classList.add('tile-img');  
    newTile.id = number + "-" + cValue + "-" + dupNum;
    newTile.draggable = true;

    const outputCell = document.querySelector('#outputCell');
    outputCell.appendChild(newTile);

    setupDragAndDrop();
  }

  numberInput.addEventListener('change', checkSelections);
  colorInput.addEventListener('change', checkSelections);