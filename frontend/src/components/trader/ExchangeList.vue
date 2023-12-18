<template>
  <table>
    <tr v-for="ex in exchanges">
      <td>{{ ex.name }}</td>
    </tr>
  </table>
</template>

<script>
export default {
  name: "ExchangeList",
  data: {
    exchanges: []
  }
}
</script>

<style scoped>

</style>
