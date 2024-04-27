<template>
  <section class="login-form"> <!-- Login form section -->
    <header> <!-- Header section -->
      <h2 id="logInTitle">Log In</h2> <!-- Title for the login form -->
    </header> 
    <form @submit.prevent="submitForm"> <!-- Form for user login -->
      <input type="text" v-model="userId" placeholder="Enter ID" class="input-field" required>
      <br>
      <input type="password" v-model="password" placeholder="Enter password" class="input-field" required>
      <br>
      <br>
      <button class="custom-button1">Enter</button> <!-- Button to submit the form -->
    </form>
    <footer> <!-- Footer section -->
      <div id="error-message"></div> <!-- Error message div -->
      <button @click="signup" class="custom-button2">Sign Up</button> <!-- Button to navigate to the signup page -->
    </footer>
  </section>
</template>

<script>
export default { // Data property for the component
  data() {
    return {
      userId: '',
      password: '',
      isMobile: window.innerWidth <= 768,
    };
  },
  methods: {
    async submitForm() { // Method to handle form submission
      try {
        const response = await fetch('https://balandrau.salle.url.edu/i3/players/join', {
          method: 'POST', // Send a POST request
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            player_ID: this.userId, // Send the user ID and password in the body of the request
            password: this.password,
          }),
        });

        if (response.ok) {
          const responseData = await response.json();
          //responseData is the token
          const token = responseData.token;
          localStorage.setItem('token', token);
          localStorage.setItem('playerID', this.userId);
          console.log(token);

          // Assuming the response contains some user information, you can redirect to the player information page
          this.$router.push({
            path: '/player-info',
          });
        } else {
          const errorData = await response.json(); 
              
          if (errorData.error && errorData.error.message) { // Check if the response contains an error message
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
    },
    signup() { // Method to navigate to the signup page
      this.$router.push('/register');
    },
  },
};
</script>


<style scoped>

/* Scoped styles for the login form */
.login-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px; 
  background-color: #ffffff;
  max-width: 400px;
  margin: auto;
}

/* Specific styles for the header */
.login-form header {
  width: 100%;
  background-color: #ffffff; 
  padding: 10px 0;
  text-align: center;
}

/* Media query for mobile devices */
@media screen and (max-width: 768px) {
  .login-form {
    width: 95%;
    max-width: none;
  }

  .input-field {
    width: calc(100% - 20px); 
  }
}

/* Styles for input fields */
.input-field {
  width: 100%;
  padding: 10px;
  margin: 5px 0;
  border: 2px solid #3D5CFF; 
  border-radius: 5px; 
  background-color: #ffffff; 
  color: #000000; 
  outline: none;
}

/* Styles for the password toggle button in WebKit browsers */
.input-field::-webkit-password-toggle-button {
  -webkit-filter: invert(100%); 
  filter: invert(100%); 
}

/* Styles for the login form submit button */
.custom-button1 {
  padding: 10px 15px;
  border: none;
  border-radius: 5px; 
  background-color: #3D5CFF; 
  color: #ffffff;
  cursor: pointer;
  outline: none; 
}

/* Styles for the signup button */
.custom-button2 {
  padding: 10px 15px;
  border: none;
  border-radius: 5px; 
  background-color: #ffffff; 
  color: #3D5CFF;
  cursor: pointer;
  outline: none; 
}

/* Styles for the login title */
#logInTitle {
  color: #3D5CFF; 
}

</style>
