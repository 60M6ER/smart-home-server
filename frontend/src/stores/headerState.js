import { defineStore } from 'pinia';

export const useHeaderStateStore = defineStore('headerState', {
  state: () => ({
    title: '',
    tabs: []
  }),

  getters: {
    getTitle (state) {
      return state.title
    },
    getTabs (state) {
      return state.tabs
    }
  },

  actions: {
    setTitle (title) {
      this.title = title
    },
    setTabs (tabs) {
      this.tabs = tabs
    }
  }
})
