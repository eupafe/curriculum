<template>
  <!-- Container for available games component -->
  <main class="available-games">
    <h2 id="availableGamesTitle">Available Arenas</h2>
    
    <table>
      <!-- Table header -->
      <thead>
        <tr>
          <th class="column">Game ID</th>
          <th class="column">Size</th>
          <th class="column">HP-max</th>
        </tr>
      </thead>
      <!-- Table body with rows for each available game -->
      <tbody>
        <!-- Loop through availableGames array and create a row for each game -->
        <tr
          v-for="game in availableGames"
          :key="game.id"
          @click="toggleActionColumn(game.id)"
          :class="{ 'selected-row': selectedGame === game.id }"
        >
          <!-- Display game information in columns -->
          <td :style="{ color: selectedGame === game.id ? 'blue' : 'black' }">{{ game.id }}</td>
          <td>{{ game.size }}</td>
          <td>{{ game.hpMax }}</td>

          <!-- Display action column with Enter button for the selected game -->
          <td v-if="selectedGame === game.id" class="action-column">
            <router-link :to="'/arena/' + game.id + '/' + game.size+ '/' + game.hpMax + '/player1'" class="game-link" @keyup.enter="enterGame">
              Enter
            </router-link>
          </td>
        </tr>
      </tbody>
    </table>
    <div id="error-message"></div>
  </main>
</template>

<script>
export default {
  data() {
    // Data properties for available games and the selected game
    return {
      availableGames: [],
      selectedGame: null, // Track the selected game
    };
  },
  async mounted() {
    try {
      const token = localStorage.getItem('token');

      // Fetch player data
      const playerResponse = await fetch(`https://balandrau.salle.url.edu/i3/arenas`, {
        method: 'GET',
        headers: {
          'accept': 'application/json',
          'Bearer': `${token}`,
        },
      });

      if (playerResponse.ok) {
        const playerData = await playerResponse.json();
        const games = playerData.filter(game => !game.finished );

        const transformedData = games.map(game => ({
          id: game.game_ID,
          size: game.size,
          hpMax: game.HP_max,
          
        }));

        // Actualizar availableGames con los datos transformados
        this.availableGames = transformedData;
      } else {
        const errorMessageDiv = document.getElementById('error-message');
        errorMessageDiv.textContent = 'Error fetching data';
        errorMessageDiv.style.color = 'red'; 
      }

    } catch (error) {
      const errorMessageDiv = document.getElementById('error-message');
      errorMessageDiv.textContent = 'Error fetching data';
      errorMessageDiv.style.color = 'red'; 
    }
  },
  methods: {
    // Method to toggle the visibility of the action column for a specific game
    toggleActionColumn(gameId) {
      // Set the selectedGame to the clicked row
      this.selectedGame = gameId;
    },
    async enterGame(){
      
      try {
        
        const token = localStorage.getItem('token');
        const gameId = this.selectedGame; // Get the selected game ID from the selectedGame property
       
        const response = await fetch(`https://balandrau.salle.url.edu/i3/arenas/${gameId}/play`, {
          method: 'POST',
          headers: {
            'Bearer': `${token}`,
          },
        });


        if (response.ok) {
          const errorMessageDiv = document.getElementById('error-message');
          errorMessageDiv.textContent = '';
         
          this.$router.push({ name: 'arena', params: { gameId: this.selectedGame, rows: this.matrixSize, hp: this.hp, currentPlayer: 'player1' } });

        } else { 
          const errorData = await response.json(); 
              
          if (errorData.error && errorData.error.message) {
            const errorMessage = errorData.error.message;
            alert(errorMessage);
          }  
        }
      } catch (error) {
        alert('Error with the server');
      }
    }
  },
};
</script>

<style scoped>
/* Styles for the available games component */

.available-games {
  text-align: center;
  color: black;
  padding: 20px;
  padding-bottom: 50px; 
  background-color: #ffffff; 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
}

.scrollable-table {
  overflow-x: auto; 
}

/* Styles for the title of the available games section */
#availableGamesTitle {
  color: #3D5CFF;
}

/* Styles for the table */
table {
  margin: auto; 
  width: 100%;
  max-width: 1000px; 
  border-collapse: collapse;
  margin-top: 20px;
}

/* Styles for table headers and cells */
th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

/* Styles for table header background */
th {
  background-color: #f2f2f2;
}

/* Styles for columns in the table */
.column {
  color: black;
}

/* Styles for the selected row */
.selected-row td {
  color: blue;
}

/* Styles for the action column initially hidden */
.action-column {
  visibility: hidden;
}

/* Styles for the action column when the row is selected */
.selected-row .action-column {
  visibility: visible; 
}

/* Styles for the Enter button link */
.game-link {
  text-decoration: none;
  color: #007BFF;
  font-size: 18px;
  display: inline-block;
  padding: 10px 20px;
  border: 2px solid #007BFF;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.3s;
}

/* Styles for the Enter button link on hover */
.game-link:hover {
  background-color: #007BFF;
  color: #fff;
}

/* Responsive Styles for Mobile */
@media only screen and (max-width: 600px) {
  .available-games {
    padding: 10px;
    max-width: 100%;
  }

  table {
    font-size: 14px; 
  }

  th, td {
    padding: 6px; 
  }

  .game-link {
    padding: 8px 16px; 
    font-size: 16px; 
  }
}
</style>
