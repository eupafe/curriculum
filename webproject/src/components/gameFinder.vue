<template>
  <!-- The main container for the game finder component -->
  <section class="game-finder">
    <!-- Form for filters containing dropdown, date inputs, and search bar -->
    <form @submit.prevent="applyFilters" class="filter-form">
      <!-- Filter by Status -->
      <section class="filters">
        <div class="filter-group">
          <label for="filterStatus">Status:</label>
          <select v-model="filterStatus" id="filterStatus" class="selector">
            <option value="all">All</option>
            <option value="finished">Finished</option>
            <option value="available">Available</option>
          </select>
        </div>

      <!-- Between Dates -->
      <div class="date-group">
          <div class="date-filter">
            <label for="startDate">Start Date:</label>
            <input type="date" v-model="startDate" id="startDate" class="dates" />
          </div>
          <div class="date-filter">
            <label for="endDate">End Date:</label>
            <input type="date" v-model="endDate" id="endDate" class="dates" />
          </div>
        </div> 
      
      
        <!-- Search by Name --> 
        <div class="search-group">
          <label for="searchQuery">Search by Name:</label>
          <input v-model="searchQuery" type="text" id="searchQuery" class="search-input" placeholder="Search...">
        </div>
      </section>
      <!-- List of games displayed based on applied filters -->
      <ul v-if="hasFilteredGames" class="game-list">
        <li v-for="game in filteredGames" :key="game.id" class="game-item">
          <span class="game-name">{{ game.id }} - {{ game.size }} with hp {{game.hpMax}},
            Creation date: {{ game.startDate }} </span>
          <button v-if="game.status === 'finished'" @click="viewRecord(game)">View Record</button>
        </li>
      </ul>
      <p v-else  style="color: black;">No games to display</p>
      <div id="error-message"></div>
    </form>
  </section>
</template>

<script>
export default {
  data() {
    return {
      games: [],
      filterStatus: 'all',
      startDate: '',
      endDate: '',
      searchQuery: '',
      selectedGame: null,
    };
  },
  mounted() {
    // Fetch the list of games when the component is mounted
    this.fetchGames();
  },
  computed: {
    hasFilteredGames() { // Check if there are filtered games
      return this.filteredGames.length > 0;
    },
    filteredGames() { // Filter games based on applied filters
      if (this.filterStatus === 'all') {

        return this.games.filter((game) => {
          const nameMatch = game.id.toLowerCase().includes(this.searchQuery.toLowerCase()); 
          const dateMatch = this.matchCreationDate(game.startDate); 
          return nameMatch && dateMatch; 
        });

      } 
      else if (this.filterStatus === 'available') { 
        
        return this.games.filter((game) => {
          const statusMatch = game.start === false && game.finished === false;
          const nameMatch = game.id.toLowerCase().includes(this.searchQuery.toLowerCase());
          const dateMatch = this.matchCreationDate(game.startDate);
          return statusMatch && nameMatch && dateMatch;
        }); // Filter games that are available

      } else if (this.filterStatus === 'finished') {
        return this.games.filter((game) => {
          const statusMatch = game.finished === true;
          const nameMatch = game.id.toLowerCase().includes(this.searchQuery.toLowerCase());
          const dateMatch = this.matchCreationDate(game.startDate);
          return statusMatch && nameMatch && dateMatch;
        }); // Filter games that are finished
      }
    },
  },
  methods: {
      matchCreationDate(creationDate) { // Check if the game creation date matches the filter start date
        if (!this.startDate || !creationDate) {
          return true; // Si no hay fecha de inicio o fecha de creación, devuelve true
        }

        const gameDate = new Date(creationDate);
        const filterStartDate = new Date(this.startDate); // Convertir a Date
        
        return (
          gameDate.getDate() === filterStartDate.getDate() &&
          gameDate.getMonth() === filterStartDate.getMonth() &&
          gameDate.getFullYear() === filterStartDate.getFullYear()
        );
    },
    async fetchGames() { // Fetch the list of games
     
      try {
        const token = localStorage.getItem('token');  // Get token from local storage

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

          const transformedData = playerData.map(game => ({
            id: game.game_ID,
            size: game.size,
            hpMax: game.HP_max,
            startDate: (new Date(game.creation_date)).toLocaleDateString('en-GB'),  // Convert creation_date to Date
          }));

          // Actualizar availableGames con los datos transformados
          this.games = transformedData;
         
        } else {
         
          const errorMessageDiv = document.getElementById('error-message');
          errorMessageDiv.textContent = 'Error fetching data';
          errorMessageDiv.style.color = 'red'; 
        }

      } catch (error) {
        const errorMessageDiv = document.getElementById('error-message');
        errorMessageDiv.textContent = 'Error with the server';
        errorMessageDiv.style.color = 'red'; 
      }
  
    },
    viewRecord(gameId) {
      //aqui vemos el record del juego
    },
    viewGameDetails(gameId) { // Method to view the details of a specific game
      this.selectedGame = this.games.find((game) => game.game_ID === gameId);
    },
  },
};
</script>


<style scoped>
  /* Styles for the game finder component */
  #searchGamesTitle {
    color: #3D5CFF;  /* Styling for the title color*/
  }
  .game-finder {
    font-family: Arial, sans-serif;
    padding: 20px;
    background-color: #ffffff;  /*Styling for the background color */
  }
  
  .filter-group, .date-group, .search-group { /* Styling for the filter groups */
    margin-bottom: 10px;
  }

  /* Styles for the filters section */
  .filters {
    margin-bottom: 20px;
    display: flex;
    flex-direction: column;
    border-color: white;
  }
  
  .filters label {
    margin-right: 10px;
    color: #3D5CFF;
  }

  .selector, .search-input { /* Styling for the selector and search input */
    padding: 10px;
    border-radius: 5px;
    border: 1px solid black;
    background-color: white;
    color: black;
  }


  .dates{ /* Styling for the dates */
      color: white;
      background-color: black;
      padding: 10px;
      border: 1px solid white;
      border-radius: 5px;
      filter: invert(1); 
      width: 100px
  }
  
  /* Styles for the game list */
  .game-list {
    list-style: none;
    padding: 0;
  }
  
  /* Styles for each game item in the list */
  .game-item {
  display: flex;
  flex-direction: column;
  text-align: left;
  border: 1px solid black;
  padding: 10px;
  margin-bottom: 10px;
}

  
  .game-name {
    flex: 1;
    color: black;
    margin-bottom: 10px;
  }
  
  .view-details-button {
    background-color: #354a5e;
    color: #fff;
    border: none;
    padding: 5px 10px;
    cursor: pointer;
  }
  
  .view-details-button:hover {
    background-color: #2e3e4f;
  }
  
  /* Styles for the game details section */
  .game-details {
    margin-top: 20px;
    padding: 10px;
    border: 1px solid #ccc;
  }

  /* Styles for the search bar */
  .search-container {
    position: relative;
  }

.search-input {
  padding-right: 40px; 
  width: 200px; 
}

  .search-input::after {  /* Styling for the search icon */
    content: '';
    position: absolute;
    top: 50%;
    right: 10px;
    transform: translateY(-50%);
    width: 20px; 
    height: 20px; 
    background: url('../assets/profilePicture.png') no-repeat center center; 
    background-size: cover;
    cursor: pointer;
  }

  @media only screen and (max-width: 600px) {  
  .game-item {
    align-items: center;
  }
}

</style>

  

