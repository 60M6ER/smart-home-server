<template>
  <router-view />
</template>

<script setup>
import { useSettingsStore } from "stores/settings";
import { useAuthenticateStore} from "stores/authenticate";
import { onMounted, watch, ref } from "vue";
import { useQuasar } from 'quasar'
import {useRouter} from "vue-router/dist/vue-router";

const $q = useQuasar();
const settingsStore = useSettingsStore();
const authenticateStore = useAuthenticateStore();
const router = useRouter();


defineOptions({
  name: 'App'
});

onMounted (() => {
  $q.dark.set(settingsStore.getDarkMode)
  autoGotoLoginPage();
})

watch(
  () => settingsStore.getDarkMode,
  (darkMode) => {
    $q.dark.set(darkMode)
  }
)
function autoGotoLoginPage() {
  let jwt_token = JSON.parse(localStorage.getItem('jwt_token'));
  if (!jwt_token) {
    router.push('/login');
  }
  setTimeout(autoGotoLoginPage, 1000);
}

</script>
