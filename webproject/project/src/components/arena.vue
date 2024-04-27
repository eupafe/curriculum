<template>
  
  <section id="block2">
    <article class="block1">
        <h2>{{gameId}}</h2>
        <table id="gameTable">
            <tr v-for="row in rows" :key="row">
                <!-- Crear celdas en cada fila según la longitud de la tabla -->
                <td
                  v-for="col in rows"
                  :key="col"
                  :class="{
                    'cell': true,
                    'player1-cursor': row === currentRow && col === currentColumn,
                    'player2-cursor': row === player2Row && col === player2Column,
                  }"
                ></td>
            </tr>
        </table>
        <!-- Leave Game Button -->
        <footer class="block4">
            <button @click="leaveGame" class="leave-game-btn">Leave Game</button>
        </footer>
    </article>

    <!-- Attack Options -->
    <aside class="block3">
      <h3>Attack with:</h3>
      <button v-for="button in attackButtons" :key="button.text" @click="attack(button)">
        {{ button.text }} - {{ button.position }}
      </button>
    </aside>

    <nav class="block5">  
        <img src="../assets/arrow.png" alt="arrow" class="arrow" @click="up()"/>
        <img src="../assets/arrow.png" alt="arrow" class="arrow" style="transform: rotate(180deg);" @click="down()"/> 
        <img src="../assets/arrow.png" alt="arrow" class="arrow" style="transform: rotate(90deg);" @click="right()"/>
        <img src="../assets/arrow.png" alt="arrow" class="arrow" style="transform: rotate(-90deg);" @click="left()"/>
    </nav>
  </section>
 
  
</template>

