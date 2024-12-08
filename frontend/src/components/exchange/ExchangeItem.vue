<template>
  <div class="q-pa-md bg-grey-9 text-white" style="max-width: 500px">
    <div class="q-gutter-md" style="max-width: 500px">

      <q-field outlined label="Имя биржи" stack-label disable>
        <template v-slot:control>
          <div class="self-center full-width no-outline" tabindex="0">{{ name }}</div>
        </template>
      </q-field>
      <q-input outlined v-model="API_KEY" label="API KEY" />
      <div v-if="secret === 'yes'">
        <q-field outlined label="Secret ключ" stack-label>
          <template v-slot:control>
            <div class="self-center full-width no-outline" tabindex="0">Секретный ключ задан</div>
            <q-btn color="primary" label="Изменить" @click="editSecret"/>
          </template>
        </q-field>
      </div>
      <div v-if="secret !== 'yes'">
        <q-input outlined v-model="secret" label="Secret ключ" />
      </div>
      <q-toggle
        v-model="active"
        :label="active ? 'Включена' : 'Выключена'"
      />
      <div>
        <q-btn color="secondary" label="Сохранить" @click="saveExchangeItem"/>
        <div class="text-h6" v-if="message !== ''">{{ message }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch} from "vue";
import { useRoute } from "vue-router";
import ExchangeService from "src/services/ExchangeService";

const route = useRoute();

defineOptions({
  name: 'ExchangeItem'
})

const vendor = ref();
const name = ref();
const id = ref();
const API_KEY = ref('');
const secret = ref('');
const active = ref(false);
const message = ref('');

onMounted(() => {
  updateThisObject(route.params.id)
})

watch(
  () => route.params.id,
  (newId) => {
    updateThisObject(newId)
  }
)

function updateThisObject(v) {
 ExchangeService.getExchange(v)
   .then((data) => {
     console.log(data)
    vendor.value = data.vendor;
    name.value = data.name;
    id.value = data.id;
    API_KEY.value = data.apiKey;
    secret.value = data.secret;
    active.value = data.active === true;
  })
}

function saveExchangeItem () {
  console.log(API_KEY.value)
  ExchangeService.saveExchange({
    id: id.value,
    vendor: vendor.value,
    apiKey: API_KEY.value,
    secret: secret.value,
    active: active.value
  }).then((status) => {
    if (status === 200) {
      message.value = 'Данные успешно сохранены.'
    } else {
      message.value = 'Не удалось сохранить данные биржи.'
    }
  });

}

function editSecret () {
  secret.value = '';
}

</script>
