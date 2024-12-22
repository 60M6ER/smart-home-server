<template>
  <q-card flat bordered class="my-card" style="margin-bottom: 15px;">
    <q-card-section class="row justify-center">
      <div class="text-h6">Спред</div>
      <q-icon name="attach_money" size="md" v-if="item.state === 'WORKING'"/>
      <q-icon name="money_off_csred" size="md" v-if="item.state === 'FINISHED'"/>
    </q-card-section>

    <q-card-section class="q-pt-none">
      <div class="row">
        <span class="col">{{ item.description }}</span>
      </div>
      <div class="row">
        <div class="col-6">Начало: <p>{{ item.dateCreate }}</p></div>
        <div class="col-6">Конец: <p>{{ item.dateFinish }}</p></div>
      </div>
    </q-card-section>

    <q-separator inset />

    <q-card-section>
      <div class="row">
        <span class="col">Доступный объем: {{ getFormattedUSD(item.usdt_amount_start) }}</span>
      </div>
      <div class="row">
        <div class="col">Максимальный доход: {{ getFormattedUSD(item.maxProfit) }} ({{ getFormattedPercent(item.maxProfitPercent) }})</div>
      </div>
      <div class="row">
        <div class="col">Ожидаемый доход: {{ getFormattedUSD(item.profit) }} ({{ getFormattedPercent(item.profitPercent) }})</div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { onMounted, watch, ref} from "vue";

defineOptions({
  name: 'SpreadItem'
})

const props = defineProps({
  item: {
    required: true
  }
})

function getFormattedUSD(usd) {
  return Intl.NumberFormat("ru", {style: "currency", currency: "USD"}).format(usd);
}

function getFormattedPercent(percent) {
  return Intl.NumberFormat("ru", {style: "percent",
    minimumFractionDigits: 2,
    maximumFractionDigits: 2}).format(percent / 100);
}

</script>
