import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import DiaryListView from '../views/DiaryListView.vue'
import DiaryDetailView from '../views/DiaryDetailView.vue'
import DiaryFormView from '../views/DiaryFormView.vue'
import CalendarView from '../views/CalendarView.vue'
import SettingsView from '../views/SettingsView.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView,
    meta: { requiresGuest: true }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }
  },
  {
    path: '/diaries',
    name: 'DiaryList',
    component: DiaryListView,
    meta: { requiresAuth: true }
  },
  {
    path: '/diaries/new',
    name: 'DiaryNew',
    component: DiaryFormView,
    meta: { requiresAuth: true }
  },
  {
    path: '/diaries/:id',
    name: 'DiaryDetail',
    component: DiaryDetailView,
    meta: { requiresAuth: true }
  },
  {
    path: '/diaries/:id/edit',
    name: 'DiaryEdit',
    component: DiaryFormView,
    meta: { requiresAuth: true }
  },
  {
    path: '/calendar',
    name: 'Calendar',
    component: CalendarView,
    meta: { requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: SettingsView,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresGuest && userStore.isLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router