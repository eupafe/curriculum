<template>
  <section class="attack-container"> <!-- Main container for the component -->
    <div class="block1"> <!-- Block for creating attacks -->
      <h2 id="titles">Create Attacks</h2> <!-- Heading for the block -->
      <form @submit.prevent="createAttack" class="attack-form">
        <input v-model="newAttack.name" type="text" placeholder="Enter attack name" class="input-field" required> <!-- Input field for attack name -->
        <input v-model="newAttack.position" type="text" placeholder="Enter positions" class="input-field" required> <!-- Input field for attack positions -->
        <button class="button" type="submit">Create Attack</button> <!-- Button to submit the form -->
      </form>
      <div id="error-message"></div> <!-- Error message div -->
    </div>
    <div class="block2">
      <h2 id="titleAttack">Your Attacks:</h2> <!-- Heading for the block -->
      <ul class="attack-list"> <!-- List of attacks -->
        <li v-for="(attack, index) in userAttacks" :key="index" class="attack-item"> <!-- Using <li> to represent each attack -->
          <span class="attack-info">
            {{ attack.name }} - Position: {{ attack.position }}  <!-- Display attack name and position -->
          </span>
          <form @submit.prevent="sellAttack(index)" class="sell-attack">
            <input v-if="attack.textButton !== 'In store'" v-model="attack.askingPrice" type="number" id="input-price" placeholder="Enter price" required>
            <button class="button">{{ attack.textButton }}</button> <!-- Button to sell the attack -->
          </form>
        </li>
      </ul>
    </div>
  </section>
</template>

<script>
export default {
  data() {
    return {
      newAttack: {
        name: '',
        position: '',
        textButton: 'Sell',
        askingPrice: 0,
      },
      userAttacks: [],
    };
  },
  mounted() {
    this.fetchAttacks(); // Fetch the list of attacks when the component is mounted
  },
  methods: {
    async fetchAttacks() { // Fetch the list of attacks
          
      try {
        const token = localStorage.getItem('token');

        const response = await fetch('https://balandrau.salle.url.edu/i3/players/attacks', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Bearer': `${token}`,
          },
        });

        const data = await response.json(); // Get the list of attacks from the response
        
        this.userAttacks = data.map(attack => ({  // Map the list of attacks to the userAttacks array
            name: attack.attack_ID, 
            position: attack.positions, 
            textButton: attack.on_sale ? 'In store' : 'Sell', 
        }));     

      } catch (error) {
        const errorMessageDiv = document.getElementById('error-message');
        errorMessageDiv.textContent = 'Error with the server';
        errorMessageDiv.style.color = 'red'; 
      }
    },
    async createAttack() { // Method to create an attack
      try {
        const token = localStorage.getItem('token');

        const response = await fetch('https://balandrau.salle.url.edu/i3/shop/attacks', {
          method: 'POST', // Send a POST request
          headers: {
            'Content-Type': 'application/json',
            'Bearer': `${token}`,
          },
          body: JSON.stringify({
            attack_ID: this.newAttack.name, // Send the attack name and positions in the body of the request
            positions: this.newAttack.position,
            img: 'https://www.shutterstock.com/image-vector/battle-game-cross-swords-concept-260nw-1888899151.jpg', // Default image
          }),
        });

        if (response.ok) {
          const errorMessageDiv = document.getElementById('error-message');
          errorMessageDiv.textContent = '';

        } else { // Display error message if the request fails
         
          const errorData = await response.json(); 
              
          if (errorData.error && errorData.error.message) {
              const errorMessage = errorData.error.message;
              const errorMessageDiv = document.getElementById('error-message');
              errorMessageDiv.textContent = errorMessage;
              errorMessageDiv.style.color = 'red'; 
          }
        }

        // Update userAttacks with the new list of attacks after creation
        await this.fetchAttacks();

        // Reset the newAttack object
        this.newAttack = {
          name: '',
          position: '',
          textButton: 'Sell',
        };
      } catch (error) {
        const errorMessageDiv = document.getElementById('error-message');
        errorMessageDiv.textContent = 'Error with the server';
        errorMessageDiv.style.color = 'red'; 
      }
    },
    async sellAttack(index) {
      try {
        const token = localStorage.getItem('token'); // Get token from local storage
        if (this.userAttacks[index].textButton === 'Sell') { 
          
          const response = await fetch(`https://balandrau.salle.url.edu/i3/shop/attacks/${this.userAttacks[index].name}/sell`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Bearer': `${token}`,
            },
            body: JSON.stringify({
              price: this.userAttacks[index].askingPrice // Send the price in the body of the request
            }),
          });
          
          if (response.ok){
            // Update userAttacks with the new list of attacks after selling
            await this.fetchAttacks();
            this.userAttacks[index].textButton = 'In store';
          }
          else{
            const errorData = await response.json(); 
              
            if (errorData.error && errorData.error.message) {
                const errorMessage = errorData.error.message;
                const errorMessageDiv = document.getElementById('error-message');
                errorMessageDiv.textContent = errorMessage;
                errorMessageDiv.style.color = 'red'; 
            }
          }
        }
      } catch (error) { // Catch any errors and display them
          const errorMessageDiv = document.getElementById('error-message');
          errorMessageDiv.textContent = 'Error with the server';
          errorMessageDiv.style.color = 'red'; 
      }
    },
  },
};
</script>
  
<style>
 
  #titles {  /* Title for the block */
    color: #3D5CFF;
  }

  #title-attack{ /* Title for the block */
    color: black;
  }

  .input-field { /* Input field styles */
    width: 100%;
    padding: 10px;
    margin: 5px 0;
    border: 2px solid #3D5CFF; 
    border-radius: 5px; 
    background-color: #ffffff; 
    color: #000000; 
    outline: none;
  }

  #input-price{ /* Input field styles */
    width: 80px;
    margin: 5px 0;
    border: 2px solid #3D5CFF; 
    border-radius: 5px; 
    background-color: #ffffff; 
    color: #000000; 
    outline: none;
  }

  .attack-container { /* Main container for the component */
    margin: 20px auto;
    background-color: white;
    display: flex;
    flex-direction: row;
  }

  .block2{ /* Block for displaying attacks */
    margin-left: 150px;
    display: flex;
    flex-direction: column;
    width: 900px;
    align-items: center;
  }
  
  .attack-form { /* Form for creating attacks */
    margin-bottom: 20px;
  }
  
  label { /* Label styles */
    display: block;
    margin-bottom: 5px;
  }
  
  input { /* Input field styles */
    width: 100%;
    padding: 8px;
    margin-bottom: 10px;
  }
  
  .attack-list { /* List of attacks styles */
    list-style: none;
  }
  
  .attack-item { /* Attack item styles */
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: black;
    max-width: 1000px;
  }
  
  .button { /* Button styles */
    padding: 10px 15px;
    border: none;
    border-radius: 5px; 
    background-color: #3D5CFF; 
    color: #ffffff;
    cursor: pointer;
    outline: none; 
    margin-top: 20px;
  }

/* Responsive Styles for Mobile */
@media only screen and (max-width: 600px) {
  .attack-container {
    flex-direction: column; 
  }

  .block1, .block2 {
    margin-left: 0;
    width: 100%; 
    height: 100px;
  }

  #titleAttack {
    margin-left: 30px;
    margin-top: 0%;
  }

  .button {
    width: 150px; 
    margin: 10px 0; 
  }
}

</style>
  