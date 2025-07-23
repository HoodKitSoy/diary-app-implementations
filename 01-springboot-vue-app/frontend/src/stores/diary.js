import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../services/api'

export const useDiaryStore = defineStore('diary', () => {
  const diaries = ref([])
  const currentDiary = ref(null)
  const pagination = ref({})
  const loading = ref(false)

  const fetchDiaries = async (params = {}) => {
    loading.value = true
    try {
      const response = await api.get('/diaries', { params })
      diaries.value = response.data.diaries
      pagination.value = response.data.pagination
      return response.data
    } catch (error) {
      throw new Error('日記の取得に失敗しました')
    } finally {
      loading.value = false
    }
  }

  const fetchDiary = async (id) => {
    loading.value = true
    try {
      const response = await api.get(`/diaries/${id}`)
      currentDiary.value = response.data
      return response.data
    } catch (error) {
      throw new Error('日記の取得に失敗しました')
    } finally {
      loading.value = false
    }
  }

  const createDiary = async (diaryData) => {
    try {
      const response = await api.post('/diaries', diaryData)
      return response.data
    } catch (error) {
      throw new Error('日記の作成に失敗しました')
    }
  }

  const updateDiary = async (id, diaryData) => {
    try {
      const response = await api.put(`/diaries/${id}`, diaryData)
      currentDiary.value = response.data
      return response.data
    } catch (error) {
      throw new Error('日記の更新に失敗しました')
    }
  }

  const deleteDiary = async (id) => {
    try {
      await api.delete(`/diaries/${id}`)
      diaries.value = diaries.value.filter(diary => diary.diaryId !== id)
    } catch (error) {
      throw new Error('日記の削除に失敗しました')
    }
  }

  return {
    diaries,
    currentDiary,
    pagination,
    loading,
    fetchDiaries,
    fetchDiary,
    createDiary,
    updateDiary,
    deleteDiary
  }
})