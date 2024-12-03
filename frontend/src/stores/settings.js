import { defineStore } from 'pinia';

export const useSettingsStore = defineStore('settings', {
  state: () => ({
    darkMode: JSON.parse(localStorage.getItem("darkMode")) || [],
  }),

  getters: {
    getDarkMode: (state) => state.darkMode === true,
  },

  actions: {
    persistToLocalStorage() {
      localStorage.setItem("darkMode", JSON.stringify(this.darkMode));
    },
    toggle () {
      this.darkMode = !this.getDarkMode;
      this.persistToLocalStorage();
    }
  }
})
