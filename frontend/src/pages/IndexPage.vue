<template>
  <q-page class="flex flex-center">
    <img
      alt="Quasar logo"
      src="~assets/quasar-logo-vertical.svg"
      style="width: 200px; height: 200px"
    >
  </q-page>
</template>

<script setup>
import { useHeaderStateStore} from "stores/headerState";
import { onMounted, ref } from "vue";

const headerStateStore = useHeaderStateStore();

defineOptions({
  name: 'IndexPage'
});

onMounted(() => {
  headerStateStore.setTitle('Торговля криптовалютой')
  headerStateStore.setTabs([]);
})
</script>
