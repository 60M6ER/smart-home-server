<template>
  <div class="trader">
    <h1>Торговля</h1>
    <div class="left">
      <nav class="navi_trader">
        <router-link to="/trader/exchanges">Биржи</router-link><br/>
        <router-link to="/trader/bots">Боты</router-link><br/>
      </nav>
    </div>
    <div class="right">
      <router-view/>
    </div>
  </div>
</template>

<style>
.left {
  display: inline-block;
  /*position: absolute;*/
  width: 20%;
  border-right: #2c3e50 dotted 3px;
  /*top: 0;*/
  /*left: 0;*/
}

.right {
  display: inline-block;
  /*position: absolute;*/
  width: 80%;
  /*top: 0;*/
  /*right: 0;*/
}

.navi_trader {
  text-align: left;
}
</style>
