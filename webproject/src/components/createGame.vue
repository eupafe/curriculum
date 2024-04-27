<template>
  <main class="create-game"> <!-- Main container for creating a game -->
    <h2 id="createArenaTitle">Create an Arena</h2> <!-- Heading for the component -->
    <form @submit.prevent="createGame"> <!-- Form for creating a game -->
      <div class="form-group">
        <label id="createArenaTitle">Enter Game ID</label> <!-- Label for the input field -->
        <input type="text" v-model="gameId" id="gameId" required> <!-- Input field for the game ID -->
      </div> 

      <div class="form-group"> <!-- Form group for the matrix size -->
        <label id="createArenaTitle">Enter size of Arena (2-10)</label>
        <input type="number" v-model="matrixSize" id="matrixSize" min="2" max="10" required>
      </div>

      <div class="form-group"> <!-- Form group for the starting HP -->
        <label id="createArenaTitle">Enter starting HP for players</label> 
        <input type="number" v-model="hp" id="hp" required> <!-- Input field for the starting HP -->
      </div>  

      <button class="create-button" type="submit">Create</button>
      <div id="error-message"></div>
    </form>  
  </main> 
</template> 

<script>
export default {
  data() {
    return {
      gameId: '',
      matrixSize: 2, // Default value, you can adjust as needed
      hp: 0,
    };
  },
  methods: {
    async createGame() { // Method to handle form submission 
      
      try {
        
        const token = localStorage.getItem('token');
       
        const response = await fetch('https://balandrau.salle.url.edu/i3/arenas', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Bearer': `${token}`,
          },
          body: JSON.stringify({
            game_ID: this.gameId, // Send the game ID, matrix size, and starting HP in the body of the request
            size: this.matrixSize,
            HP_max: this.hp,
          }),
        });

        if (response.ok) {
          const errorMessageDiv = document.getElementById('error-message');
          errorMessageDiv.textContent = '';

          this.$router.push({ name: 'arena', params: { gameId: this.gameId, rows: this.matrixSize, hp: this.hp, currentPlayer: 'player1' } }); 
          // Redirect to the arena page
             
        } else {
          const errorData = await response.json(); 
              
          if (errorData.error && errorData.error.message) {
            const errorMessage = errorData.error.message;
            const errorMessageDiv = document.getElementById('error-message');
            errorMessageDiv.textContent = errorMessage;
            errorMessageDiv.style.color = 'red'; 
          }  
        }
      } catch (error) { // Catch any errors and display them
        const errorMessageDiv = document.getElementById('error-message');
        errorMessageDiv.textContent = 'Error with the server';
        errorMessageDiv.style.color = 'red'; 
      }

    }
  }
};
</script>

<style scoped>
.create-game { /* Styles for the main container */
  text-align: center;
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  background: white;
}

.form-group { /* Styles for the form group */
  margin: 10px 0;
  text-align: left;
}

label { /* Styles for the label */
  display: block;
  font-weight: bold;
}

input { /* Styles for the input field */
  width: 100%;
  padding: 10px;
  margin: 5px 0;
  border: 2px solid #3D5CFF; 
  border-radius: 5px; 
  background-color: #ffffff; 
  color: #000000; 
  outline: none;
}

.create-button { /* Styles for the create button */
  padding: 10px 15px;
  border: none;
  border-radius: 5px; 
  background-color: #3D5CFF; 
  color: #ffffff; 
  cursor: pointer;
  outline: none; 
  width: 150px;
  margin: 0 auto;
}

.create-button:hover { /* Styles for the create button when hovered over */
  background-color: #0056b3;
}

#createArenaTitle { /* Styles for the title */
  color: #3D5CFF;
}

/* Responsive Styles for Mobile */
@media only screen and (max-width: 600px) {
  .create-game {
    max-width: 90%;
    padding: 10px;
  }

  input, .create-button {
    padding: 8px; 
  }

}
</style>