<script>
export default {
   data() {
    return {
      gameId: '',
      rows: 0,
      hp: 0,
      currentPlayer: '',
      currentRow: 1,
      currentColumn: 1,
      attackButtons: [],
      direccionMirada: null,
      logsInterval: null,
      player2Row: null,
      player2Column: null,
      logs: [],
    };
  },
  methods: {
    up() { //mira cap a dalt
      
      this.rotateCursor('cursor-rotate-up'); 
      this.direccionMirada = 'up';
      this.paintCellsRed();
      this.fetchDirection('up');
  
    },
    down() { //mira cap a baix
    
      this.rotateCursor('cursor-rotate-down');
      this.direccionMirada = 'down';
      this.paintCellsRed();
      this.fetchDirection('down');
      
    },
    right() { //mira cap a la dreta
     
      this.rotateCursor('cursor-rotate-right');
      this.direccionMirada = 'right';
      this.paintCellsRed();
      this.fetchDirection('right');

    },
    left() { //mira cap a l'esquerra 
    
      this.rotateCursor('cursor-rotate-left');
      this.direccionMirada = 'left';
      this.paintCellsRed();
      this.fetchDirection('left');
      
    },
    rotateCursor(rotationClass) { //rotar el cursor
      const cursorCell = document.querySelector(`#gameTable tr:nth-child(${this.currentRow}) td:nth-child(${this.currentColumn})`);
      if (cursorCell) { 
        cursorCell.classList.remove('cursor-rotate-up', 'cursor-rotate-down', 'cursor-rotate-right', 'cursor-rotate-left');
        cursorCell.classList.add(`${rotationClass}`); 
      }
      console.log(rotationClass);
    },
    rotateCursorSecondP(rotationClass) { //rotar el cursor
      const cursorCell = document.querySelector(`#gameTable tr:nth-child(${this.player2Row}) td:nth-child(${this.player2Column})`);
      if (cursorCell) { 
        cursorCell.classList.remove('cursor-rotate-up', 'cursor-rotate-down', 'cursor-rotate-right', 'cursor-rotate-left');
        cursorCell.classList.add(`${rotationClass}`);
      }
      console.log(rotationClass);
    },
    getCellsInCursorDirection() { //obtenir les cel·les en la direcció del cursor
      const cells = [];

      if (this.direccionMirada  === 'right'){ //si mira cap a la dreta

        for (let i = this.currentColumn + 1; i <= this.rows; i++) {
          const cell = document.querySelector(`#gameTable tr:nth-child(${this.currentRow}) td:nth-child(${i})`);
          if (cell) {
            cells.push(cell);
          }
        }

      }
      else if (this.direccionMirada  === 'left'){ //si mira cap a l'esquerra
      
        for (let i = 1; i < this.currentColumn; i++) {
          const cell = document.querySelector(`#gameTable tr:nth-child(${this.currentRow}) td:nth-child(${i})`);
          if (cell) { 
            cells.push(cell); 
          }
        }
      }
     
      else if (this.direccionMirada  === 'down'){ //si mira cap avall
        for (let i = this.currentRow + 1; i <= this.rows; i++) {
          const cell = document.querySelector(`#gameTable tr:nth-child(${i}) td:nth-child(${this.currentColumn})`);
          if (cell) {
            cells.push(cell);
          }
        }

      }
      
      else if (this.direccionMirada  === 'up'){ //si mira cap a dalt
        for (let i = 1; i < this.currentRow; i++) {
          const cell = document.querySelector(`#gameTable tr:nth-child(${i}) td:nth-child(${this.currentColumn})`);
          if (cell) {
            cells.push(cell);
          }
        }

      }

      return cells;
    },

    getCellsInCursorDirectionSP() { //obtenir les cel·les en la direcció del cursor
      const cells = [];
     
        for (let i = 1; i < this.player2Row; i++) {
          const cell = document.querySelector(`#gameTable tr:nth-child(${i}) td:nth-child(${this.player2Column})`);
          if (cell) {
            cells.push(cell);
          }
        }
      return cells;
    },
    
    
    paintCellsRed() {
      // Limpiar celdas resaltadas anteriores
      this.clearRedCells();

      // Obtener las celdas en la dirección del cursor
      const cells = this.getCellsInCursorDirection();

      cells.forEach((cell) => {
        if (!cell.classList.contains('player1-cursor')) {
          cell.classList.add('red-cell');
        }
      });
    },

    paintCellsBlue() {
      // Limpiar celdas resaltadas anteriores
      this.clearBlueCells();
      console.log("pinta blau");

      // Obtener las celdas en la dirección del cursor
      const cells = this.getCellsInCursorDirectionSP();
      //console.log(cells);
      cells.forEach((cell) => {
        if (!cell.classList.contains('player2-cursor')) {
          cell.classList.add('blue-cell');
        }
      });
    },

    clearRedCells() {
      // Limpiar el color rojo de las celdas resaltadas anteriores
      const redCells = document.querySelectorAll('#gameTable .red-cell');
      redCells.forEach((cell) => {
        cell.classList.remove('red-cell');
      });
    },
    clearBlueCells() {
      // Limpiar el color rojo de las celdas resaltadas anteriores
      const blueCells = document.querySelectorAll('#gameTable .blue-cell');
      blueCells.forEach((cell) => {
        cell.classList.remove('blue-cell');
      });
    },

    async fetchDirection(directionEntered){
      try {
        const token = localStorage.getItem('token');

        const response = await fetch(`https://balandrau.salle.url.edu/i3/arenas/direction`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Bearer': `${token}`,
          },
          body: JSON.stringify({
            direction: directionEntered,
          }),
        });

        if (!response.ok){
          const errorData = await response.json(); 
              
          if (errorData.error && errorData.error.message) {
            const errorMessage = errorData.error.message;
            alert(errorMessage);
          }  
        }
      } catch (error) {
        alert('Error with the server');
      }

    },

    leaveGame() {
      this.$router.push('/available-games'); // Redirect to the available games page
    },
    attack(button){
      this.getLogs();
      this.clearRedCells();
      console.log(button.position);
      const [x, y] = button.position.slice(1, -1).split(',').map(Number);
      // Coordenadas a las que deseas llegar
      const coordenadaObjetivoX = x;
      const coordenadaObjetivoY = y;
      const coordenadaInicialX = this.currentColumn;
      const coordenadaInicialY = this.currentRow;


      // Calcular la diferencia en coordenadas
      const diferenciaX = coordenadaObjetivoX - coordenadaInicialX;
      const diferenciaY = coordenadaObjetivoY - coordenadaInicialY;

      // Ajustar las coordenadas según la dirección de la mirada
      let nuevaCoordenadaX, nuevaCoordenadaY;

      switch (this.direccionMirada) { //mirar cap a la dreta
        case 'up':
          nuevaCoordenadaX = diferenciaX;
          nuevaCoordenadaY = diferenciaY;
          break;
        case 'down':
          console.log("en aquest cas");
          nuevaCoordenadaX = this.currentColumn + x;
          nuevaCoordenadaY = this.currentRow + y;
          break;
        case 'left':
          nuevaCoordenadaX = this.currentColumn + x;
          nuevaCoordenadaY = this.currentRow + y;
          break;
        case 'right':
          nuevaCoordenadaX = this.currentRow ;
          nuevaCoordenadaY = this.currentColumn + y -1 ;
          break;
        default:
          console.error('Dirección no válida');
      }

      // Sumar las coordenadas ajustadas a las coordenadas iniciales
      this.currentColumn = coordenadaInicialX + nuevaCoordenadaX;
      this.currentRow = coordenadaInicialY + nuevaCoordenadaY;
      this.rotateCursor(`cursor-rotate-${this.direccionMirada}`);
     
    },
    async getLogs(){ //obtenir els logs
      try {
        console.log("hey");
        // Fetch attacks information
        const token = localStorage.getItem('token');
        const attacksResponse = await fetch(`https://balandrau.salle.url.edu/i3/arenas/${this.gameId}/logs`, {
          method: 'GET', 
          headers: {
            'accept': 'application/json',
            'Bearer': `${token}`,
          },
        });

        if (attacksResponse.ok) {
          this.logs = await attacksResponse.json();
          
          
        } else {
          throw new Error('Failed to fetch attacks data');
        }
      } catch (error) {
        
      }
    },
    async fetchAttacks() {
      try {
        // Fetch attacks information
        const token = localStorage.getItem('token');
        const attacksResponse = await fetch(`https://balandrau.salle.url.edu/i3/players/attacks`, {
          method: 'GET',
          headers: {
            'accept': 'application/json',
            'Bearer': `${token}`,
          },
        });

        if (attacksResponse.ok) {
          const attacksData = await attacksResponse.json();
          
          // Assign attack names to buttons
          this.attackButtons = (attacksData.filter(attack => attack.equipped)).map(attack => ({
            text: attack.attack_ID,
            position: attack.positions,
          }));
        } else {
          throw new Error('Failed to fetch attacks data');
        }
      } catch (error) {
        // Handle the error, show a message to the user, etc.
      }
    },
    async checkForNewLogs() {
      
      await this.getLogs();

      if (this.currentLogIndex == 1){ //si és el primer log
        const positionPattern = /\((\d+),\s*(\d+)\)/;
        const directionPattern = /looking to the (right|left|up|down)/;
       
        const positionMatch = this.logs[1].description.match(positionPattern);
        const directionMatch = this.logs[1].description.match(directionPattern);

        if (positionMatch && directionMatch) {
          // Extraer las coordenadas x e y de la posición
          const x = parseInt(positionMatch[1], 10);
          const y = parseInt(positionMatch[2], 10);

          const direction = directionMatch[1];

          
          this.rotateCursor(`cursor-rotate-${direction}`);
          this.currentRow = x+1;
          this.currentColumn = y+1;
          this.direccionMirada = 'up';
          this.paintCellsRed();
        }
      }
      else if (this.currentLogIndex > 2){
        // Definir una expresión regular para buscar el movimiento y la posición
        const moveRegex = /'(\w+)' moves (\w+) \((\d+),(\d+)\) -> \((\d+),(\d+)\)/;
        // Buscar coincidencias en el mensaje
        const moveMatches = this.logs[this.currentLogIndex].description.match(moveRegex);

        if (moveMatches) {

          const finalX = parseInt(moveMatches[5], 10) + 1;
          const finalY = parseInt(moveMatches[6], 10) + 1;

          this.player2Row = finalX ;
          this.player2Column = finalY ;
          this.paintCellsBlue();
        }
      }

      // Incrementar el índice para avanzar al siguiente log (movimiento)
      this.currentLogIndex++;

      // Verificar si hay más logs disponibles
      if (this.currentLogIndex < this.logs.length) {
        // Obtener el siguiente log
        const nextLog = this.logs[this.currentLogIndex];

        // Mostrar una alerta con la descripción del siguiente log (esto es solo un ejemplo, puedes ajustarlo según tus necesidades)
        //alert(`Siguiente movimiento: ${nextLog.description}`);
      }
    },
    setupLogChecker() {
      
      setInterval(this.checkForNewLogs, 2000);
    },
  },
  async mounted() {
    this.gameId = this.$route.params.gameId;
    this.rows = parseInt(this.$route.params.rows);
    this.hp = this.$route.params.hp;
    this.currentPlayer = this.$route.params.currentPlayer;
    await this.fetchAttacks();
    this.rotateCursor('cursor-rotate-up');
    this.currentLogIndex = 0;
    this.setupLogChecker();
    //await this.getLogs();


  },
};
</script>


