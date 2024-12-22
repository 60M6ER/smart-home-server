<template>
  <q-page class="flex flex-center">

    <div class="column q-gutter-sm flex-center">
      <q-table
        title="Балансы на биржах"
        :rows="exchangeBalances"
        :columns="columns"
        row-key="exchange"
        style="min-width: 700px;"
      />
      <div>
        <q-btn color="secondary" label="Выровнять балансы" @click="clickOkInDialog"/>
      </div>
    </div>

    <q-dialog v-model="dialog">
      <q-card>
        <q-card-section>
          <div class="text-h6">Выравнивание балансов</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          Коммиссия за переводы составит: {{ fee }} USDT.
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="OK" color="primary" @click="clickOkInDialog"/>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { useHeaderStateStore} from "stores/headerState";
import { onMounted, ref } from "vue";
import PortfolioService from "src/services/PortfolioService";
import UserService from "src/services/UserService";

const headerStateStore = useHeaderStateStore();

defineOptions({
  name: 'BalancePage'
});

const exchangeBalances = ref([]);
const dialog = ref(false);
const fee = ref('...');

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

function clickOkInDialog() {
  if (dialog.value) {
    fee.value = '...';
  } else {
    PortfolioService.getFeeEqualize()
      .then((data) => {
      fee.value = data;
    })
  }
  dialog.value = !dialog.value;
}

</script>
