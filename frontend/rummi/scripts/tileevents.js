// ====== DRAG & DROP ======
  function setupDragAndDrop() {
    let draggedImage = null;
    let dragPreview = null;

    // Tile drag start
    document.querySelectorAll('.tile-img').forEach(img => {
      img.addEventListener('dragstart', e => {
        draggedImage = e.target;

        //draggedImage.style.visibility = 'hidden'; // hide from original spot
        setTimeout(() => draggedImage.style.visibility = 'hidden', 0);

        dragPreview = draggedImage.cloneNode(true);
        dragPreview.classList.add('drag-preview');
        document.body.appendChild(dragPreview);

        const emptyCanvas = document.createElement('canvas');
        e.dataTransfer.setDragImage(emptyCanvas, 0, 0);
      });

      img.addEventListener('dragend', () => {
        if (draggedImage) draggedImage.style.visibility = 'visible';
        dragPreview?.remove();
        dragPreview = null;
        draggedImage = null;

        checkAndExpandGrid(); // check if we should add cells
      });
    });

    // move preview with mouse
    document.addEventListener('dragover', e => {
      if (dragPreview) {
        dragPreview.style.left = e.pageX + 'px';
        dragPreview.style.top = e.pageY + 'px';
      }
    });

    // handle grid cell drop targets
    document.querySelectorAll('.grid-cell, #outputCell').forEach(cell => {
      cell.addEventListener('dragover', e => {
        e.preventDefault();
        cell.classList.add('drag-over');
      });

      cell.addEventListener('dragleave', () => cell.classList.remove('drag-over'));

      cell.addEventListener('drop', e => {
        e.preventDefault();
        cell.classList.remove('drag-over');
        const hasImage = cell.querySelector('img');

        if (cell.id === 'outputCell') {
          draggedImage.parentNode.removeChild(draggedImage);
          return;
        }

        const tileRack = document.querySelector('#tile-rack');

        if (draggedImage && !hasImage) {
          if (draggedImage.parentNode === tileRack || draggedImage.parentNode.classList.contains('grid-cell') || draggedImage.parentNode.id === 'outputCell') {
            draggedImage.parentNode.removeChild(draggedImage);
            cell.appendChild(draggedImage);
          }
        }
      });
    });

  }