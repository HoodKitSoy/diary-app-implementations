// Vue Router と Pinia のストアをインポート
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

// 各ビューコンポーネントをインポート
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import DiaryListView from '../views/DiaryListView.vue'
import DiaryDetailView from '../views/DiaryDetailView.vue'
import DiaryFormView from '../views/DiaryFormView.vue'
import CalendarView from '../views/CalendarView.vue'
import SettingsView from '../views/SettingsView.vue'

// アプリケーションのルート定義
const routes = [
  {
    path: '/',              // ベース URL にアクセスされた場合
    redirect: '/dashboard'   // ダッシュボードページへリダイレクト
  },
  {
    path: '/login',         // ログインページのパス
    name: 'Login',          // ルート名
    component: LoginView,   // レンダリングするコンポーネント
    meta: { requiresGuest: true } // ログイン済ユーザーはアクセス禁止
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView,
    meta: { requiresGuest: true } // ログイン済ユーザーは登録ページへアクセス不可
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }  // 認証必須
  },
  {
    path: '/diaries',
    name: 'DiaryList',
    component: DiaryListView,
    meta: { requiresAuth: true }  // 認証必須で日記一覧を表示
  },
  {
    path: '/diaries/new',
    name: 'DiaryNew',
    component: DiaryFormView,
    meta: { requiresAuth: true }  // 認証必須で新規作成フォームを表示
  },
  {
    path: '/diaries/:id',   // パラメータ付きルート
    name: 'DiaryDetail',
    component: DiaryDetailView,
    meta: { requiresAuth: true }  // 認証必須で日記詳細を表示
  },
  {
    path: '/diaries/:id/edit',
    name: 'DiaryEdit',
    component: DiaryFormView,
    meta: { requiresAuth: true }  // 認証必須で編集フォームを表示
  },
  {
    path: '/calendar',
    name: 'Calendar',
    component: CalendarView,
    meta: { requiresAuth: true }  // 認証必須でカレンダー表示
  },
  {
    path: '/settings',
    name: 'Settings',
    component: SettingsView,
    meta: { requiresAuth: true }  // 認証必須でユーザー設定
  }
]

// Router インスタンスを生成
const router = createRouter({
  history: createWebHistory(), // HTML5 History モード
  routes                      // 定義したルート配列を渡す
})

// ナビゲーションガードを設定して認証情報をチェック
router.beforeEach((to, from, next) => {
  const userStore = useUserStore() // Pinia のユーザーストアを取得
  
  // 認証が必要なルートで未ログインの場合はログインページへリダイレクト
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  // 未認証ユーザー専用ルートでログイン済の場合はダッシュボードへ
  } else if (to.meta.requiresGuest && userStore.isLoggedIn) {
    next('/dashboard')
  } else {
    next() // 通常通り遷移を許可
  }
})

export default router // 外部から参照できるようエクスポート