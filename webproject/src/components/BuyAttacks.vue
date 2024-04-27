<template>
  <main id="sale-attacks"> <!-- Container for the Attacks on Sale component -->
    <h2 id="title">Attacks on Sale</h2> <!-- Heading for the component -->
    <section class="attacks"> <!-- Container for the list of attacks -->
    <ul class="attack-list"> <!-- List of attacks -->
      <li v-for="(attack, index) in attacksForSale" :key="index" class="attack-item">
        <span class="attack-info">
          {{ attack.attack_ID }} (position: {{ attack.positions }}), power: {{ attack.power }} 
        </span> <!-- Display attack information -->
        <button @click="buyAttack(attack)" class="buy-button">
          Buy for {{ attack.price }} $ <!-- Button to buy the attack -->
        </button>
      </li> <!-- End of attack item -->
    </ul> <!-- End of list of attacks -->
  </section>
  </main>
</template>

<script>
export default {
  data() {
    return {
      attacksForSale: [],
    };
  },
  mounted() {
    this.fetchAttacksAndCoins(); // Fetch the list of attacks when the component is mounted
  },
  methods: {
    async fetchAttacksAndCoins() { // Fetch the list of attacks

      try {
        const token = localStorage.getItem('token');

          // Fetch player data
          const playerResponse = await fetch(`https://balandrau.salle.url.edu/i3/shop/attacks`, {
            method: 'GET',
            headers: {
              'accept': 'application/json',
              'Bearer': `${token}`,
            },
          });

          if (playerResponse.ok) {
            const playerData = await playerResponse.json();
            console.log(playerData);
            this.attacksForSale = playerData;
          
          } else {
            throw new Error('Failed to fetch player data');
          }

      } catch (error) {
        
      }
    },
    async buyAttack(attack) { // Buy an attack
      try {
        
          const token = localStorage.getItem('token'); // Get token from local storage
          const response = await fetch(`https://balandrau.salle.url.edu/i3/shop/attacks/${attack.attack_ID}/buy`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Bearer': `${token}`,
            },
          });

          if (response.ok){
            const data = await response.json();
            console.log(data);
            alert(`You have successfully purchased ${attack.attack_ID} for ${attack.price} coins.`); // Alert the user that the attack was purchased
          }
          else{
            const errorData = await response.json(); 
            alert(errorData.error.message); // Alert the user that the attack was not purchased
          }
          
      } catch (error) {
        alert(`Error with the server`);
      }
    },
  },
};
</script>

<style>
/* Styles for the Attacks on Sale component */
#title {
  color: #3D5CFF;
}

#sale-attacks{ /* Container for the Attacks on Sale component */
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
  align-items: center;
}

.attack-list { /* List of attacks */
  list-style: none;
  padding: 0;
  width: 100%;
}

/* Styles for each attack item in the list */
.attack-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  margin-bottom: 10px;
}

/* Styles for the buy button */
.buy-button {
  background-color: #3D5CFF; 
  color: #ffffff; 
  padding: 8px;
  border: none;
  cursor: pointer;
}

/* Styles for the buy button on hover */
.buy-button:hover {
  background-color: #3D5CFF; 
}

/* Styles for the disabled buy button */
.buy-button[disabled] {
  background-color: #a5d6a7; 
  cursor: not-allowed; 
}

.attacks {
  width: 100%;
  max-width: 600px;
}

/* Responsive Styles for Mobile */
@media only screen and (max-width: 600px) {
  .attack-item {
    flex-direction: column; 
    align-items: flex-start;
  }

  .buy-button {
    width: 100%; 
    margin-top: 10px; 
  }

  .attacks {
    width: 100%;
    max-width: 400px;
  }
}

</style>