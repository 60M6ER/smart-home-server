<template>
  <div class="bots">
    <h1>Боты</h1>
    <BotList/>
  </div>
</template>

<script>
import BotList from '../../components/trader/BotList'

export default {
  name: 'BotsView',
  components: { BotList },
  data () {
  }
}
</script>

<style>

</style>
