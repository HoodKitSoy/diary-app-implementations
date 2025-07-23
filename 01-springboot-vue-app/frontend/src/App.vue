<template>
  <!-- App.vue: アプリのルートコンポーネント。UI全体にダークテーマを適用可能 -->
  <div id="app" :class="{ 'dark-theme': isDarkTheme }">
    <router-view />
  </div>
</template>

<script>
// Vue組み込み関数とユーザーストアをインポート
import { computed } from 'vue'
import { useUserStore } from './stores/user'

export default {
  name: 'App',
  setup() {
    const userStore = useUserStore()
    
    // ストアのthemeプロパティがdarkの場合にダークテーマを適用
    const isDarkTheme = computed(() => userStore.theme === 'dark')

    return {
      isDarkTheme
    }
  }
}
</script>

<style>
/* グローバルスタイル */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #333;
}

#app {
  min-height: 100vh;
  transition: all 0.3s ease;
}

/* ライトテーマ */
#app {
  background-color: #f8f9fa;
  color: #333;
}

/* ダークテーマ */
#app.dark-theme {
  background-color: #1a1a1a;
  color: #e0e0e0;
}

.dark-theme .card {
  background-color: #2d2d2d;
  border-color: #404040;
}

.dark-theme input,
.dark-theme textarea,
.dark-theme select {
  background-color: #404040;
  color: #e0e0e0;
  border-color: #606060;
}

/* 共通コンポーネントスタイル */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.form-group {
  margin-bottom: 15px;
}

.form-label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.error-message {
  color: #dc3545;
  font-size: 12px;
  margin-top: 5px;
}

.loading {
  text-align: center;
  padding: 20px;
}

/* レスポンシブ */
@media (max-width: 768px) {
  .container {
    padding: 0 10px;
  }
  
  .card {
    padding: 15px;
  }
}
</style>