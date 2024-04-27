<template>
  <!-- Main container for the component -->
  <div class="list-players">
    <!-- Header block with title and search input -->
    <div class="block1">
      <!-- Heading for the player ranking section -->
      <h2  class="header2">Player's Ranking</h2>
      <!-- Input for searching players -->
      <input v-model="searchQuery" placeholder="Search players" class="search-input" />
    </div>

    <!-- Player list display -->
    <div>
      <!-- Router view to display player details (if applicable) -->
      <router-view></router-view>

      <!-- Table displaying player ranking -->
      <table class="player-table">
        <thead>
          <tr>
            <th>Ranking according to Experience</th>
            <th>Player Name</th>
          </tr>
        </thead>
        <tbody>
          <!-- Loop through filtered players and display in the table -->
          <tr v-for="player in filteredPlayers" :key="player.id" @click="showPlayerHistory(player.player_ID)">
            <td>{{ player.xp }}</td> <!-- Display player experience -->
            <td>{{ player.player_ID }}</td> <!-- Display player name -->
          </tr>
        </tbody>
        <tfoot>
          <!-- Display total number of filtered players -->
          <tr>
            <td colspan="2">Total Players: {{ filteredPlayers.length }}</td> <!-- Display total number of filtered players -->
          </tr>
        </tfoot> 
      </table> <!-- End of player ranking table -->
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // Data property for search input
      searchQuery: '',
      // Array of player data
      players: [],
    };
  },
  mounted() { // Fetch player data when the component is mounted
    this.fetchListOfPlayers();
  },
  computed: {
    // Computed property for filtering and sorting players based on search query
    filteredPlayers() {
      let filtered = this.players;
      // Apply search filter
      if (this.searchQuery) {
        filtered = filtered.filter(player =>
          player.player_ID.toLowerCase().includes(this.searchQuery.toLowerCase())
        );
      }
      // Sort players by experience in descending order
      return this.sortPlayersByExperience(filtered);
    },
  },
  methods: {
    async fetchListOfPlayers() {

      try {
        const token = localStorage.getItem('token'); // Get token from local storage

          // Fetch player data
          const playerResponse = await fetch(`https://balandrau.salle.url.edu/i3/players`, {
            method: 'GET',
            headers: {
              'Bearer': `${token}`,
            },
          });

          if (playerResponse.ok) { // Check if the response is ok
            const playerData = await playerResponse.json();
            this.players = playerData; // Update player data
            
          } else { // Throw an error if the response is not ok
            throw new Error('Failed to fetch player data');
          }

      } catch (error) {
      }
      },
    // Method to sort players by experience in descending order
    sortPlayersByExperience(players) {
      return players.sort((a, b) => b.xp - a.xp);
    },
    // Method to navigate to player history view
    showPlayerHistory(playerId) {
      
      this.$router.push({ name: 'PlayerHistory', params: { id: playerId } }); // Navigate to player history view
    },
  },
};
</script>

<style scoped>
/* Styling for the main container */
.list-players {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
}

/* Styling for the search input */
.search-input {
  width: 200px;
  height: 30px;
  padding: 10px;
  border: 1px solid #000000;
  border-radius: 5px;
  background: white;
  color: black;
}

.header2 {
  padding-right: 40px;
}

/* Styling for the player ranking table */
.player-table {
  width: 750px;
  border-collapse: collapse;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

/* Styling for table headers */
.player-table th {
  background-color: white;
  color: #3D5CFF;
  padding: 20px;
  text-align: center;
}

/* Styling for table data cells */
.player-table td {
  padding: 10px;
  color: black;
}

/* Styling for alternating row colors in the table */
.player-table tr:nth-child(even) {
  background-color: #f2f2f2;
}

/* Styling for hover effect on table rows */
.player-table tr:hover {
  background-color: #e0e0e0;
}

/* Styling for the header block containing the title and search input */
.block1 {
  display: flex;
  flex-direction: row;
  justify-content: center; 
  align-items: center; 
  width: 100%;
  height: auto;
  padding-top: 30px;
  padding-bottom: 30px;
}

.block1-container {
  display: flex;
  justify-content: center;
  width: 100%;
  padding-top: 10px; 
  padding-bottom: 10px; 
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .block1 {
    flex-direction: column; 
    padding-top: 20px;
    padding-bottom: 20px;
  }

  .header2 { /* Styling for the player ranking section heading */
    padding-right: 0; 
    margin-bottom: 10px;
  }

  .search-input { /* Styling for the search input */
    width: 100%; 
    max-width: 300px; 
    margin: 0 auto; 
  }

  .player-table { /* Styling for the player ranking table */
    width: 100%; 
  }

  .player-table th,
  .player-table td { /* Styling for table headers and data cells */
    padding: 10px; 
  }
}

</style>
