// axiosライブラリをインポート
import axios from 'axios'
// Piniaのユーザーストアをインポート（認証用トークンの取得・管理に使用）
import { useUserStore } from '../stores/user'

// axiosインスタンスを生成：APIサーバーのベースURL、タイムアウト、共通ヘッダーを設定
const api = axios.create({
  baseURL: 'http://localhost:8080/api', // バックエンドAPIのエンドポイント
  timeout: 10000,                        // タイムアウト時間（ミリ秒）
  headers: {
    'Content-Type': 'application/json'  // JSON形式での通信
  }
})

// リクエストインターセプター：各リクエスト送信前に認証トークンがあればAuthorizationヘッダーに追加
api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()      // ユーザーストアから現在のトークンを取得
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}` // トークンをヘッダーにセット
    }
    return config
  },
  (error) => {
    return Promise.reject(error)         // リクエストエラーをそのままreject
  }
)

// レスポンスインターセプター：401 Unauthorizedエラー時に自動ログアウトしログイン画面へリダイレクト
api.interceptors.response.use(
  (response) => {
    return response                    // 正常レスポンスはそのまま返却
  },
  (error) => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()  // 認証エラー時にユーザーストアをクリア
      userStore.logout()
      window.location.href = '/login'   // ログインページへ遷移
    }
    return Promise.reject(error)       // その他エラーはそのままreject
  }
)

export default api // apiインスタンスをデフォルトエクスポート