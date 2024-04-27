<template>
  <!-- The main container for displaying player game history -->
  <section class="game-history">
    <h2 id="playersGameTitle">Players Game History</h2>
    <!-- Table for displaying player information -->
    <table class="table1">
      <thead>
        <tr>
          <!-- Table header columns -->
          <th class="column">Player Name</th>
          <th class="column">Games Played</th>
          <th class="column">Games Won (%)</th>
          <th class="column"></th>
        </tr>
      </thead>
      <tbody>
        <!-- Loop through players and display information in table rows -->
        <tr v-for="player in players" :key="player.id">
          <td>{{ player.player_ID }}</td>
          <td>{{ player.gamesPlayed }}</td>
          <td>{{ player.gamesWon }}</td>
          <td>
            <!-- Button to show games played by a specific player -->
            <button @click="showPlayerGames(player.player_ID)">Show Games</button>
          </td>
        </tr>
      </tbody>
    </table>
    <!-- Display selected player's games if available -->
    <div v-if="selectedPlayerGames" class="moreinfo">
      <h3 id="playedBy">Games Played by {{ selectedPlayerName }}</h3>
      <!-- List to display games played by the selected player -->
      <ul class="player-games">
        <li v-for="game in selectedPlayerGames" :key="game.id">
          {{ game.game_ID }} - {{game.size}} (size) - hpMax {{game.HP_max}}
        </li>
      </ul>
    </div>
  </section>
</template>

<script>
export default {
  data() {
    // Data properties for player information, selected player's games, and selected player's name
    return {
      players: null,
      selectedPlayerGames: null,
      selectedPlayerName: '',
    };
  },
  mounted() {
    this.fetchListOfPlayers(); // Fetch player data when the component is mounted
  },
  methods: {
    // Method to show the games played by a specific player
    async fetchListOfPlayers() {
      try {
        const token = localStorage.getItem('token');

          // Fetch player data
          const playerResponse = await fetch(`https://balandrau.salle.url.edu/i3/players`, {
            method: 'GET',
            headers: {
              'Bearer': `${token}`,
            },
          });

          if (playerResponse.ok) {
            const playerData = await playerResponse.json();
            this.players = playerData;
            for (let i = 0; i < this.players.length; i++) {
              await this.fetchStatistics(i);
            }
            

          } else {
            throw new Error('Failed to fetch player data');
          }

      } catch (error) {
      }
    },
    async fetchStatistics(index){ // Fetch statistics for a specific player
        try {
          const playerId = this.players[index].player_ID; // Get player ID from the player object
          
          const token = localStorage.getItem('token');
          const response = await fetch(`https://balandrau.salle.url.edu/i3/players/${playerId}/statistics`, {
            method: 'GET',
            headers: {
              'Bearer': `${token}`,
            },
          });

          if (response.ok) {
            const statistics = await response.json();
            this.players[index] = {
              ...this.players[index],
              gamesPlayed: statistics.games_played, // Update player object with statistics
              gamesWon: statistics.games_won, 
            };
           
          } else {
            throw new Error('Failed to fetch player data');
          }
        } catch (error) {
        }
      },
    async showPlayerGames(playerId) { // Method to show the games played by a specific player
      
      try { 
          const token = localStorage.getItem('token'); // Get token from local storage
          const response = await fetch(`https://balandrau.salle.url.edu/i3/players/${playerId}/games/finished`, {
            method: 'GET',
            headers: {
              'Bearer': `${token}`,
            },
          });

          if (response.ok) {
            const games = await response.json();
            this.selectedPlayerGames = games;
            this.selectedPlayerName = playerId;
           
          } else {
            throw new Error('Failed to fetch player data');
          }
        } catch (error) {
        }
     
    },
  },
};
</script>

<style scoped>
/* Scoped styles for the player game history component */
#playersGameTitle {
  color: #3D5CFF;
  margin-bottom: 50px;
}

.game-history { /* Styles for the game history component */
  padding: 20px;
  background-color: white;
  width: auto;
  margin: 0 auto;
  display: flex; /* Flex display to help center the table */
  flex-direction: column; /* Column direction for inner items */
  align-items: center; 
}

.table1 { /* Styles for the table */
  width: 100%;
  max-width: 700px;
}

table {   /* Styles for the table */
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

th, td { /* Styles for the table header and table data */
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th { /* Styles for the table header */
  background-color: #354a5e;
  color: #fff;
}

td { /* Styles for the table data */
  color: black;
}

.column { /* Styles for the table columns */
  color: #3D5CFF;
  background: #ffffff;
}

#playedBy { /* Styles for the title */
  color: black;
}

/* Button styles */
button {
  background-color: #3D5CFF;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #2e3e4f;
}

/* Styles for the list of player games */
.player-games {
  list-style: none;
  padding: 0;
  margin-top: 10px;
}

.player-games li { /* Styles for the list items */
  margin-bottom: 5px;
  color: black;
}

.moreinfo { /* Styles for the div containing the list of player games */
  margin-top: 60px;
}

@media only screen and (max-width: 600px) { /* Responsive adjustments */
  .game-history {
    padding: 10px;
  }

  table, .player-games-section {
    max-width: 100%;
    overflow-x: auto;
  }
}

</style>
