document.getElementById("setverifybtn").addEventListener("click", () => validateSetsAndDisplayResults());

  function getPlacematSets(){
    const tiles = Array.from(document.querySelectorAll('.placemat-cell'));

    const sets = []
    let set = []

    for (let i = 0; i < tiles.length; i++){
      const img = tiles[i].querySelector('img');

      if (i % columns === 0 || !(img)){ //reached a new row or reached an empty cell. columns variable is created in placematgridsetup.js
        if (set.length > 0){
          set.reverse();
          sets.push(set);
        }
        set = []
      }
      if (img) {
        let id = img.id;
        const attributes = id.split("-");
        set.push({ "number": parseInt(attributes[0]), "color": parseInt(attributes[1]), "dupNum": parseInt(attributes[2]), "joker": false }); // *joker
      }
    }

    if (set.length > 0){// if there's a tile in the last cell
      set.reverse();
      sets.push(set);
    }

    return sets
  }

  async function validateSetsAndDisplayResults() {
    const badtiles = await validateSets();
    displaySetValidationResults(badtiles, true);
  }


  async function validateSets() {
    document.getElementById("overlay").classList.add("verifytiles"); //don't allow the user to mess with the board until the response is returned
    const board = getPlacematSets();
    const requestObject = {"board": board};

    try {
      //replace with actual backend endpoint
      const response = await fetch("http://the-backend-endpoint/api/validatesetsinorder", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestObject)
      });

      const result = await response.json();
      const badtiles = [];
      result['invalidsets'].forEach(invalidset => {
        console.log(invalidset);
        invalidset.forEach(tile => {
          console.log(tile);
          badtiles.push(tile["number"] + "-" + tile["color"] + "-" + tile["dupNum"]);
        });
      });
      return badtiles;
      
    } 
    catch (error) {
        console.error("Error:", error);
    } 
    finally {
        document.getElementById("overlay").classList.remove("verifytiles");
    }
  }

  function displaySetValidationResults(badtiles, showGreen = true){

    document.querySelectorAll('.placemat-cell').forEach(c => {
      const img = c.querySelector('img');
      if (img){
        const id = img.id;
        let isBad = false;
        badtiles.forEach(tilePattern => {
          if (id.startsWith(tilePattern)){
            isBad = true;
          }
        });
        if (isBad) {flashCell(c, 'r')}
        else {if(showGreen){flashCell(c, 'g')}}
      }

    });
  }


  function flashCell(cell, highlightColor, duration = 10000) {
    cell.classList.remove('highlight' + highlightColor);
    void cell.offsetWidth; //don't know what this does
    cell.classList.add('highlight' + highlightColor);
    setTimeout(() => cell.classList.remove('highlight' + highlightColor), duration);
  }

  function flashRed() {
    document.querySelectorAll('.placemat-cell').forEach(e => {
      flashell(e, 'r')
    });    
  }

  function flashGreen() {
    document.querySelectorAll('.placemat-cell').forEach(e => {
      flashell(e, 'g')
    });    
  }