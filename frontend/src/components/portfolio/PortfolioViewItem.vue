<template>
  <div class="q-pa-md q-gutter-sm" style="max-width: 500px" @click="updateAllBalances">
    Портфель: {{ usdAmount }}, {{ rubAmount }}.
  </div>
</template>

<script setup>
import { onMounted, ref} from "vue";
import PortfolioService from "src/services/PortfolioService";


defineOptions({
  name: 'PortfolioViewItem'
})

const usdAmount = ref();
const rubAmount = ref();


onMounted(() => {
  updateViewPortfolio();
})

function updateViewPortfolio() {

  PortfolioService.gerView().then(data => {
    usdAmount.value = Intl.NumberFormat("ru", {style: "currency", currency: "USD"}).format(data.usdAmount);
    rubAmount.value = Intl.NumberFormat("ru", {style: "currency", currency: "RUB"}).format(data.rubAmount);
  });

  setTimeout(updateViewPortfolio, 5000);
}

function updateAllBalances() {
  PortfolioService.updateBalances();
}

</script>
