<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1>新規登録</h1>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label class="form-label">ユーザー名</label>
          <input
            type="text"
            v-model="form.username"
            class="form-input"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">メールアドレス</label>
          <input
            type="email"
            v-model="form.email"
            class="form-input"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">パスワード</label>
          <input
            type="password"
            v-model="form.password"
            class="form-input"
            required
          />
        </div>
        <div v-if="error" class="error-message">{{ error }}</div>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? '登録中...' : '新規登録' }}
        </button>
      </form>
      <p class="auth-link">
        すでにアカウントをお持ちの方は
        <router-link to="/login">ログイン</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const form = ref({
      username: '',
      email: '',
      password: ''
    })
    
    const error = ref('')
    const loading = ref(false)

    const handleRegister = async () => {
      error.value = ''
      loading.value = true
      
      try {
        await userStore.register(form.value)
        router.push('/dashboard')
      } catch (err) {
        error.value = err.message
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      error,
      loading,
      handleRegister
    }
  }
}
</script>

<style scoped>
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

.auth-card h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.auth-link {
  text-align: center;
  margin-top: 20px;
}

.auth-link a {
  color: #007bff;
  text-decoration: none;
}

.auth-link a:hover {
  text-decoration: underline;
}
</style>