<template>
<!-- DiaryDetailView: 日記詳細ページコンポーネント -->
  <div class="diary-detail-view">
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
      <div v-if="diaryStore.loading" class="loading">
        読み込み中...
      </div>

      <div v-else-if="diary" class="diary-detail">
        <div class="diary-header">
          <div class="diary-actions">
            <router-link to="/diaries" class="btn btn-secondary">← 一覧に戻る</router-link>
            <div class="action-buttons">
              <router-link :to="`/diaries/${diary.diaryId}/edit`" class="btn btn-primary">編集</router-link>
              <button @click="confirmDelete" class="btn btn-danger">削除</button>
            </div>
          </div>
        </div>

        <div class="diary-content">
          <div class="diary-meta">
            <div class="diary-emotion" v-if="diary.emotion">
              {{ getEmotionIcon(diary.emotion) }}
            </div>
            <div class="diary-date">{{ formatDate(diary.createdAt) }}</div>
          </div>

          <h1 class="diary-title">{{ diary.title }}</h1>

          <div class="diary-tags" v-if="diary.tags && diary.tags.length > 0">
            <span v-for="tag in diary.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>

          <div class="diary-body">
            <p v-for="(paragraph, index) in formattedContent" :key="index">
              {{ paragraph }}
            </p>
          </div>

          <div class="diary-images" v-if="diary.images && diary.images.length > 0">
            <h3>添付画像</h3>
            <div class="image-gallery">
              <img
                v-for="image in diary.images"
                :key="image.imageId"
                :src="image.url"
                :alt="image.filename"
                class="diary-image"
                @click="openImageModal(image)"
              />
            </div>
          </div>

          <div class="diary-timestamps">
            <p class="timestamp">作成日時: {{ formatDateTime(diary.createdAt) }}</p>
            <p class="timestamp" v-if="diary.updatedAt !== diary.createdAt">
              更新日時: {{ formatDateTime(diary.updatedAt) }}
            </p>
          </div>
        </div>
      </div>

      <div v-else class="error-state">
        <p>日記が見つかりません。</p>
        <router-link to="/diaries" class="btn btn-primary">日記一覧に戻る</router-link>
      </div>
    </div>

    <!-- 削除確認モーダル -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="closeDeleteModal">
      <div class="modal" @click.stop>
        <h3>日記を削除</h3>
        <p>この日記を削除してもよろしいですか？この操作は取り消せません。</p>
        <div class="modal-actions">
          <button @click="closeDeleteModal" class="btn btn-secondary">キャンセル</button>
          <button @click="deleteDiary" class="btn btn-danger" :disabled="deleting">
            {{ deleting ? '削除中...' : '削除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 画像モーダル -->
    <div v-if="showImageModal" class="modal-overlay" @click="closeImageModal">
      <div class="image-modal" @click.stop>
        <button @click="closeImageModal" class="modal-close">&times;</button>
        <img :src="selectedImage?.url" :alt="selectedImage?.filename" />
      </div>
    </div>
  </div>
</template>

<script>
// Vue組み込み関数とPiniaストアをインポート
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useDiaryStore } from '../stores/diary'

export default {
  name: 'DiaryDetailView',
  setup() {
    // ルーターとユーザーストア、日記ストアの初期化
    const route = useRoute()
    const router = useRouter()
    const userStore = useUserStore()
    const diaryStore = useDiaryStore()

    // 削除確認モーダル表示フラグ
    const showDeleteModal = ref(false)
    // 削除中フラグ
    const deleting = ref(false)
    // 画像モーダル表示フラグ
    const showImageModal = ref(false)
    // モーダルで表示する選択中の画像情報
    const selectedImage = ref(null)

    // 現在表示する日記データの算出プロパティ
    const diary = computed(() => diaryStore.currentDiary)

    // 日記本文を改行で分割し、段落ごとの配列に変換
    const formattedContent = computed(() => {
      if (!diary.value?.content) return []
      return diary.value.content.split('\n').filter(p => p.trim())
    })

    // ユーザーログアウト処理
    const handleLogout = () => {
      userStore.logout()
      router.push('/login')
    }

    // 感情に対応するアイコンを返すユーティリティ関数
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

    // 日付を日本語ロケールで整形
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('ja-JP', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    // 日時を日本語ロケールで整形
    const formatDateTime = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString('ja-JP')
    }

    // 削除確認モーダルを開く
    const confirmDelete = () => {
      showDeleteModal.value = true
    }

    // 削除確認モーダルを閉じる
    const closeDeleteModal = () => {
      showDeleteModal.value = false
    }

    // 日記削除処理
    const deleteDiary = async () => {
      deleting.value = true
      try {
        await diaryStore.deleteDiary(diary.value.diaryId)
        router.push('/diaries')
      } catch (error) {
        alert('削除に失敗しました: ' + error.message)
      } finally {
        deleting.value = false
        showDeleteModal.value = false
      }
    }

    // 画像モーダルを開く
    const openImageModal = (image) => {
      selectedImage.value = image
      showImageModal.value = true
    }

    // 画像モーダルを閉じる
    const closeImageModal = () => {
      showImageModal.value = false
      selectedImage.value = null
    }

    // コンポーネントマウント時にAPIから日記データを取得
    onMounted(async () => {
      const diaryId = route.params.id
      try {
        await diaryStore.fetchDiary(diaryId)
      } catch (error) {
        console.error('Failed to load diary:', error)
      }
    })

    return {
      diaryStore,
      diary,
      formattedContent,
      showDeleteModal,
      deleting,
      showImageModal,
      selectedImage,
      handleLogout,
      getEmotionIcon,
      formatDate,
      formatDateTime,
      confirmDelete,
      closeDeleteModal,
      deleteDiary,
      openImageModal,
      closeImageModal
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

.diary-detail {
  max-width: 800px;
  margin: 0 auto;
}

.diary-header {
  margin-bottom: 2rem;
}

.diary-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 1rem;
}

.diary-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.diary-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.diary-emotion {
  font-size: 2rem;
}

.diary-date {
  color: #666;
  font-size: 1.125rem;
}

.diary-title {
  color: #333;
  margin-bottom: 1rem;
  font-size: 2rem;
}

.diary-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 2rem;
}

.tag {
  background-color: #e9ecef;
  color: #495057;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.875rem;
}

.diary-body {
  margin-bottom: 2rem;
  line-height: 1.8;
}

.diary-body p {
  margin-bottom: 1rem;
  color: #333;
}

.diary-images {
  margin-bottom: 2rem;
}

.diary-images h3 {
  margin-bottom: 1rem;
  color: #333;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
}

.diary-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.diary-image:hover {
  transform: scale(1.05);
}

.diary-timestamps {
  border-top: 1px solid #eee;
  padding-top: 1rem;
}

.timestamp {
  color: #666;
  font-size: 0.875rem;
  margin: 0.25rem 0;
}

.loading {
  text-align: center;
  padding: 2rem;
  font-size: 1.125rem;
  color: #666;
}

.error-state {
  text-align: center;
  padding: 3rem;
}

.error-state p {
  font-size: 1.125rem;
  color: #666;
  margin-bottom: 1rem;
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
  max-width: 400px;
  width: 90%;
}

.modal h3 {
  margin-bottom: 1rem;
  color: #333;
}

.modal p {
  margin-bottom: 1.5rem;
  color: #666;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

.image-modal {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
}

.image-modal img {
  width: 100%;
  height: auto;
  border-radius: 8px;
}

.modal-close {
  position: absolute;
  top: -10px;
  right: -10px;
  background: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  font-size: 1.5rem;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0,0,0,0.2);
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 1rem;
  }

  .diary-actions {
    flex-direction: column;
    gap: 1rem;
  }

  .action-buttons {
    width: 100%;
    justify-content: center;
  }

  .diary-content {
    padding: 1rem;
  }

  .diary-meta {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .diary-title {
    font-size: 1.5rem;
  }

  .image-gallery {
    grid-template-columns: 1fr;
  }
}
</style>