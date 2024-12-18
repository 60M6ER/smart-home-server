<template>

    <q-page-container>
      <div class="q-pa-md bg-grey-9 text-white" style="max-width: 1000px">
        <div class="q-gutter-md" style="max-width: 500px">

          <q-table title="Балансы на биржах" rows="exchangeBalances" columns="columns" row-key="row.exchange"/>
          <div>
            <q-btn color="secondary" label="Сохранить" @click="saveExchangeItem"/>
            <div class="text-h6" v-if="message !== ''">{{ message }}</div>
          </div>
        </div>
      </div>
    </q-page-container>

</template>

<script setup>
import { useHeaderStateStore} from "stores/headerState";
import { onMounted, ref } from "vue";
import PortfolioService from "src/services/PortfolioService";

const headerStateStore = useHeaderStateStore();

defineOptions({
  name: 'ExchangesPage'
});

const exchangeBalances = ref([]);

const columns = [
  {
    name: 'exchange',
    required: true,
    label: 'Биржа',
    align: 'left',
    field: row => row.exchange,
    format: val => `${val}`,
    sortable: true
  },
  { name: 'usd',
    align: 'center',
    label: 'USD',
    field: row => Intl.NumberFormat("ru", {style: "currency", currency: "USD"}).format(row.usd),
    sortable: true }
]

onMounted(() => {

  PortfolioService.getExchangeBalances().then((data) => {
    exchangeBalances.value = data;
  })

  headerStateStore.setTabs([]);
  headerStateStore.setTitle('Настройка бирж')

})
</script>
