<template>
  <div class="dashboard">
    <nav class="navbar">
      <div class="nav-brand">
        <h1>My Diary App</h1>
      </div>
      <div class="nav-links">
        <router-link to="/diaries" class="nav-link">日記一覧</router-link>
        <router-link to="/calendar" class="nav-link">カレンダー</router-link>
        <router-link to="/settings" class="nav-link">設定</router-link>
        <button @click="handleLogout" class="btn btn-secondary">ログアウト</button>
      </div>
    </nav>

    <div class="container">
      <div class="dashboard-content">
        <div class="welcome-section">
          <h2>こんにちは、{{ userStore.user?.username || 'ユーザー' }}さん</h2>
          <p>今日はどんな一日でしたか？</p>
        </div>

        <div class="quick-actions">
          <router-link to="/diaries/new" class="action-card">
            <div class="action-icon">✏️</div>
            <h3>新しい日記を書く</h3>
            <p>今日の出来事を記録しましょう</p>
          </router-link>

          <router-link to="/diaries" class="action-card">
            <div class="action-icon">📚</div>
            <h3>過去の日記を見る</h3>
            <p>これまでの記録を振り返りましょう</p>
          </router-link>

          <router-link to="/calendar" class="action-card">
            <div class="action-icon">📅</div>
            <h3>カレンダー表示</h3>
            <p>月別で日記を確認できます</p>
          </router-link>
        </div>

        <div class="recent-diaries" v-if="recentDiaries.length > 0">
          <h3>最近の日記</h3>
          <div class="diary-list">
            <div v-for="diary in recentDiaries" :key="diary.diaryId" class="diary-item">
              <router-link :to="`/diaries/${diary.diaryId}`" class="diary-link">
                <div class="diary-emotion" v-if="diary.emotion">{{ getEmotionIcon(diary.emotion) }}</div>
                <div class="diary-info">
                  <h4>{{ diary.title }}</h4>
                  <p class="diary-date">{{ formatDate(diary.createdAt) }}</p>
                </div>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useDiaryStore } from '../stores/diary'

export default {
  name: 'DashboardView',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const diaryStore = useDiaryStore()
    const recentDiaries = ref([])

    const handleLogout = () => {
      userStore.logout()
      router.push('/login')
    }

    const getEmotionIcon = (emotion) => {
      const icons = {
        happy: '😊',
        sad: '😢',
        angry: '😠',
        excited: '😆',
        neutral: '😐',
        tired: '😴'
      }
      return icons[emotion] || '😐'
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('ja-JP')
    }

    const loadRecentDiaries = async () => {
      try {
        const result = await diaryStore.fetchDiaries({ limit: 5 })
        recentDiaries.value = result.diaries || []
      } catch (error) {
        console.error('Failed to load recent diaries:', error)
      }
    }

    onMounted(() => {
      userStore.initializeUser()
      loadRecentDiaries()
    })

    return {
      userStore,
      recentDiaries,
      handleLogout,
      getEmotionIcon,
      formatDate
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
}

.nav-brand h1 {
  color: #007bff;
  margin: 0;
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

.dashboard-content {
  padding: 2rem 0;
}

.welcome-section {
  text-align: center;
  margin-bottom: 3rem;
}

.welcome-section h2 {
  color: #333;
  margin-bottom: 0.5rem;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.action-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  text-align: center;
  text-decoration: none;
  color: #333;
  transition: transform 0.3s, box-shadow 0.3s;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  text-decoration: none;
  color: #333;
}

.action-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.action-card h3 {
  margin-bottom: 0.5rem;
  color: #007bff;
}

.recent-diaries h3 {
  margin-bottom: 1rem;
  color: #333;
}

.diary-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.diary-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.diary-link {
  display: flex;
  align-items: center;
  padding: 1rem;
  text-decoration: none;
  color: #333;
  transition: background-color 0.3s;
}

.diary-link:hover {
  background-color: #f8f9fa;
  text-decoration: none;
  color: #333;
}

.diary-emotion {
  font-size: 2rem;
  margin-right: 1rem;
}

.diary-info h4 {
  margin: 0 0 0.25rem 0;
  color: #007bff;
}

.diary-date {
  margin: 0;
  color: #666;
  font-size: 0.875rem;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 1rem;
  }

  .nav-links {
    flex-wrap: wrap;
    justify-content: center;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>