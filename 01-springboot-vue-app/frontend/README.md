# Vue.js実装の詳細解説

Vue.jsを使った「My Diary App」のフロントエンド実装について、初心者でもわかるように詳しく説明します。

## 1. プロジェクト構造の理解

Vue.jsアプリケーションは「コンポーネントベース」のアーキテクチャを使っています。

```
src/
├── main.js                    # アプリケーションのエントリーポイント
├── App.vue                    # ルートコンポーネント
├── style.css                  # グローバルスタイル
├── router/                    # ルーティング設定
│   └── index.js              # ページ遷移の設定
├── stores/                    # 状態管理（Pinia）
│   ├── user.js               # ユーザー関連の状態
│   └── diary.js              # 日記関連の状態
├── services/                  # API通信
│   └── api.js                # HTTP通信の設定
└── views/                     # ページコンポーネント
    ├── LoginView.vue          # ログインページ
    ├── RegisterView.vue       # 新規登録ページ
    ├── DashboardView.vue      # ダッシュボード
    ├── DiaryListView.vue      # 日記一覧ページ
    ├── DiaryDetailView.vue    # 日記詳細ページ
    ├── DiaryFormView.vue      # 日記作成・編集ページ
    ├── CalendarView.vue       # カレンダーページ
    └── SettingsView.vue       # 設定ページ
```

## 2. 各層の役割と実装

### 2.1 エントリーポイント（main.js）

**役割**: アプリケーションの初期設定と起動

```javascript
import { createApp } from 'vue'        // Vueアプリケーションを作成する関数
import { createPinia } from 'pinia'    // 状態管理ライブラリ
import router from './router'          // ルーティング設定
import App from './App.vue'            // ルートコンポーネント
import './style.css'                   // グローバルスタイル

const app = createApp(App)  // Vueアプリケーションのインスタンスを作成

app.use(createPinia())  // Piniaを使用可能にする
app.use(router)         // Vue Routerを使用可能にする

app.mount('#app')       // index.htmlの#appにアプリケーションをマウント
```

**ポイント**:
- `createApp()`: Vue 3の新しいアプリケーション作成方法
- `use()`: プラグインやライブラリを登録
- `mount()`: DOMにアプリケーションを接続

### 2.2 ルーティング設定（router/index.js）

**役割**: URLとページコンポーネントの対応関係を定義

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

// ページコンポーネントのインポート
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
// ... 他のページ

const routes = [
  {
    path: '/',           // URL
    redirect: '/dashboard'  // リダイレクト先
  },
  {
    path: '/login',
    name: 'Login',       // ルート名
    component: LoginView, // 表示するコンポーネント
    meta: { requiresGuest: true }  // ログインしていない人のみアクセス可能
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }   // ログインが必要
  },
  // ... 他のルート
]

const router = createRouter({
  history: createWebHistory(),  // HTML5 History APIを使用
  routes
})

// ページ遷移前に実行される処理（ナビゲーションガード）
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 認証が必要なページへのアクセス
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')  // ログインページにリダイレクト
  }
  // ゲスト専用ページへのアクセス（すでにログイン済み）
  else if (to.meta.requiresGuest && userStore.isLoggedIn) {
    next('/dashboard')  // ダッシュボードにリダイレクト
  }
  else {
    next()  // そのまま遷移
  }
})
```

**ポイント**:
- ルートは配列で定義し、pathとcomponentを対応付け
- `meta`でルートのメタ情報を設定
- `beforeEach`で認証チェックを実装

### 2.3 状態管理（Pinia Store）

**役割**: アプリケーション全体で共有する状態（データ）を管理

#### user.js の詳細解説
```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../services/api'