<style>
#title { /* Title of the page */
  color: black;
}

h2 { /* Main heading */
  color: #3D5CFF;
}

h3{ /* Subheading */
    color: black;
}

#block2{ /* Main container of the component */
    display: flex;
    flex-direction: row;
    max-width: 900px;
}

.block1, .block3, .block5 { /* Blocks for displaying game information, history and arrows */
    display: flex;
    flex-direction: column;
    width: 300px;
    height: 400px;
    margin-left: 20px;

}

.block4{ /* Block for displaying the leave game button */
    margin-top: 20px;
}

#gameTable { /* Table for displaying the game */
  border-collapse: collapse;
  width: 100%;
  height: 300px;
}

#gameTable td { /* Table cells */
  border: 1px solid black;
  width: 30px;
  height: 30px;
}

.cell { /* Table cells */
  background-color: white;
}

#gameTable .player1-cursor { /* Cursor for player 1 */
    background-image: url('../assets/cursor.png');
    background-size: contain;
    background-repeat: no-repeat;
    background-position: center;
    background-color: white;
    border: none;
    transition: transform 0.3s ease; 
  }

  #gameTable .player2-cursor {  /* Cursor for player 2 */
    background-image: url('../assets/secondP.png');
    background-size: contain;
    background-repeat: no-repeat;
    background-position: center;
    background-color: white;
    border: none;
    transition: transform 0.3s ease; 
  }
  #gameTable .cursor-rotate-up { transform: rotate(0deg); } /* Rotations for the cursor */
#gameTable .cursor-rotate-down { transform: rotate(180deg); } 
#gameTable .cursor-rotate-right { transform: rotate(90deg); }
#gameTable .cursor-rotate-left { transform: rotate(-90deg); }

  #gameTable .red-cell { 
  background-color: red; 
}

#gameTable .blue-cell {  /* Color for the cells */
  background-color: #6CE5E8; 
}

.arrow { /* Arrows for moving the cursor */
  width: 60px;
  height: 90px;
}

button { /* Button styles */
  margin: 10px;
  padding: 8px;
  background-color: #3d5cff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.leave-game-btn{ /* Leave game button */
    width: 100px;
    height: 35px;
    border: 2px solid #3D5CFF; 
    border-radius: 4px;
    background-color: white;
    color: black;
}

/* Responsive Styles for Mobile */
@media only screen and (max-width: 600px) {
    #block2 {
        flex-direction: column;
        align-items: center;
    }

    .block1, .block3, .block5 {
        width: 90%; 
        margin-left: 0;
        margin-bottom: 20px;
    }

    #gameTable {
        width: 90%; 
        height: auto; 
    }

    #gameTable td {
        width: auto; 
        height: auto;
    }

    .arrow {
        width: 40px; 
        height: 60px; 
    }
}

</style>