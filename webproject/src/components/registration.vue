<template>
  <section class="registration">
   
    <h1 id="signUpTitle">Sign Up</h1>

    <!-- Form for user registration -->
    <form @submit.prevent="submitForm">
      <fieldset>
        <!-- Input field for URL profile picture -->
        <input type="url" id="url" v-model="url" placeholder="Enter URL profile pic" class="input-field" required>
        <br>

        <!-- Input field for user ID -->
        <input type="text" id="id" v-model="id" placeholder="Enter ID" class="input-field" required>
        <br>

        <!-- Input field for user password -->
        <input type="password" id="pwd" v-model="password" placeholder="Enter password" class="input-field" required>
        <br>

        <!-- Input field to confirm user password -->
        <input type="password" id="confirmpwd" v-model="confirmPassword" placeholder="Confirm password" class="input-field" required>
        <br>

        <!-- Button to submit the form -->
        <button class="custom-button1">Sign Up</button>
      </fieldset>
    </form>
    <div id="error-message"></div>
    <!-- Button to navigate to the login page -->
    <button @click.prevent="login" class="custom-button2">Log In</button>
  </section>
</template>

<script>
export default {
  data() {
    // Data properties for form input binding
    return {
      url: '',
      id: '',
      password: '',
      confirmPassword: '',
      responseData: null
    };
  },
  methods: {
    // Method to handle form submission
    async submitForm() {
      // Check if the entered passwords match
      if (this.password === this.confirmPassword) {
        // Check if the password length is between 1 and 20 characters
        if (this.password.length >= 1 && this.password.length <= 20) {
          
          // Redirect to the player-info page if passwords match
          try {
            const dataToSend = {
              player_ID: this.id,
              password: this.password,
              img: this.url || 'string'
            };
            // Send a POST request to the server with the user data
            let response = await fetch('https://balandrau.salle.url.edu/i3/players', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              },
              body: JSON.stringify(dataToSend)
            });

            if (response.ok) {
              // Redirect to the login  page if passwords match
              const errorMessageDiv = document.getElementById('error-message');
              errorMessageDiv.textContent = 'Registered';
              errorMessageDiv.style.color = 'black'; 
              setTimeout(() => {
                this.$router.push({
                  path: '/home',
                });
              }, 500);
              
              
            } else {
              const errorData = await response.json(); 
              // Display error message if the request fails
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
        } else { // Display error message if the password length is not between 1 and 20 characters
        
        }
      } else { // Display error message if the passwords do not match
      }
    },

    // Method to handle navigation to the login page
    login() {
      // when login button is clicked, navigate to the login page
      this.$router.push('/home');
    }
  }
}
</script>

<style scoped>

  .registration { /* Styling for the main container */
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px; 
    background-color: #ffffff;
    max-width: 500px; 
    margin: auto; 
  }
  
   .input-field { /* Styling for the input fields */
    width: 100%;
    padding: 10px;
    margin: 5px 0;
    border: 2px solid #3D5CFF; 
    border-radius: 5px; 
    background-color: #ffffff; 
    color: #000000; 
    outline: none;
  }
  
  .custom-button1, .custom-button2 { /* Styling for the buttons */
    padding: 10px 15px;
    border: none;
    border-radius: 5px; 
    cursor: pointer;
    outline: none;  
  }

  .custom-button1 { /* Styling for the sign up button */
    margin-top: 30px;
    background-color: #3D5CFF;
    color: #ffffff;
  }

  .custom-button2 { /* Styling for the log in button */
    background-color: #ffffff;
    color: #3D5CFF;
  }

   #signUpTitle { /* Styling for the sign up title */
    color: #3D5CFF;
  }

  fieldset { /* Styling for the fieldset */
    border: none; 
    padding: 10px; 
    margin: 0; 
  }


/* Responsive adjustments */
@media (max-width: 768px) {
  .registration {
    width: 95%; 
  }

  .input-field, .custom-button1, .custom-button2 { /* Styling for the input fields and buttons */
    padding: 8px;  
  }

}

</style>