export const useUserStore = defineStore('user', () => {
  // 状態（state）の定義
  const user = ref(null)                              // ユーザー情報
  const token = ref(localStorage.getItem('token') || null)  // 認証トークン
  const theme = ref(localStorage.getItem('theme') || 'light')  // テーマ設定

  // 算出プロパティ（computed）の定義
  const isLoggedIn = computed(() => !!token.value)   // ログイン状態

  // アクション（actions）の定義
  const login = async (credentials) => {
    try {
      // APIにログインリクエストを送信
      const response = await api.post('/auth/login', credentials)
      
      // 成功時の処理
      user.value = response.data          // ユーザー情報を保存
      token.value = response.data.token   // トークンを保存
      
      // ローカルストレージに永続化
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data))
      
      return response.data
    } catch (error) {
      // エラー処理
      throw new Error(error.response?.data?.error || 'ログインに失敗しました')
    }
  }

  const logout = () => {
    // 状態をクリア
    user.value = null
    token.value = null
    
    // ローカルストレージからも削除
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 外部から使用可能な値・関数を返す
  return {
    user,
    token,
    theme,
    isLoggedIn,
    login,
    logout,
    // ... 他の関数
  }
})
```

**ポイント**:
- `ref()`: 変更可能な値（リアクティブ）
- `computed()`: 他の値に依存する算出値
- ローカルストレージで永続化
- 非同期処理は`async/await`で実装

### 2.4 API通信サービス（services/api.js）

**役割**: バックエンドとのHTTP通信を管理

```javascript
import axios from 'axios'
import { useUserStore } from '../stores/user'

// Axiosインスタンスを作成（基本設定付き）
const api = axios.create({
  baseURL: 'http://localhost:8080/api',  // バックエンドのベースURL
  timeout: 10000,                        // 10秒でタイムアウト
  headers: {
    'Content-Type': 'application/json'   // JSON形式で通信
  }
})

// リクエストインターセプター（送信前の処理）
api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    // 認証トークンがあれば、ヘッダーに追加
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// レスポンスインターセプター（受信後の処理）
api.interceptors.response.use(
  (response) => {
    return response  // 成功時はそのまま返す
  },
  (error) => {
    // 401エラー（認証失敗）の場合
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()           // ログアウト処理
      window.location.href = '/login'  // ログインページに遷移
    }
    return Promise.reject(error)
  }
)

export default api
```

**ポイント**:
- `axios.create()`: 共通設定を持つHTTPクライアントを作成
- インターセプターで認証トークンの自動付与
- エラー時の自動ログアウト処理

### 2.5 Vueコンポーネントの構造

Vue.jsコンポーネントは3つの部分から構成されます：

#### LoginView.vue の詳細解説
```vue
<!-- 1. template: HTMLテンプレート -->
<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1>ログイン</h1>
      <!-- v-model: 双方向データバインディング -->
      <!-- @submit.prevent: フォーム送信時にhandleLoginを実行 -->
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label">メールアドレス</label>
          <input
            type="email"
            v-model="form.email"      <!-- form.emailと双方向バインド -->
            class="form-input"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">パスワード</label>
          <input
            type="password"
            v-model="form.password"   <!-- form.passwordと双方向バインド -->
            class="form-input"
            required
          />
        </div>
        <!-- v-if: 条件付きレンダリング -->
        <div v-if="error" class="error-message">{{ error }}</div>
        <!-- :disabled: 動的な属性バインディング -->
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'ログイン中...' : 'ログイン' }}
        </button>
      </form>
      <p class="auth-link">
        アカウントをお持ちでない方は
        <!-- router-link: SPA内のページ遷移 -->
        <router-link to="/register">新規登録</router-link>
      </p>
    </div>
  </div>
</template>

<!-- 2. script: JavaScriptロジック -->
<script>
// Composition APIのインポート
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

export default {
  name: 'LoginView',
  setup() {  // Composition APIのエントリーポイント
    // Vue Router（ページ遷移）を使用
    const router = useRouter()
    // Piniaストア（状態管理）を使用
    const userStore = useUserStore()
    
    // リアクティブな値を定義
    const form = ref({
      email: '',
      password: ''
    })
    const error = ref('')      // エラーメッセージ
    const loading = ref(false) // ローディング状態

    // フォーム送信処理
    const handleLogin = async () => {
      error.value = ''     // エラーメッセージをクリア
      loading.value = true // ローディング開始
      
      try {
        // ユーザーストアのloginアクションを実行
        await userStore.login(form.value)
        // 成功時はダッシュボードに遷移
        router.push('/dashboard')
      } catch (err) {
        // エラー時はメッセージを表示
        error.value = err.message
      } finally {
        loading.value = false // ローディング終了
      }
    }

    // テンプレートで使用する値・関数を返す
    return {
      form,
      error,
      loading,
      handleLogin
    }
  }
}
</script>

