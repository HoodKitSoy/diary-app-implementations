// PiniaとVueのリアクティブユーティリティをインポート
import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../services/api'

// ストアを定義
export const useDiaryStore = defineStore('diary', () => {
  // 日記一覧を格納するリアクティブ変数
  const diaries = ref([])
  // 現在選択中の日記データを保持する変数
  const currentDiary = ref(null)
  // ページネーション情報（ページ番号、総件数など）を保持
  const pagination = ref({})
  // API呼び出し中のローディング状態を管理
  const loading = ref(false)

  // 日記一覧を取得する非同期関数
  // params: ページ番号や検索フィルターなどのクエリパラメータ
  const fetchDiaries = async (params = {}) => {
    loading.value = true // 読み込み開始
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

  // 単一の日記をID指定で取得する非同期関数
  const fetchDiary = async (id) => {
    loading.value = true // 読み込み開始
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

  // 新規日記を作成する非同期関数
  // diaryData: タイトル・本文・感情・タグ・画像などのフォームデータ
  const createDiary = async (diaryData) => {
    try {
      const response = await api.post('/diaries', diaryData)
      return response.data
    } catch (error) {
      throw new Error('日記の作成に失敗しました')
    }
  }

  // 既存の日記を更新する非同期関数
  // id: 更新対象の日記ID, diaryData: 更新後のデータ
  const updateDiary = async (id, diaryData) => {
    try {
      const response = await api.put(`/diaries/${id}`, diaryData)
      currentDiary.value = response.data
      return response.data
    } catch (error) {
      throw new Error('日記の更新に失敗しました')
    }
  }

  // 日記を削除する非同期関数
  // id: 削除対象の日記ID
  const deleteDiary = async (id) => {
    try {
      await api.delete(`/diaries/${id}`)
      diaries.value = diaries.value.filter(diary => diary.diaryId !== id)
    } catch (error) {
      throw new Error('日記の削除に失敗しました')
    }
  }

  return {
    // 各ステートとアクションを公開
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