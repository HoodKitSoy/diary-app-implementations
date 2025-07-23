// main.js: アプリケーションのエントリーポイント
import { createApp } from 'vue'           // Vueアプリ生成関数
import { createPinia } from 'pinia'         // Piniaストアライブラリ
import router from './router'              // ルーティング設定
import App from './App.vue'                // ルートコンポーネント
import './style.css'                       // グローバルスタイル

// Vueアプリケーションを生成
const app = createApp(App)

// PiniaとVue Routerをプラグインとして登録
app.use(createPinia())
app.use(router)

// アプリを#app要素にマウント
app.mount('#app')