<template>
  <q-layout view="hHh lpr fFf">

    <q-header reveal elevated class="bg-primary text-white" height-hint="98">
      <q-toolbar>
        <q-btn dense flat round icon="menu" @click="toggleLeftDrawer" />

        <q-toolbar-title>
          <q-avatar>
            <img src="~assets/logo_mini2.png"
                 style="width: 50px;">
          </q-avatar>
          {{ title }}
        </q-toolbar-title>
        <q-toggle
          v-model="darkMode"
          color="green"
          v-bind:label="titleDarkMode"
          @click="settingsStore.toggle()"
          left-label
        />
        <div @click="logout()">Quasar v{{ $q.version }}</div>
      </q-toolbar>

      <q-tabs align="left">
        <q-route-tab
          v-for="tab in tabs"
          :key="tab.id"
          :label="tab.title"
          :to="tab.link"
          />
      </q-tabs>
    </q-header>

    <q-drawer show-if-above v-model="leftDrawerOpen" side="left" bordered>
        <q-list bordered separator>
          <q-item clickable v-ripple @click="router.push('/')">
            <q-item-section >
              Главная
            </q-item-section>
          </q-item>
          <q-item clickable v-ripple @click="router.push('/exchanges')">
            <q-item-section>
              Биржи
            </q-item-section>
          </q-item>
          <q-item clickable v-ripple @click="router.push('/user')">
            <q-item-section>
              Настройки пользователя
            </q-item-section>
          </q-item>
        </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>

    <q-footer elevated class="bg-grey-8 text-white">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar>
            <img src="~assets/logo_mini2.png" style="width: 70px;">
          </q-avatar>
          <div>Автобот</div>
        </q-toolbar-title>
        <PortfolioViewItem></PortfolioViewItem>
      </q-toolbar>
    </q-footer>

  </q-layout>
</template>

<script setup>
import { useQuasar } from 'quasar'
import { useSettingsStore } from 'stores/settings';
import { useHeaderStateStore} from "stores/headerState";
import { onMounted, computed, watch, ref } from "vue";
import AuthService from "src/services/AuthService";
import { useRouter } from "vue-router";
import PortfolioViewItem from "components/portfolio/PortfolioViewItem";


defineOptions({
  name: 'MainLayout'
})

const $q = useQuasar();
const router = useRouter();
const settingsStore = useSettingsStore();
const headerStateStore = useHeaderStateStore();


const darkMode = computed({
  get () {
    return settingsStore.getDarkMode
  },
  set (newValue) {
    // ignore
  }
});

const title = computed({
  get () {
    return headerStateStore.getTitle
  }
});

const tabs = computed({
  get () {
    return headerStateStore.getTabs
  }
})

const titleDarkMode = ref('');

watch(() => settingsStore.getDarkMode,
  (dark) => {
  updateTitleDarkMode()
  })

onMounted(() => {
  updateTitleDarkMode();
})

function updateTitleDarkMode () {
  if (darkMode.value)
    titleDarkMode.value = 'Темный режим'
  else
    titleDarkMode.value = 'Светлый режим'
}

function logout() {
  console.log('logout.')
  AuthService.logout();
}

const leftDrawerOpen = ref(false)

function toggleLeftDrawer () {
  leftDrawerOpen.value = !leftDrawerOpen.value
}
</script>
