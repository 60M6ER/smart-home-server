<template>
  <table>
    <tr v-for="bot in bots" :key="bot.id">
      <td>{{ bot.name }}</td>
    </tr>
  </table>
</template>

<script>
import axios from 'axios'

export default {
  name: 'BotList',
  data () {
    return {
      bots: []
    }
  },
  mounted () {
    axios
      .get('http://localhost:9031/v1/bots')
      .then(response => (this.bots = response))
  }
}
</script>

<style scoped>

</style>
