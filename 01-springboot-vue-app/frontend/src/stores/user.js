import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../services/api'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)
  const theme = ref(localStorage.getItem('theme') || 'light')

  const isLoggedIn = computed(() => !!token.value)

  const login = async (credentials) => {
    try {
      const response = await api.post('/auth/login', credentials)
      user.value = response.data
      token.value = response.data.token
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data))
      return response.data
    } catch (error) {
      throw new Error(error.response?.data?.error || 'ログインに失敗しました')
    }
  }

  const register = async (userData) => {
    try {
      const response = await api.post('/auth/register', userData)
      user.value = response.data
      token.value = response.data.token
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data))
      return response.data
    } catch (error) {
      throw new Error(error.response?.data?.error || '登録に失敗しました')
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const updateTheme = (newTheme) => {
    theme.value = newTheme
    localStorage.setItem('theme', newTheme)
  }

  const initializeUser = () => {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
      user.value = JSON.parse(savedUser)
    }
  }

  return {
    user,
    token,
    theme,
    isLoggedIn,
    login,
    register,
    logout,
    updateTheme,
    initializeUser
  }
})