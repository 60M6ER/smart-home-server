<template>

    <q-page-container>
      <router-view />
    </q-page-container>

</template>

<script setup>
import { useHeaderStateStore} from "stores/headerState";
import { onMounted, ref } from "vue";
import ExchangeService from "src/services/ExchangeService";

const headerStateStore = useHeaderStateStore();

defineOptions({
  name: 'ExchangesPage'
});

onMounted(() => {
  let allExchanges = ExchangeService.gerAll();
  allExchanges.then((value) => {
    let newButtons = [];
    for (let i = 0; i < value.length; i++) {
      newButtons.push({
        title: value[i].name,
        id: value[i].id,
        property: value[i].vendor,
        link: '/exchanges/' + value[i].vendor
      })
    }
    headerStateStore.setTabs(newButtons);
  })
  headerStateStore.setTitle('Настройка бирж')

})
</script>
