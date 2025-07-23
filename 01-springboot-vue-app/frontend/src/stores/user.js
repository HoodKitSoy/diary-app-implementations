// PiniaとVueのリアクティブユーティリティをインポート
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../services/api'

// ユーザーストアの定義
export const useUserStore = defineStore('user', () => {
  // ユーザー情報を保持するリアクティブ変数
  const user = ref(null)
  // 認証トークンをlocalStorageから初期取得し保持
  const token = ref(localStorage.getItem('token') || null)
  // テーマ選択（ライト/ダーク）をlocalStorageから取得
  const theme = ref(localStorage.getItem('theme') || 'light')

  // トークンが存在するかに基づくログイン判定
  const isLoggedIn = computed(() => !!token.value)

  // ログイン処理：認証API呼び出しとステート更新
  const login = async (credentials) => {
    try {
      const response = await api.post('/auth/login', credentials)
      user.value = response.data               // ユーザー情報をステートに保存
      token.value = response.data.token         // トークンをステートに保存
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data))
      return response.data
    } catch (error) {
      // エラー時にはbackendのエラーメッセージを優先表示
      throw new Error(error.response?.data?.error || 'ログインに失敗しました')
    }
  }

  // 新規登録処理：登録API呼び出しと同様にトークン・ユーザー情報を保存
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

  // ログアウト処理：ステートとlocalStorageをクリア
  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // テーマ更新：ライト/ダークテーマの切り替え処理
  const updateTheme = (newTheme) => {
    theme.value = newTheme
    localStorage.setItem('theme', newTheme)
  }

  // アプリ起動時にlocalStorageから保存ユーザー情報を再設定
  const initializeUser = () => {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
      user.value = JSON.parse(savedUser)
    }
  }

  return {
    // ステートとアクションを公開
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