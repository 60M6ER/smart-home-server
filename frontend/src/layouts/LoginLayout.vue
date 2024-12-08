<template>
  <q-layout view="lHh Lpr lFf"
            class="img-background" >
    <q-page-container class="container-fluid">
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
defineOptions({
  name: 'LoginLayout'
})
</script>

<style scoped>
  ::-webkit-scrollbar {
    display: none;
  }

  .img-background {
    /* Full height */
    height: 100%;
    /* Center and scale the image nicely */
    background: #fff url(assets/background-login.jpg) no-repeat center;
    background-size: cover;
    border: solid 1px black
  }
</style>
