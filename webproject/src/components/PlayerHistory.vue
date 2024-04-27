<template>
    <section class="Player-History">
      <article class="info">
        <!-- Heading for the player information section -->
        <h2>Information on {{ player.player_ID }}</h2>
        <!-- Display player information -->
        <p><strong>Experience:</strong> {{ player.xp }}</p> <!-- Display player experience -->
        <p><strong>Level:</strong> {{ player.level }}</p> <!-- Display player level -->
        <p><strong>Coins:</strong> {{ player.coins }}</p> <!-- Display player coins -->
        <p><strong>Games Played:</strong> {{ player.gamesPlayed }}</p>
        <p><strong>Games Won:</strong> {{ player.gamesWon }}</p>
      </article>
      <article class="attacks" >
          <div >
            <button @click="toggleBackpackAttacks">Show Backpacked Attacks</button> <!-- Button to toggle display of backpacked attacks -->
            <div v-if="showBackpackAttacks"> <!-- Display backpacked attacks if the button is clicked -->
              <h3>Backpacked Attacks:</h3>
              <ul style=" list-style-type: none;">
                <li v-for="attack in player.backpackAttacks" :key="attack.id">
                  {{ attack.attack_ID }}
                </li>
              </ul>
            </div>
          </div>
          <!-- Display equipped attacks for each game -->
          <div>
            <button @click="toggleEquippedAttacks">Show Equipped Attacks</button>
            <div v-if="showEquippedAttacks">
              <h3>Equipped Attacks:</h3>
              <ul  style=" list-style-type: none;">
                <li v-for="attack in player.equippedAttacks" :key="attack.id">
                  {{ attack.attack_ID }}
                </li>
              </ul>
            </div>
          </div>
        
        </article>
      </section>
  </template>
  
  <script>
  export default { // Data property for the component
    data() {
      return {
        player: {
          player_ID: '',
          xp: 0,
          level: 0,
          coins: 0,
          gamesPlayed: 0,
          gamesWon: 0,
          backpackAttacks: [],
          equippedAttacks: [],
        },
        showBackpackAttacks: false, 
        showEquippedAttacks: false,
      };
    },
    created() { // Fetch player data when the component is created
      this.loadPlayer();
    },
    methods: {
      async loadPlayer() { // Fetch player data
        try {
          const playerId = this.$route.params.id;
          
          const token = localStorage.getItem('token'); // Get token from local storage
          const response = await fetch(`https://balandrau.salle.url.edu/i3/players/${playerId}`, {
            method: 'GET', // Send a GET request
            headers: {
              'Bearer': `${token}`,
            },
          });

          if (response.ok) { // Check if the response is ok
            const playerData = await response.json();
            this.player = playerData;
            // Fetch statistics
            this.fetchStatistics();
            // Fetch attacks
            this.fetchAttacks();

          } else {
            throw new Error('Failed to fetch player data');
          }
        } catch (error) {
        }
      },
      async fetchAttacks(){ // Fetch attacks
        try {
          const playerId = this.$route.params.id; // Get player ID from the route parameters
          
          const token = localStorage.getItem('token');
          const response = await fetch(`https://balandrau.salle.url.edu/i3/players/${playerId}/attacks`, {
            method: 'GET',
            headers: {
              'Bearer': `${token}`,
            },
          });

          if (response.ok) {
            const playerAttacks = await response.json();
            this.player.backpackAttacks = playerAttacks;
            this.player.equippedAttacks = playerAttacks.filter(attack => attack.equipped);
           
          } else {
            throw new Error('Failed to fetch player data');
          }
        } catch (error) {
        }
      },
      async fetchStatistics(){ // Fetch statistics
        try {
          const playerId = this.$route.params.id; // Get player ID from the route parameters
          
          const token = localStorage.getItem('token');
          const response = await fetch(`https://balandrau.salle.url.edu/i3/players/${playerId}/statistics`, {
            method: 'GET',
            headers: {
              'Bearer': `${token}`,
            },
          });

          if (response.ok) {
            const statistics = await response.json();
            this.player.gamesPlayed = statistics.games_played;
            this.player.gamesWon = statistics.games_won;
           
          } else {
            throw new Error('Failed to fetch player data');
          }
        } catch (error) {
        }
      },
      toggleBackpackAttacks() {   // Method to toggle display of backpacked attacks
        this.showBackpackAttacks = !this.showBackpackAttacks;
      },
      toggleEquippedAttacks() {     // Method to toggle display of equipped attacks
        this.showEquippedAttacks = !this.showEquippedAttacks;
      },
    },
  };
  </script>
  
  <style scoped>

  .Player-History { /* Styles for the player history component */
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
  }

  .info, .attacks { /* Styles for the player information and attacks sections */
    max-width: 90%;
    margin: 10px;
    background-color: #f5f5f5;
    border-radius: 10px;
    padding: 20px;
  }
  .info p { 
  color: black;
}

  .attacks { /* Styles for the attacks section */
    display: flex;
    flex-direction: column;
  }

  button { /* Styles for the buttons */
    margin-top: 10px;
    padding: 5px 10px;
    cursor: pointer;
  }

  li { /* Styles for the list items */
    color: black;
  }

  @media (max-width: 768px) { /* Responsive adjustments */
    .Player-History { 
      flex-direction: column;
    }

    .info, .attacks {
      width: 100%;
      margin: 5px 0;
    }

    .attacks { 
      flex-direction: column;
    }
  }
</style>
  