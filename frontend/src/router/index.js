import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
  {
    path: '/trader',
    name: 'Торговля',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/TraderView.vue'),
    children:
      [
        {
          path: 'exchanges',
          component: () => import(/* webpackChunkName: "about" */ '../views/trader/ExchangesView.vue')
        },
        {
          path: 'bots',
          component: () => import(/* webpackChunkName: "about" */ '../views/trader/BotsView.vue')
        }
      ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
