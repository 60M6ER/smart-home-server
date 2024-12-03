<template>
  <router-view />
</template>

<script setup>
import { useSettingsStore } from "stores/settings";
import { onMounted, watch, ref } from "vue";
import { useQuasar } from 'quasar'

const $q = useQuasar();
const settingsStore = useSettingsStore();


defineOptions({
  name: 'App'
});

onMounted (() => {
  $q.dark.set(settingsStore.getDarkMode)
})

watch(
  () => settingsStore.getDarkMode,
  (darkMode) => {
    $q.dark.set(darkMode)
  }
)

</script>
