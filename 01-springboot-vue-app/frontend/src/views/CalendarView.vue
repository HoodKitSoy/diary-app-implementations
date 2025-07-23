<template>
  <div class="calendar-view">
    <nav class="navbar">
      <div class="nav-brand">
        <router-link to="/dashboard">My Diary App</router-link>
      </div>
      <div class="nav-links">
        <router-link to="/dashboard" class="nav-link">ダッシュボード</router-link>
        <router-link to="/diaries" class="nav-link">日記一覧</router-link>
        <router-link to="/settings" class="nav-link">設定</router-link>
        <button @click="handleLogout" class="btn btn-secondary">ログアウト</button>
      </div>
    </nav>

    <div class="container">
      <div class="calendar-header">
        <h1>カレンダー表示</h1>
        <div class="calendar-controls">
          <button @click="previousMonth" class="btn btn-secondary">
            ← 前月
          </button>
          <h2>{{ formatMonth(currentDate) }}</h2>
          <button @click="nextMonth" class="btn btn-secondary">
            次月 →
          </button>
        </div>
      </div>

      <div class="calendar-container">
        <div class="calendar">
          <div class="calendar-weekdays">
            <div v-for="day in weekdays" :key="day" class="weekday">
              {{ day }}
            </div>
          </div>
          
          <div class="calendar-days">
            <div
              v-for="date in calendarDates"
              :key="date.dateString"
              :class="[
                'calendar-day',
                {
                  'other-month': !date.isCurrentMonth,
                  'has-diary': date.hasDiary,
                  'today': date.isToday
                }
              ]"
            >
              <div class="day-number">{{ date.day }}</div>
              <div v-if="date.diary" class="day-content">
                <router-link :to="`/diaries/${date.diary.diaryId}`" class="diary-link">
                  <div class="diary-emotion" v-if="date.diary.emotion">
                    {{ getEmotionIcon(date.diary.emotion) }}
                  </div>
                  <div class="diary-title">{{ truncateTitle(date.diary.title) }}</div>
                </router-link>
              </div>
              <div v-else-if="date.isCurrentMonth" class="day-content">
                <router-link
                  :to="`/diaries/new?date=${date.dateString}`"
                  class="add-diary-link"
                >
                  +
                </router-link>
              </div>
            </div>
          </div>
          </div>
      </div>

      <div class="calendar-legend">
        <h3>感情の凡例</h3>
        <div class="emotion-legend">
          <div v-for="emotion in emotions" :key="emotion.value" class="legend-item">
            <span class="legend-icon">{{ emotion.icon }}</span>
            <span class="legend-label">{{ emotion.label }}</span>
          </div>
        </div>
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
  name: 'CalendarView',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const diaryStore = useDiaryStore()
    
    const currentDate = ref(new Date())
    const monthlyDiaries = ref([])

    const weekdays = ['日', '月', '火', '水', '木', '金', '土']
    
    const emotions = [
      { value: 'happy', icon: '😊', label: 'うれしい' },
      { value: 'sad', icon: '😢', label: 'かなしい' },
      { value: 'angry', icon: '😠', label: 'おこった' },
      { value: 'excited', icon: '😆', label: 'わくわく' },
      { value: 'tired', icon: '😴', label: 'つかれた' },
      { value: 'neutral', icon: '😐', label: '普通' }
    ]

    const calendarDates = computed(() => {
      const year = currentDate.value.getFullYear()
      const month = currentDate.value.getMonth()
      
      const firstDay = new Date(year, month, 1)
      const lastDay = new Date(year, month + 1, 0)
      const startDate = new Date(firstDay)
      startDate.setDate(startDate.getDate() - firstDay.getDay())
      
      const dates = []
      const today = new Date()
      
      for (let i = 0; i < 42; i++) {
        const date = new Date(startDate)
        date.setDate(startDate.getDate() + i)
        
        const dateString = date.toISOString().split('T')[0]
        const isCurrentMonth = date.getMonth() === month
        const isToday = dateString === today.toISOString().split('T')[0]
        
        const diary = monthlyDiaries.value.find(d => {
          const diaryDate = new Date(d.createdAt).toISOString().split('T')[0]
          return diaryDate === dateString
        })
        
        dates.push({
          date,
          day: date.getDate(),
          dateString,
          isCurrentMonth,
          isToday,
          hasDiary: !!diary,
          diary
        })
      }
      
      return dates
    })

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

    const formatMonth = (date) => {
      return date.toLocaleDateString('ja-JP', {
        year: 'numeric',
        month: 'long'
      })
    }

    const truncateTitle = (title) => {
      return title.length > 10 ? title.substring(0, 10) + '...' : title
    }

    const previousMonth = () => {
      currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1)
      loadMonthlyDiaries()
    }

    const nextMonth = () => {
      currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1)
      loadMonthlyDiaries()
    }

    const loadMonthlyDiaries = async () => {
      try {
        const year = currentDate.value.getFullYear()
        const month = currentDate.value.getMonth() + 1
        const monthString = `${year}-${month.toString().padStart(2, '0')}`
        
        const result = await diaryStore.fetchDiaries({
          month: monthString,
          limit: 100
        })
        monthlyDiaries.value = result.diaries || []
      } catch (error) {
        console.error('Failed to load monthly diaries:', error)
      }
    }

    onMounted(() => {
      loadMonthlyDiaries()
    })

    return {
      currentDate,
      weekdays,
      emotions,
      calendarDates,
      handleLogout,
      getEmotionIcon,
      formatMonth,
      truncateTitle,
      previousMonth,
      nextMonth
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

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.calendar-controls {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.calendar-controls h2 {
  margin: 0;
  min-width: 200px;
  text-align: center;
  color: #333;
}

.calendar-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  overflow: hidden;
  margin-bottom: 2rem;
}

.calendar {
  width: 100%;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background-color: #007bff;
  color: white;
}

.weekday {
  padding: 1rem;
  text-align: center;
  font-weight: bold;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.calendar-day {
  min-height: 120px;
  border: 1px solid #eee;
  padding: 0.5rem;
  background: white;
  position: relative;
  display: flex;
  flex-direction: column;
}

.calendar-day.other-month {
  background-color: #f8f9fa;
  color: #999;
}

.calendar-day.today {
  background-color: #fff3cd;
  border-color: #ffc107;
}

.calendar-day.has-diary {
  background-color: #e3f2fd;
}

.day-number {
  font-weight: bold;
  margin-bottom: 0.25rem;
  align-self: flex-start;
}

.day-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.diary-link {
  text-decoration: none;
  color: #333;
  text-align: center;
  width: 100%;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.diary-link:hover {
  background-color: rgba(0,123,255,0.1);
  text-decoration: none;
  color: #333;
}

.diary-emotion {
  font-size: 1.5rem;
  margin-bottom: 0.25rem;
}

.diary-title {
  font-size: 0.75rem;
  line-height: 1.2;
  color: #007bff;
}

.add-diary-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border: 2px dashed #ccc;
  border-radius: 50%;
  text-decoration: none;
  color: #999;
  font-size: 1.25rem;
  transition: all 0.3s;
}

.add-diary-link:hover {
  border-color: #007bff;
  color: #007bff;
  text-decoration: none;
}

.calendar-legend {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.calendar-legend h3 {
  margin-bottom: 1rem;
  color: #333;
}

.emotion-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.legend-icon {
  font-size: 1.25rem;
}

.legend-label {
  font-size: 0.875rem;
  color: #666;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 1rem;
  }

  .calendar-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .calendar-controls {
    flex-direction: column;
    gap: 1rem;
  }

  .calendar-controls h2 {
    min-width: auto;
  }

  .calendar-day {
    min-height: 80px;
    padding: 0.25rem;
  }

  .diary-emotion {
    font-size: 1rem;
  }

  .diary-title {
    font-size: 0.625rem;
  }

  .emotion-legend {
    justify-content: center;
  }
}
</style>