<!-- 3. style: CSSスタイル -->
<style scoped>  /* scoped: このコンポーネントのみに適用 */
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.auth-card {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

/* レスポンシブデザイン */
@media (max-width: 768px) {
  .auth-card {
    padding: 20px;
  }
}
</style>
```

## 3. Vue.jsの主要機能

### 3.1 データバインディング

```vue
<template>
  <!-- テキスト補間 -->
  <h1>{{ title }}</h1>
  
  <!-- 属性バインディング -->
  <img :src="imageUrl" :alt="imageAlt">
  
  <!-- 双方向データバインディング -->
  <input v-model="message">
  
  <!-- 条件付きレンダリング -->
  <div v-if="isVisible">表示される</div>
  <div v-else>表示されない</div>
  
  <!-- リストレンダリング -->
  <ul>
    <li v-for="item in items" :key="item.id">
      {{ item.name }}
    </li>
  </ul>
  
  <!-- イベントハンドリング -->
  <button @click="handleClick">クリック</button>
</template>
```

### 3.2 Composition API の活用

```javascript
import { ref, computed, onMounted, watch } from 'vue'

export default {
  setup() {
    // リアクティブな値
    const count = ref(0)
    const message = ref('Hello')
    
    // 算出プロパティ
    const doubleCount = computed(() => count.value * 2)
    
    // メソッド
    const increment = () => {
      count.value++
    }
    
    // ライフサイクルフック
    onMounted(() => {
      console.log('コンポーネントがマウントされました')
    })
    
    // ウォッチャー（値の変更を監視）
    watch(count, (newValue, oldValue) => {
      console.log(`カウントが ${oldValue} から ${newValue} に変更`)
    })
    
    return {
      count,
      message,
      doubleCount,
      increment
    }
  }
}
```

### 3.3 コンポーネント間の通信

#### 親から子への データ渡し（Props）
```vue
<!-- 親コンポーネント -->
<template>
  <ChildComponent :user="currentUser" :posts="userPosts" />
</template>

<!-- 子コンポーネント -->
<script>
export default {
  props: {
    user: {
      type: Object,
      required: true
    },
    posts: {
      type: Array,
      default: () => []
    }
  }
}
</script>
```

#### 子から親への イベント送信（Emit）
```vue
<!-- 子コンポーネント -->
<template>
  <button @click="sendMessage">メッセージ送信</button>
</template>

<script>
export default {
  emits: ['message-sent'],
  setup(props, { emit }) {
    const sendMessage = () => {
      emit('message-sent', 'Hello from child!')
    }
    
    return { sendMessage }
  }
}
</script>

<!-- 親コンポーネント -->
<template>
  <ChildComponent @message-sent="handleMessage" />
</template>
```

## 4. 日記アプリ特有の実装

### 4.1 日記作成フォーム（DiaryFormView.vue）

```vue
<template>
  <form @submit.prevent="handleSubmit">
    <!-- タイトル入力 -->
    <input v-model="form.title" placeholder="タイトル">
    
    <!-- 感情選択 -->
    <div class="emotion-selector">
      <button
        v-for="emotion in emotions"
        :key="emotion.value"
        @click="selectEmotion(emotion.value)"
        :class="{ active: form.emotion === emotion.value }"
      >
        {{ emotion.icon }} {{ emotion.label }}
      </button>
    </div>
    
    <!-- タグ入力 -->
    <div class="tag-input">
      <span v-for="(tag, index) in form.tags" :key="index">
        {{ tag }}
        <button @click="removeTag(index)">×</button>
      </span>
      <input
        v-model="newTag"
        @keydown.enter.prevent="addTag"
        placeholder="タグを追加..."
      >
    </div>
    
    <!-- 本文入力 -->
    <textarea v-model="form.content" rows="10"></textarea>
    
    <!-- 画像アップロード -->
    <input
      type="file"
      @change="handleFileSelect"
      multiple
      accept="image/*"
    >
    
    <button type="submit">保存</button>
  </form>
</template>

<script>
export default {
  setup() {
    const form = ref({
      title: '',
      content: '',
      emotion: '',
      tags: [],
      images: []
    })
    
    // ファイル選択処理
    const handleFileSelect = (event) => {
      const files = Array.from(event.target.files)
      
      files.forEach(file => {
        const reader = new FileReader()
        reader.onload = (e) => {
          form.value.images.push({
            filename: file.name,
            data: e.target.result  // Base64エンコードされたデータ
          })
        }
        reader.readAsDataURL(file)  // ファイルをBase64に変換
      })
    }
    
    return {
      form,
      handleFileSelect
    }
  }
}
</script>
```

### 4.2 カレンダー表示（CalendarView.vue）

```vue
<template>
  <div class="calendar">
    <!-- 月ナビゲーション -->
    <div class="calendar-header">
      <button @click="previousMonth">←</button>
      <h2>{{ formatMonth(currentDate) }}</h2>
      <button @click="nextMonth">→</button>
    </div>
    
    <!-- カレンダーグリッド -->
    <div class="calendar-grid">
      <div
        v-for="date in calendarDates"
        :key="date.dateString"
        :class="{
          'has-diary': date.hasDiary,
          'today': date.isToday
        }"
      >
        <div class="day-number">{{ date.day }}</div>
        <div v-if="date.diary">
          <router-link :to="`/diaries/${date.diary.id}`">
            {{ date.diary.title }}
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  setup() {
    const currentDate = ref(new Date())
    
    // カレンダーの日付配列を生成
    const calendarDates = computed(() => {
      const year = currentDate.value.getFullYear()
      const month = currentDate.value.getMonth()
      
      // 月の最初の日と最後の日を取得
      const firstDay = new Date(year, month, 1)
      const lastDay = new Date(year, month + 1, 0)
      
      // カレンダーグリッド用の日付配列を生成
      const dates = []
      // ... 日付計算ロジック
      
      return dates
    })
    
    return {
      currentDate,
      calendarDates
    }
  }
}
</script>
```

## 5. 開発フロー

### 5.1 コンポーネント作成の流れ

1. **テンプレート作成**
   ```vue
   <template>
     <!-- UIの構造を定義 -->
   </template>
   ```

2. **ロジック実装**
   ```vue
   <script>
   import { ref, computed } from 'vue'
   
   export default {
     setup() {
       // 状態とロジックを定義
       return {
         // 公開する値・関数
       }
     }
   }
   </script>
   ```

3. **スタイル適用**
   ```vue
   <style scoped>
   /* コンポーネント専用のCSS */
   </style>
   ```

### 5.2 デバッグとツール

- **Vue DevTools**: ブラウザ拡張機能でコンポーネントや状態を検査
- **Vite**: 高速な開発サーバーとホットリロード
- **ESLint**: コード品質チェック

## 6. ビルドとデプロイ

```bash
# 開発サーバー起動
npm run dev

# 本番用ビルド
npm run build

# ビルド結果のプレビュー
npm run preview
```

ビルド後は`dist/`フォルダにHTMLやCSS、JavaScriptファイルが生成され、静的ファイルとしてWebサーバーにデプロイできます。

## まとめ

Vue.jsアプリケーションは以下の特徴があります：

- **コンポーネントベース**: 再利用可能な部品で構築
- **リアクティブ**: データの変更が自動でUIに反映
- **SPA**: ページリロードなしでスムーズな画面遷移
- **モジュラー**: 機能ごとにファイルを分割して管理

これにより、保守性が高く、ユーザー体験の良いWebアプリケーションを効率的に開発できます。