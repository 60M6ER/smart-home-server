<template>
  <q-page class="q-pa-md row">
      <div class="col">
        <img
          alt="Quasar logo"
          src="~assets/quasar-logo-vertical.svg"
          style="width: 200px; height: 200px"
        >
      </div>
      <div class="col-2 column q-gutter-sm items-center justify-start">
        <div class="text-h4">Спреды</div>
        <q-scroll-area class="col" style="width: 100%;">
          <SpreadItem v-for="spread in spreads" :key="spread.id" :item="spread"></SpreadItem>
        </q-scroll-area>
      </div>
  </q-page>
</template>

<script setup>
import { useHeaderStateStore} from "stores/headerState";
import { onMounted, ref } from "vue";
import SpreadsService from "src/services/SpreadsService";
import SpreadItem from "components/SpreadItem";

const headerStateStore = useHeaderStateStore();

defineOptions({
  name: 'IndexPage'
});

const spreads = ref([]);

onMounted(() => {
  headerStateStore.setTitle('Торговля криптовалютой')
  headerStateStore.setTabs([]);
  updateSpreads();
})

function updateSpreads() {
  SpreadsService.gerSpreads().then((data) => {
    spreads.value = data;
  });
  setTimeout(updateSpreads, 1000);
}
</script>
