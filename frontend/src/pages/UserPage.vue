<template>
  <q-page class="flex flex-center">
    <div class="q-gutter-md" style="max-width: 500px">

      <q-field outlined label="Имя" stack-label disable>
        <template v-slot:control>
          <div class="self-center full-width no-outline" tabindex="0">{{ name }}</div>
        </template>
      </q-field>
      <div v-if="isTelegram">
        <q-field outlined label="Telegram" stack-label disable>
          <template v-slot:control>
            <div class="self-center full-width no-outline" tabindex="0">Подключен</div>
          </template>
        </q-field>
      </div>
      <div v-if="!isTelegram">
        <q-field outlined label="Telegram" stack-label>
          <template v-slot:control>
            <div class="self-center full-width no-outline" tabindex="0">Не подключен</div>
            <q-btn color="secondary" label="Получить код регистрации" @click="clickOkInDialog"/>
          </template>
        </q-field>
      </div>
    </div>

    <q-dialog v-model="dialog">
      <q-card>
        <q-card-section>
          <div class="text-h6">Ключ регистрации Telegram</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          Код для регистрации в Telegram: {{ telegramCode }}.
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
import { useAuthenticateStore } from "stores/authenticate";
import { onMounted, ref } from "vue";
import UserService from "src/services/UserService";

const headerStateStore = useHeaderStateStore();
const authenticateStore = useAuthenticateStore();

defineOptions({
  name: 'IndexPage'
});

const id = ref();
const name = ref();
const isTelegram = ref();
const dialog = ref(false);
const telegramCode = ref();

onMounted(() => {
  headerStateStore.setTitle('Настройки пользователя')
  headerStateStore.setTabs([]);
  console.log(authenticateStore.username);
  UserService.getUser(authenticateStore.username)
    .then((data) => {
      console.log(data);
      id.value = data.id;
      name.value = data.name;
      isTelegram.value = data.telegram;
    });
})

function clickOkInDialog() {
  if (dialog.value) {
    UserService.getUser(authenticateStore.username)
      .then((data) => {
        console.log(data);
        id.value = data.id;
        name.value = data.name;
      });
  } else {
    UserService.getTelegramCode({
      userId: id.value
    }).then((data) => {
      console.log(data);
      telegramCode.value = data.code;
    })
  }
  dialog.value = !dialog.value;
}
</script>
