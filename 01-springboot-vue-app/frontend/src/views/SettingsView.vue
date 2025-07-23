<template>
  <!-- SettingsView: ユーザーの各種設定（プロフィール、テーマ、リマインダー、データ管理、アカウント管理）を行うページコンポーネント -->
  <div class="settings-view">
    <nav class="navbar">
      <div class="nav-brand">
        <router-link to="/dashboard">My Diary App</router-link>
      </div>
      <div class="nav-links">
        <router-link to="/dashboard" class="nav-link">ダッシュボード</router-link>
        <router-link to="/diaries" class="nav-link">日記一覧</router-link>
        <router-link to="/calendar" class="nav-link">カレンダー</router-link>
        <button @click="handleLogout" class="btn btn-secondary">ログアウト</button>
      </div>
    </nav>

    <div class="container">
      <h1>設定</h1>

      <div class="settings-sections">
        <!-- プロフィール設定 -->
        <div class="settings-section">
          <h2>プロフィール</h2>
          <div class="setting-item">
            <label class="setting-label">ユーザー名</label>
            <p class="setting-value">{{ userStore.user?.username || 'ユーザー' }}</p>
          </div>
          <div class="setting-item">
            <label class="setting-label">メールアドレス</label>
            <p class="setting-value">{{ userStore.user?.email || 'メールアドレス' }}</p>
          </div>
        </div>

        <!-- テーマ設定 -->
        <div class="settings-section">
          <h2>表示設定</h2>
          <div class="setting-item">
            <label class="setting-label">テーマ</label>
            <div class="theme-selector">
              <button
                @click="changeTheme('light')"
                :class="['theme-button', { active: currentTheme === 'light' }]"
              >
                ☀️ ライトモード
              </button>
              <button
                @click="changeTheme('dark')"
                :class="['theme-button', { active: currentTheme === 'dark' }]"
              >
                🌙 ダークモード
              </button>
            </div>
          </div>
        </div>

        <!-- リマインダー設定 -->
        <div class="settings-section">
          <h2>リマインダー</h2>
          <div class="setting-item">
            <label class="setting-label">日記のリマインダー時刻</label>
            <div class="reminder-setting">
              <input
                type="time"
                v-model="reminderTime"
                class="form-input"
              />
              <button @click="saveReminderTime" class="btn btn-primary">
                {{ savingReminder ? '保存中...' : '保存' }}
              </button>
            </div>
            <small class="setting-help">毎日指定した時刻に日記を書くリマインダーが届きます</small>
          </div>
        </div>

        <!-- データ管理 -->
        <div class="settings-section">
          <h2>データ管理</h2>
          <div class="setting-item">
            <label class="setting-label">データエクスポート</label>
            <div class="export-actions">
              <button @click="exportData" class="btn btn-secondary" :disabled="exporting">
                {{ exporting ? 'エクスポート中...' : 'JSON形式でエクスポート' }}
              </button>
            </div>
            <small class="setting-help">すべての日記データをJSON形式でダウンロードします</small>
          </div>
        </div>

        <!-- アカウント管理 -->
        <div class="settings-section danger-section">
          <h2>アカウント管理</h2>
          <div class="setting-item">
            <label class="setting-label">パスワード変更</label>
            <button @click="showPasswordModal = true" class="btn btn-secondary">
              パスワードを変更
            </button>
          </div>
          <div class="setting-item">
            <label class="setting-label">アカウント削除</label>
            <button @click="showDeleteModal = true" class="btn btn-danger">
              アカウントを削除
            </button>
            <small class="setting-help danger-text">
              この操作は取り消せません。すべての日記データが削除されます。
            </small>
          </div>
        </div>
      </div>
    </div>

    <!-- パスワード変更モーダル -->
    <div v-if="showPasswordModal" class="modal-overlay" @click="closePasswordModal">
      <div class="modal" @click.stop>
        <h3>パスワード変更</h3>
        <form @submit.prevent="changePassword">
          <div class="form-group">
            <label class="form-label">現在のパスワード</label>
            <input
              type="password"
              v-model="passwordForm.currentPassword"
              class="form-input"
              required
            />
          </div>
          <div class="form-group">
            <label class="form-label">新しいパスワード</label>
            <input
              type="password"
              v-model="passwordForm.newPassword"
              class="form-input"
              required
            />
          </div>
          <div class="form-group">
            <label class="form-label">新しいパスワード（確認）</label>
            <input
              type="password"
              v-model="passwordForm.confirmPassword"
              class="form-input"
              required
            />
          </div>
          <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
          <div class="modal-actions">
            <button type="button" @click="closePasswordModal" class="btn btn-secondary">
              キャンセル
            </button>
            <button type="submit" class="btn btn-primary" :disabled="changingPassword">
              {{ changingPassword ? '変更中...' : '変更' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- アカウント削除モーダル -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="closeDeleteModal">
      <div class="modal" @click.stop>
        <h3>アカウント削除</h3>
        <p class="danger-text">
          本当にアカウントを削除しますか？<br>
          この操作は取り消すことができません。すべての日記データが永久に削除されます。
        </p>
        <div class="form-group">
          <label class="form-label">削除を確認するため「削除」と入力してください</label>
          <input
            type="text"
            v-model="deleteConfirmation"
            class="form-input"
            placeholder="削除"
          />
        </div>
        <div class="modal-actions">
          <button @click="closeDeleteModal" class="btn btn-secondary">キャンセル</button>
          <button
            @click="deleteAccount"
            class="btn btn-danger"
            :disabled="deleteConfirmation !== '削除' || deletingAccount"
          >
            {{ deletingAccount ? '削除中...' : 'アカウントを削除' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
// Vue Router と Pinia のユーザーストアをインポート（ログアウトやユーザー情報取得に使用）
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

export default {
  name: 'SettingsView',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const reminderTime = ref('')
    const savingReminder = ref(false)
    const exporting = ref(false)
    
    const showPasswordModal = ref(false)
    const showDeleteModal = ref(false)
    const deleteConfirmation = ref('')
    
    const passwordForm = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    const passwordError = ref('')
    const changingPassword = ref(false)
    const deletingAccount = ref(false)

    const currentTheme = computed(() => userStore.theme)

    const handleLogout = () => {
      userStore.logout()
      router.push('/login')
    }

    const changeTheme = (theme) => {
      userStore.updateTheme(theme)
    }

    const saveReminderTime = async () => {
      savingReminder.value = true
      try {
        // API呼び出しをここに実装
        console.log('Saving reminder time:', reminderTime.value)
        // await api.put('/settings', { reminderTime: reminderTime.value })
        alert('リマインダー時刻を保存しました')
      } catch (error) {
        alert('保存に失敗しました: ' + error.message)
      } finally {
        savingReminder.value = false
      }
    }

    const exportData = async () => {
      exporting.value = true
      try {
        // API呼び出しをここに実装
        console.log('Exporting data...')
        // const response = await api.get('/export')
        
        // 仮のデータでダウンロード処理をシミュレート
        const data = {
          user: userStore.user,
          diaries: [], // 実際にはAPIから取得
          exportDate: new Date().toISOString()
        }
        
        const blob = new Blob([JSON.stringify(data, null, 2)], {
          type: 'application/json'
        })
        
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `my-diary-export-${new Date().toISOString().split('T')[0]}.json`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
        
        alert('データをエクスポートしました')
      } catch (error) {
        alert('エクスポートに失敗しました: ' + error.message)
      } finally {
        exporting.value = false
      }
    }

    const closePasswordModal = () => {
      showPasswordModal.value = false
      passwordForm.value = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      passwordError.value = ''
    }

    const changePassword = async () => {
      passwordError.value = ''
      
      if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
        passwordError.value = '新しいパスワードが一致しません'
        return
      }
      
      if (passwordForm.value.newPassword.length < 8) {
        passwordError.value = 'パスワードは8文字以上で入力してください'
        return
      }
      
      changingPassword.value = true
      try {
        // API呼び出しをここに実装
        console.log('Changing password...')
        // await api.put('/auth/change-password', passwordForm.value)
        
        alert('パスワードを変更しました')
        closePasswordModal()
      } catch (error) {
        passwordError.value = error.message || 'パスワードの変更に失敗しました'
      } finally {
        changingPassword.value = false
      }
    }

    const closeDeleteModal = () => {
      showDeleteModal.value = false
      deleteConfirmation.value = ''
    }

    const deleteAccount = async () => {
      if (deleteConfirmation.value !== '削除') {
        return
      }
      
      deletingAccount.value = true
      try {
        // API呼び出しをここに実装
        console.log('Deleting account...')
        // await api.delete('/auth/account')
        
        userStore.logout()
        router.push('/login')
        alert('アカウントを削除しました')
      } catch (error) {
        alert('アカウントの削除に失敗しました: ' + error.message)
      } finally {
        deletingAccount.value = false
        closeDeleteModal()
      }
    }

    onMounted(() => {
      userStore.initializeUser()
    })

    return {
      userStore,
      currentTheme,
      reminderTime,
      savingReminder,
      exporting,
      showPasswordModal,
      showDeleteModal,
      deleteConfirmation,
      passwordForm,
      passwordError,
      changingPassword,
      deletingAccount,
      handleLogout,
      changeTheme,
      saveReminderTime,
      exportData,
      closePasswordModal,
      changePassword,
      closeDeleteModal,
      deleteAccount
    }
  }
}
</script>

<style scoped>
.navbar {
  background: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.nav-brand a {
  color: #007bff;
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-links {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.nav-link {
  color: #333;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-link:hover {
  background-color: #f8f9fa;
}

.settings-sections {
  max-width: 800px;
  margin: 0 auto;
}

.settings-section {
  background: white;
  margin-bottom: 2rem;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.settings-section h2 {
  margin-bottom: 1.5rem;
  color: #333;
  border-bottom: 2px solid #f8f9fa;
  padding-bottom: 0.5rem;
}

.danger-section {
  border-left: 4px solid #dc3545;
}

.setting-item {
  margin-bottom: 1.5rem;
}

.setting-item:last-child {
  margin-bottom: 0;
}

.setting-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.setting-value {
  color: #666;
  margin: 0;
}

.setting-help {
  display: block;
  color: #666;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.danger-text {
  color: #dc3545;
}

.theme-selector {
  display: flex;
  gap: 1rem;
}

.theme-button {
  padding: 0.75rem 1.5rem;
  border: 2px solid #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.theme-button:hover {
  border-color: #007bff;
}

.theme-button.active {
  border-color: #007bff;
  background-color: #e3f2fd;
}

.reminder-setting {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.reminder-setting input {
  max-width: 150px;
}

.export-actions {
  margin-bottom: 0.5rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
}

.modal h3 {
  margin-bottom: 1.5rem;
  color: #333;
}

.modal p {
  margin-bottom: 1.5rem;
  color: #666;
  line-height: 1.6;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 1rem;
  }

  .settings-section {
    padding: 1rem;
  }

  .theme-selector {
    flex-direction: column;
  }

  .reminder-setting {
    flex-direction: column;
    align-items: flex-start;
  }

  .modal {
    padding: 1rem;
  }

  .modal-actions {
    flex-direction: column-reverse;
  }
}
</style>