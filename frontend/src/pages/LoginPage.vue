<template>
  <q-page  class="docs-input row justify-center">
    <div class="col-xl-4 col-lg-6 col-md-6 col-sm-12 col-xs-12 q-pa-xl dark">
      <div class="text-center" style="color:white">
        <img
          alt="Quasar logo"
          src="~assets/logo_full2.png"
          style="width: 200px;"
        >
      </div>
      <q-form
        @submit="authenticate"
        class="q-gutter-md"
      >
        <div class="q-mt-xl">
          <q-input outlined v-model="username" label="Логин"/>
          <q-input v-model="password" filled :type="isPwd ? 'password' : 'text'" label="Пароль">
            <template v-slot:append>
              <q-icon
                :name="isPwd ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="isPwd = !isPwd"
              />
            </template>
          </q-input>
          <q-btn color="primary" glossy class="full-width" label="Войти" size="md" type="submit"/>
        </div>
      </q-form>
    </div>
    <q-dialog v-model="alert">
      <q-card>
        <q-card-section>
          <div class="text-h6">Не удалось авторизоваться</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          Неправильные имя или пароль.
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="OK" color="primary" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useAuthenticateStore} from "stores/authenticate";
import AuthService from "src/services/AuthService";
import { useRouter } from "vue-router";

defineOptions({
  name: 'LoginPage'
})

const authenticateStore = useAuthenticateStore();
const router = useRouter();

const username = ref('');
const password = ref('');
const isPwd = ref(true);
const alert = ref(false);

onMounted(() => {
  if (authenticateStore.username) {
    username.value = authenticateStore.username;
  }
})

function authenticate () {
  let result = AuthService.login({
    username: username.value,
    password: password.value
  });
  result.then((code) => {
    alert.value = code === 401;
    if (code === 200) {
      router.push('/');
    }
  });
}

</script>

<style scoped>
  .dark{
    background: #1817309a;
  }
</style>
