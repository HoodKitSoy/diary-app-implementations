<template>
  <div class="diary-list-view">
    <nav class="navbar">
      <div class="nav-brand">
        <router-link to="/dashboard">My Diary App</router-link>
      </div>
      <div class="nav-links">
        <router-link to="/dashboard" class="nav-link">ダッシュボード</router-link>
        <router-link to="/calendar" class="nav-link">カレンダー</router-link>
        <router-link to="/settings" class="nav-link">設定</router-link>
        <button @click="handleLogout" class="btn btn-secondary">ログアウト</button>
      </div>
    </nav>

    <div class="container">
      <div class="page-header">
        <h1>日記一覧</h1>
        <router-link to="/diaries/new" class="btn btn-primary">新しい日記を書く</router-link>
      </div>

      <div class="search-section">
        <div class="search-form">
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="キーワードで検索..."
            class="form-input"
            @input="searchDiaries"
          />
          <select v-model="selectedTag" @change="filterByTag" class="form-input">
            <option value="">すべてのタグ</option>
            <option v-for="tag in availableTags" :key="tag" :value="tag">{{ tag }}</option>
          </select>
        </div>
      </div>

      <div class="diary-grid" v-if="!diaryStore.loading && diaries.length > 0">
        <div v-for="diary in diaries" :key="diary.diaryId" class="diary-card">
          <router-link :to="`/diaries/${diary.diaryId}`" class="diary-link">
            <div class="diary-header">
              <div class="diary-emotion" v-if="diary.emotion">
                {{ getEmotionIcon(diary.emotion) }}
              </div>
              <div class="diary-date">{{ formatDate(diary.createdAt) }}</div>
            </div>
            <h3 class="diary-title">{{ diary.title }}</h3>
            <div class="diary-tags" v-if="diary.tags && diary.tags.length > 0">
              <span v-for="tag in diary.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </router-link>
        </div>
      </div>

      <div v-else-if="diaryStore.loading" class="loading">
        読み込み中...
      </div>

      <div v-else class="empty-state">
        <p>まだ日記がありません。</p>
        <router-link to="/diaries/new" class="btn btn-primary">最初の日記を書いてみましょう</router-link>
      </div>

      <!-- ページネーション -->
      <div class="pagination" v-if="pagination.totalPages > 1">
        <button
          v-for="page in pagination.totalPages"
          :key="page"
          @click="changePage(page)"
          :class="{ active: page === pagination.page }"
          class="pagination-btn"
        >
          {{ page }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useDiaryStore } from '../stores/diary'

export default {
  name: 'DiaryListView',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const diaryStore = useDiaryStore()
    
    const searchKeyword = ref('')
    const selectedTag = ref('')
    const availableTags = ref([])

    const diaries = computed(() => diaryStore.diaries)
    const pagination = computed(() => diaryStore.pagination)

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

    const loadDiaries = async (params = {}) => {
      try {
        await diaryStore.fetchDiaries({
          page: 1,
          limit: 12,
          ...params
        })
        
        // タグの一覧を更新
        const tags = new Set()
        diaries.value.forEach(diary => {
          if (diary.tags) {
            diary.tags.forEach(tag => tags.add(tag))
          }
        })
        availableTags.value = Array.from(tags)
      } catch (error) {
        console.error('Failed to load diaries:', error)
      }
    }

    const searchDiaries = () => {
      const params = {}
      if (searchKeyword.value.trim()) {
        params.q = searchKeyword.value.trim()
      }
      loadDiaries(params)
    }

    const filterByTag = () => {
      const params = {}
      if (selectedTag.value) {
        params.tag = selectedTag.value
      }
      loadDiaries(params)
    }

    const changePage = (page) => {
      const params = { page }
      if (searchKeyword.value.trim()) {
        params.q = searchKeyword.value.trim()
      }
      if (selectedTag.value) {
        params.tag = selectedTag.value
      }
      loadDiaries(params)
    }

    onMounted(() => {
      loadDiaries()
    })

    return {
      diaryStore,
      searchKeyword,
      selectedTag,
      availableTags,
      diaries,
      pagination,
      handleLogout,
      getEmotionIcon,
      formatDate,
      searchDiaries,
      filterByTag,
      changePage
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.search-section {
  margin-bottom: 2rem;
}

.search-form {
  display: flex;
  gap: 1rem;
  max-width: 500px;
}

.search-form input,
.search-form select {
  flex: 1;
}

.diary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.diary-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.diary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.diary-link {
  display: block;
  padding: 1.5rem;
  text-decoration: none;
  color: #333;
}

.diary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.diary-emotion {
  font-size: 1.5rem;
}

.diary-date {
  color: #666;
  font-size: 0.875rem;
}

.diary-title {
  margin: 0 0 1rem 0;
  color: #007bff;
  font-size: 1.125rem;
}

.diary-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  background-color: #e9ecef;
  color: #495057;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  font-size: 1.125rem;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 3rem;
}

.empty-state p {
  font-size: 1.125rem;
  color: #666;
  margin-bottom: 1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
}

.pagination-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.pagination-btn:hover {
  background-color: #f8f9fa;
}

.pagination-btn.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 1rem;
  }

  .page-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .search-form {
    flex-direction: column;
    max-width: 100%;
  }

  .diary-grid {
    grid-template-columns: 1fr;
  }
}
</style>