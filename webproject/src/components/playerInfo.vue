<template>
  <!-- Main container for player information -->
  <section class="player-info">
      <!-- Header with player's name -->
      <header class="player-info-header">
        <h1 class="info-title">Welcome {{ playerName }}!</h1>
      </header>
      <!-- Main section with player's statistics -->
      <main class="player-info-main">
        <!-- Container for player details -->
        <section class="first">
          <section class="info-details">
            <!-- Display player information -->
            <p class="info-value">Experience: {{ playerExperience }}</p>
            <p class="info-value">Level: {{ playerLevel }}</p>
            <p class="info-value">Coins: {{ playerCoins }}</p>
          </section>

          <section class="info-attacks" v-if="backpackedAttacks.length > 0">
            <h2 class="info-value2">Backpacked Attacks:</h2>
            <ul class="game-list">
              <!-- Using <li> to represent each game -->
              <li v-for="game in backpackedAttacks" :key="game.id" class="game-item">
                <span class="game-name">{{ game.attack_ID }}:</span>
                <span class="game-attacks">{{ game.power }}</span> 
              </li>
            </ul>
          </section>
            <!-- Display equipped attacks for each game -->
            <section class="info-attacks" v-if="equippedAttacksGames.length > 0">
              <h2 class="info-value2">Equipped Attacks:</h2>
              <ul class="game-list">
                <!-- Using <li> to represent each game -->
                <li v-for="game in equippedAttacksGames" :key="game.id" class="game-item">
                  <span class="game-name">{{ game.attack_ID }}:</span>
                  <span class="game-attacks">{{ game.power }}</span>
                </li>
              </ul>
            </section>
            <div id="error-message"></div>
          </section>

          <!-- Buttons for log out and delete account -->
          <section class="display-btn">
            <button @click="logOut" class="btn">Log Out</button>
            <button @click="deleteAccount" class="btn">Delete Account</button>
          </section>
        </main>
  </section>
</template>

<script>
export default {
  data() {
    // Data properties for the component
    return {
      playerName: '',
      playerExperience: 0,
      playerLevel: 0,
      playerCoins: 200,
      backpackedAttacks: [],
      equippedAttacksGames: [],
      showBackpackedAttacks: false,
      showEquippedAttacks: false,
    };
  },
  async mounted() {
    try {
      const token = localStorage.getItem('token'); // Get token from local storage
      const playerId = localStorage.getItem('playerID'); // Get player ID from local storage

      // Fetch player data
      const playerResponse = await fetch(`https://balandrau.salle.url.edu/i3/players/${playerId}`, {
        method: 'GET',
        headers: {
          'accept': 'application/json',
          'Bearer': `${token}`, // Send token in the headers
        },
      });

      if (playerResponse.ok) { // Check if the response is ok
        const playerData = await playerResponse.json();

        // Update player data
        this.playerName = playerData.player_ID;
        this.playerExperience = playerData.xp;
        this.playerLevel = playerData.level;
        this.playerCoins = playerData.coins;
      } else {
        throw new Error('Failed to fetch player data'); // Throw an error if the response is not ok
      }

      // Fetch attacks information
      const attacksResponse = await fetch(`https://balandrau.salle.url.edu/i3/players/attacks`, {
        method: 'GET',
        headers: {
          'accept': 'application/json',
          'Bearer': `${token}`,
        },
      });

      if (attacksResponse.ok) {
        const attacksData = await attacksResponse.json();
        console.log(attacksData);
        
        // Update attacks information
        this.backpackedAttacks = attacksData;
        this.equippedAttacksGames = attacksData.filter(attack => attack.equipped); // Filter equipped attacks
      } else {
        throw new Error('Failed to fetch attacks data');
      }
    } catch (error) {
      
      // Handle the error, show a message to the user, etc.
    }
  },
  methods: {
    toggleBackpackedAttacks() { // Method to toggle the display of backpacked attacks
      this.showBackpackedAttacks = !this.showBackpackedAttacks; 
    },
    toggleEquippedAttacks() { // Method to toggle the display of equipped attacks
      this.showEquippedAttacks = !this.showEquippedAttacks;
    },
    deleteAccount() {
      try {
        
        const token = localStorage.getItem('token'); // Get token from local storage

        // Realizar la solicitud DELETE para eliminar la cuenta
        fetch(`https://balandrau.salle.url.edu/i3/players/`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'Bearer': `${token}`, 
          },
        }).then(async deleteResponse => {
          if (deleteResponse.ok) {
            alert("Account successfully deleted!");
            this.$router.push('/home'); // Redirect to the home page
          } else {
            const errorData = await deleteResponse.json();
              
            if (errorData.error && errorData.error.message) { // Display error message if the request fails
              const errorMessage = errorData.error.message;
              const errorMessageDiv = document.getElementById('error-message');
              errorMessageDiv.textContent = errorMessage;
              errorMessageDiv.style.color = 'red'; 
            }  
          }
        }).catch(error => { // Catch any errors and display them
          const errorMessageDiv = document.getElementById('error-message');
          errorMessageDiv.textContent = 'Error with the server';
          errorMessageDiv.style.color = 'red'; 
          
        });
      } catch (error) { // Catch any errors and display them
        const errorMessageDiv = document.getElementById('error-message');
        errorMessageDiv.textContent = 'Error with the server';
        errorMessageDiv.style.color = 'red'; 
        
      }
    },
    logOut() { // Method to log out
      this.$router.push('/home'); 
    } 
  },
};
</script>

<style scoped>
/* Styling for the main player information container */
.player-info {
  background-color: #ffffff;
  color: #fff;
  padding: 20px;
  display: flex;
  flex-direction: column;
  margin-left: 60px;
}

/* Styling for the header section containing player's name */
.player-info-header {
  width: 100%;
  margin-bottom: 5px;
  display: flex;
  align-items: flex-start;
  background: #ffffff;
}

/* Styling for the title displaying player's name */
.info-title {
  font-size: 28px;
  text-align: left;
  color: #3D5CFF;
}

/* Styling for the main section containing player's statistics */
.player-info-main {
  background-color: #ffffff;
  color: #fff;
  display: flex;
  flex-direction: row;
}

.first {
  margin-right: 100px;
}
/* Styling for the container of player details */
.info-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

/* Styling for the buttons section */
.display-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 30px;
}

/* Styling for the section displaying equipped attacks */
.info-attacks {
  display: flex;
  flex-direction: row;
  color: black;
  margin-top: 20px;
}

/* Styling for the individual statistics values */
.info-value {
  font-weight: bold;
  margin-bottom: 5px;
  color: black;
}

.info-value2 {
  margin-right: 30px;
}

/* Styling for the list of equipped attacks */
.game-list {
  list-style: none;
  padding: 0;
}

/* Styling for each game item in the list */
.game-item {
  display: flex;
  align-items: center;
  margin: 5px;
}

/* Styling for the name of the game in the equipped attacks list */
.game-name {
  font-weight: bold;
  margin-right: 5px;
}

/* Styling for the equipped attacks in the list */
.game-attacks {
  font-style: italic;
}

/* Styling for the log out and delete account buttons */
.btn {
  border: 2px solid #3D5CFF;
  border-radius: 10px;
  background-color: #ffffff;
  color: #000000;
  padding: 10px 10px;
  font-size: 20px;
  width: 200px;
}

@media (max-width: 768px) { /* Responsive adjustments */
  .player-info-main{ /* Styling for the main section containing player's statistics */
    flex-direction: column;
  }

  .first{ /* Styling for the container of player details */
    margin-right: 0px;
  }
}
</style>

