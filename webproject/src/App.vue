<template>
  <div id="app">
    <header id="appHeader">
      <nav id="logoMenu">
        <div class="logo-container">
          <img src="src/assets/logo.png" alt="logo" class="logo"/>
        </div>
        <div v-if="!isHomeOrSignupRoute" class="nav-menu">
          <ul class="menu-list">
            <li><router-link to="/create-game" class="titles">Game</router-link>
              <ul class="submenu">
                <li><router-link to="/create-game" class="titles">Create an Arena</router-link></li>
                <li><router-link to="/available-games" class="titles">Enter Available Arena</router-link></li>
                <li><router-link to="/game-finder" class="titles">Search games</router-link></li>
                <li><router-link to="/game-history" class="titles">Players Game History</router-link></li>
              </ul>
            </li>
            <li><router-link to="/player-lists" class="titles">Players</router-link>
              <ul class="submenu">
                <li><router-link to="/player-lists" class="titles">Player's Ranking</router-link></li>
              </ul>
            </li>
            <li ><router-link to="/create-sell-attacks" class="titles">Store</router-link>
              <ul class="submenu">
                <li><router-link to="/create-sell-attacks" class="titles">Sell/Create Attacks</router-link></li>
                <li><router-link to="/buy-attacks" class="titles">Buy Attacks</router-link></li>
              </ul>
            </li>
            <li >
              <img src="src/assets/profilePicture.png" alt="Profile" class="profile-image" />
              <ul class="submenu">
                <li><router-link to="/player-info" class="titles">Your Profile</router-link></li>
                <li><router-link to="/attacks" class="titles">Manage Attacks</router-link></li>
              </ul>
            </li>
          </ul>
        </div>
      </nav>
    </header>
    <main>
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showSubmenu: {
        game: false,
        players: false,
        store: false
      }
    };
  },
  created() {
   /*
    const storedToken = localStorage.getItem('token');

    if (storedToken) {
     
      this.$router.push('/player-info');
    }*/
  },
  methods: {
    toggleSubMenu(menu) {
      this.showSubmenu[menu] = !this.showSubmenu[menu];
    },
  },
  computed: {
    isHomeOrSignupRoute() {
      return this.$route.path === '/home' || this.$route.path === '/register';
    }
  }
};
</script>


<style>

body, #app {
  margin: 0;
  padding: 0;
  height: 100vh;
  width: 100vw;
  background: white;
}

#appHeader {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: white;
}

#logoMenu {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.nav-menu, .menu-list, .menu-list li, .submenu {
  background: white; /* Set background to white */
}

.logo-container {
  padding-left: 20px;
  background-color: white;
}

.logo {
  width: 150px; /* Adjusted size for better responsiveness */
  height: auto;
  background-color: white;
}

.profile-image {
  width: 50px; /* Smaller default width */
  height: 50px; /* Smaller default height */
  border-radius: 50%; /* Keeps it circular */
  object-fit: cover; /* Ensures the image covers the area without stretching */
}

.nav-menu {
  display: flex;
}

.menu-list {
  list-style-type: none;
  padding: 0;
  display: flex;
  margin-left: auto; /* Aligns the menu to the right */
}

.menu-list li {
  padding: 0 10px;
}

.menu {
  background: white; /* Sets the background of the menu to white */
  color: black; /* Sets the default text color to black */
}

.submenu li {
  background: white; /* Set background to white */
  display: block; /* Ensure the li elements take up the full width */
}

.submenu li,
.submenu li a.titles { /* Targeting both submenu list items and the router link titles */
  background: white; /* Set background to white */
  color: black; /* Set text color to black */
}

.submenu li:hover,
.submenu li a.titles:hover { /* Targeting hover state of both submenu list items and router link titles */
  background: white !important; /* Ensure background stays white on hover */
  color: blue; /* Change text color to indicate hover, adjust as needed */
}

.titles, .submenu .titles {
  color: black; /* Set text color to black */
  display: block; /* Ensure the links take up the full width of li */
  padding: 5px 0; /* Adjust padding as needed */
}

.titles:hover, .submenu .titles:hover {
  color: blue; /* Change text color to indicate hover */
  background: white; /* Keep the background white on hover */
}

/* Responsive adjustments */
/* Responsive adjustments */
@media (max-width: 768px) {
  #appHeader {
    flex-direction: column;
    align-items: center;
  }

  #logoMenu {
    flex-direction: column;
    align-items: center;
  }

  .nav-menu, .menu-list {
    flex-direction: column;
    align-items: center;
    width: 100%;
  }

  .menu-list li {
    padding: 5px 0;
    display: block;
    text-align: center;
  }

  .logo {
    width: 100px; /* Adjust as needed */
  }

  .profile-image {
    width: 35px; /* Adjust as needed */
    height: 35px; /* Adjust as needed */
  }
}

</style>