<template>
  <div class="diary-form-view">
    <nav class="navbar">
      <div class="nav-brand">
        <router-link to="/dashboard">My Diary App</router-link>
      </div>
      <div class="nav-links">
        <router-link to="/dashboard" class="nav-link">ダッシュボード</router-link>
        <router-link to="/diaries" class="nav-link">日記一覧</router-link>
        <button @click="handleLogout" class="btn btn-secondary">ログアウト</button>
      </div>
    </nav>

    <div class="container">
      <div class="page-header">
        <h1>{{ isEdit ? '日記を編集' : '新しい日記を書く' }}</h1>
        <router-link to="/diaries" class="btn btn-secondary">キャンセル</router-link>
      </div>

      <form @submit.prevent="handleSubmit" class="diary-form">
        <div class="form-group">
          <label class="form-label">タイトル *</label>
          <input
            type="text"
            v-model="form.title"
            class="form-input"
            placeholder="今日のタイトル..."
            required
          />
        </div>

        <div class="form-group">
          <label class="form-label">感情</label>
          <div class="emotion-selector">
            <button
              type="button"
              v-for="emotion in emotions"
              :key="emotion.value"
              @click="selectEmotion(emotion.value)"
              :class="['emotion-button', { active: form.emotion === emotion.value }]"
            >
              {{ emotion.icon }} {{ emotion.label }}
            </button>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">タグ</label>
          <div class="tag-input-container">
            <div class="tag-input">
              <span
                v-for="(tag, index) in form.tags"
                :key="index"
                class="tag-item"
              >
                {{ tag }}
                <button type="button" @click="removeTag(index)" class="tag-remove">×</button>
              </span>
              <input
                type="text"
                v-model="newTag"
                @keydown.enter.prevent="addTag"
                @keydown.comma.prevent="addTag"
                placeholder="タグを追加..."
                class="tag-input-field"
              />
            </div>
            <small class="form-help">Enterキーまたはカンマで追加</small>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">本文</label>
          <textarea
            v-model="form.content"
            class="form-textarea"
            rows="10"
            placeholder="今日はどんな一日でしたか？"
          ></textarea>
        </div>

        <div class="form-group">
          <label class="form-label">画像</label>
          <div class="image-upload">
            <input
              type="file"
              ref="fileInput"
              @change="handleFileSelect"
              accept="image/*"
              multiple
              class="file-input"
            />
            <button type="button" @click="$refs.fileInput.click()" class="btn btn-secondary">
              画像を選択
            </button>
          </div>
          
          <div class="image-preview" v-if="form.images.length > 0">
            <div
              v-for="(image, index) in form.images"
              :key="index"
              class="image-preview-item"
            >
              <img :src="image.preview || image.url" :alt="image.filename" />
              <button type="button" @click="removeImage(index)" class="image-remove">×</button>
            </div>
          </div>
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '保存中...' : (isEdit ? '更新' : '保存') }}
          </button>
          <router-link to="/diaries" class="btn btn-secondary">キャンセル</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useDiaryStore } from '../stores/diary'

export default {
  name: 'DiaryFormView',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const userStore = useUserStore()
    const diaryStore = useDiaryStore()
    
    const form = ref({
      title: '',
      content: '',
      emotion: '',
      tags: [],
      images: []
    })
    
    const newTag = ref('')
    const error = ref('')
    const loading = ref(false)

    const isEdit = computed(() => !!route.params.id)

    const emotions = [
      { value: 'happy', icon: '😊', label: 'うれしい' },
      { value: 'sad', icon: '😢', label: 'かなしい' },
      { value: 'angry', icon: '😠', label: 'おこった' },
      { value: 'excited', icon: '😆', label: 'わくわく' },
      { value: 'tired', icon: '😴', label: 'つかれた' },
      { value: 'neutral', icon: '😐', label: '普通' }
    ]

    const handleLogout = () => {
      userStore.logout()
      router.push('/login')
    }

    const selectEmotion = (emotion) => {
      form.value.emotion = form.value.emotion === emotion ? '' : emotion
    }

    const addTag = () => {
      const tag = newTag.value.trim()
      if (tag && !form.value.tags.includes(tag)) {
        form.value.tags.push(tag)
        newTag.value = ''
      }
    }

    const removeTag = (index) => {
      form.value.tags.splice(index, 1)
    }

    const handleFileSelect = (event) => {
      const files = Array.from(event.target.files)
      
      files.forEach(file => {
        if (file.type.startsWith('image/')) {
          const reader = new FileReader()
          reader.onload = (e) => {
            form.value.images.push({
              filename: file.name,
              data: e.target.result,
              preview: e.target.result
            })
          }
          reader.readAsDataURL(file)
        }
      })
    }

    const removeImage = (index) => {
      form.value.images.splice(index, 1)
    }

    const handleSubmit = async () => {
      error.value = ''
      loading.value = true

      try {
        if (isEdit.value) {
          await diaryStore.updateDiary(route.params.id, form.value)
        } else {
          await diaryStore.createDiary(form.value)
        }
        router.push('/diaries')
      } catch (err) {
        error.value = err.message
      } finally {
        loading.value = false
      }
    }

    const loadDiaryForEdit = async () => {
      if (isEdit.value) {
        try {
          await diaryStore.fetchDiary(route.params.id)
          const diary = diaryStore.currentDiary
          if (diary) {
            form.value = {
              title: diary.title,
              content: diary.content || '',
              emotion: diary.emotion || '',
              tags: diary.tags ? [...diary.tags] : [],
              images: diary.images ? diary.images.map(img => ({
                filename: img.filename || 'image',
                url: img.url,
                imageId: img.imageId
              })) : []
            }
          }
        } catch (error) {
          console.error('Failed to load diary for edit:', error)
        }
      }
    }

    onMounted(() => {
      loadDiaryForEdit()
    })

    return {
      form,
      newTag,
      error,
      loading,
      isEdit,
      emotions,
      handleLogout,
      selectEmotion,
      addTag,
      removeTag,
      handleFileSelect,
      removeImage,
      handleSubmit
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

.diary-form {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
  resize: vertical;
}

.emotion-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.emotion-button {
  padding: 0.5rem 1rem;
  border: 2px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
}

.emotion-button:hover {
  border-color: #007bff;
}

.emotion-button.active {
  border-color: #007bff;
  background-color: #e3f2fd;
}

.tag-input-container {
  position: relative;
}

.tag-input {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-height: 42px;
  align-items: center;
}

.tag-item {
  background-color: #007bff;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.tag-remove {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-weight: bold;
  padding: 0;
  margin-left: 0.25rem;
}

.tag-input-field {
  border: none;
  outline: none;
  flex: 1;
  min-width: 100px;
  padding: 0.25rem;
}

.form-help {
  color: #666;
  font-size: 0.75rem;
  margin-top: 0.25rem;
  display: block;
}

.image-upload {
  margin-bottom: 1rem;
}

.file-input {
  display: none;
}

.image-preview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.image-preview-item {
  position: relative;
}

.image-preview-item img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.image-remove {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 50%;
  width: 25px;
  height: 25px;
  cursor: pointer;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
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

  .diary-form {
    padding: 1rem;
  }

  .emotion-selector {
    justify-content: center;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .image-preview {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }
}
</style>