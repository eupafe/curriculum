<template>
    <div class="block2">
      <div class="block1">
        <p class="record-text">Record {{gameName}}</p>
        <table id="gameTable">
          <tr v-for="row in rows" :key="row">
            <td
              v-for="col in rows"
              :key="col"
              :class="{
                'cell': true,
                'cursor-cell': row === currentRow && col === currentColumn,
              }"
            ></td>
          </tr>
        </table>
      </div>
  
    <!-- Attack and Movement History Block -->
    <div class="block3">
      <!-- Headings for Attack History -->
      <h3>Attack History:</h3>
      <p>Example 1: Attack 1 used</p>
      <p>Example 2: Attack 2 used</p>
      <p>Example 3: Attack 3 used</p>
  
      <!-- Headings for Movement History -->
      <h3>Movement History:</h3>
      <p>Example 1: Moved up</p>
      <p>Example 2: Moved down</p>
      <p>Example 3: Moved right</p>
      <p>Example 4: Moved left</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // Data properties for the component
      gameId: '',
      rows: 0,
      hp: 0,
      currentPlayer: '',
      currentRow: 1,
      currentColumn: 1,
    };
  },
  mounted() {
    this.gameId = this.$route.params.gameId;
    this.rows = parseInt(this.$route.params.rows);
    this.hp = this.$route.params.hp;
    this.currentPlayer = this.$route.params.currentPlayer;
  },
};
</script>

<style>
/* Styling for the title */
#title {
  color: black;
}

/* Styling for the main heading */
h2 {
  color: #3D5CFF;
}

/* Styling for subheadings */
h3 {
  color: black;
}

/* Styling for the block containing game information */
.block1 {
  margin-left: 90px;
  display: flex;
  flex-direction: column;
  width: 300px;
  height: 400px;
}

/* Styling for the text indicating game ID */
.record-text {
  color: #3D5CFF;
}

/* Styling for the main container of the component */
.block2 {
  display: flex;
  flex-direction: row;
}

/* Styling for the block containing history information */
.block3 {
  display: flex;
  flex-direction: column;
  margin-top: 60px;
  margin-left: 50px;
  width: 300px;
  height: 400px;
}

/* Styling for the game board table */
#gameTable {
  border-collapse: collapse;
  width: 300px;
  height: 300px;
}

/* Styling for individual cells in the game board */
#gameTable td {
  border: 1px solid black;
}

/* Styling for normal cells */
.cell {
  width: 30px;
  height: 30px;
  background-color: white;
}

/* Styling for the cell indicating the current position */
#gameTable .cursor-cell {
  width: 30px;
  height: 30px;
  background-color: #e5e5ff;
  border: 2px solid #3d5cff;
}
</style>
  
  