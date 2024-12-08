import { defineStore } from 'pinia';

export const useAuthenticateStore = defineStore('authenticate', {
  state: () => ({
    jwtToken: JSON.parse(localStorage.getItem("jwt_token")) || null,
    refreshToken: JSON.parse(localStorage.getItem("refresh_token")) || null,
    username: JSON.parse(localStorage.getItem("username")) || null,
    authorities: JSON.parse(localStorage.getItem("authorities")) || null,
  }),

  getters: {
    getJwtToken (state) {
      return state.jwtToken
    }
  },

  actions: {

  }
})
