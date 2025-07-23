# **API仕様書 \- My Diary App**

## **1\. 概要**

本ドキュメントは「My Diary App」のバックエンドAPIの仕様を定義するものです。

**ベースURL:** /api

認証:  
ログアウトが必要なエンドポイント以外、すべてのリクエストにはHTTPヘッダーに認証トークンを含める必要があります。  
Authorization: Bearer \<YOUR\_AUTH\_TOKEN\>

データ形式:  
リクエストボディ、レスポンスボディ共にJSON形式を基本とします。

## **2\. エンドポイント一覧**

### **2.1. ユーザー認証 (/auth)**

| Method | Endpoint | 説明 |
| :---- | :---- | :---- |
| POST | /auth/register | 新規ユーザー登録 |
| POST | /auth/login | ログイン |
| POST | /auth/logout | ログアウト |

### **2.2. 日記 (/diaries)**

| Method | Endpoint | 説明 |
| :---- | :---- | :---- |
| GET | /diaries | 日記の一覧を取得 |
| POST | /diaries | 新しい日記を作成 |
| GET | /diaries/{diaryId} | 特定の日記を取得 |
| PUT | /diaries/{diaryId} | 特定の日記を更新 |
| DELETE | /diaries/{diaryId} | 特定の日記を削除 |

### **2.3. タグ (/tags)**

| Method | Endpoint | 説明 |
| :---- | :---- | :---- |
| GET | /tags | ユーザーが使用したタグの一覧を取得 |

### **2.4. 設定 (/settings)**

| Method | Endpoint | 説明 |
| :---- | :---- | :---- |
| GET | /settings | ユーザー設定を取得 |
| PUT | /settings | ユーザー設定を更新 |

### **2.5. データエクスポート (/export)**

| Method | Endpoint | 説明 |
| :---- | :---- | :---- |
| GET | /export | 日記データをエクスポート |

## **3\. API詳細**

### **3.1. ユーザー認証**

#### **POST /auth/register**

* **説明:** 新しいユーザーアカウントを作成します。  
* **リクエストボディ:**  
  {  
    "username": "testuser",  
    "email": "test@example.com",  
    "password": "password123"  
  }

* **レスポンス (201 Created):**  
  {  
    "userId": "user-uuid-12345",  
    "username": "testuser",  
    "email": "test@example.com",  
    "token": "jwt.auth.token.string"  
  }

#### **POST /auth/login**

* **説明:** ログインし、認証トークンを取得します。  
* **リクエストボディ:**  
  {  
    "email": "test@example.com",  
    "password": "password123"  
  }

* **レスポンス (200 OK):**  
  {  
    "userId": "user-uuid-12345",  
    "username": "testuser",  
    "token": "jwt.auth.token.string"  
  }

### **3.2. 日記**

#### **GET /diaries**

* **説明:** 日記の一覧を取得します。カレンダー表示、キーワード検索、タグ検索にも使用します。  
* **クエリパラメータ:**  
  * q (string, optional): キーワード検索。タイトルと本文が対象。  
  * tag (string, optional): タグ名で絞り込み。  
  * month (string, optional, YYYY-MM形式): 指定した年月のカレンダー表示用。  
  * page (number, optional, default: 1): ページネーション。  
  * limit (number, optional, default: 10): 1ページあたりの件数。  
* **レスポンス (200 OK):**  
  {  
    "diaries": \[  
      {  
        "diaryId": "diary-uuid-abcde",  
        "title": "最高の一日",  
        "createdAt": "2023-10-26T10:00:00Z",  
        "emotion": "happy",  
        "tags": ["お出かけ", "カフェ"]  
      }  
    ],  
    "pagination": {  
      "total": 100,  
      "page": 1,  
      "limit": 10,  
      "totalPages": 10  
    }  
  }

#### **POST /diaries**

* **説明:** 新しい日記を作成します。画像はBase64エンコードされた文字列の配列として送信します。  
* **リクエストボディ:**  
  {  
    "title": "新しい日記",  
    "content": "これは日記の本文です。",  
    "emotion": "neutral",  
    "tags": ["日記", "開始"],  
    "images": [  
      {  
        "filename": "photo1.jpg",  
        "data": "data:image/jpeg;base64,/9j/4AAQSkZJRgABA..."  
      }  
    ]  
  }

* **レスポンス (201 Created):**  
  {  
    "diaryId": "diary-uuid-fghij",  
    "title": "新しい日記",  
    "content": "これは日記の本文です。",  
    "emotion": "neutral",  
    "createdAt": "2023-10-27T12:00:00Z",  
    "tags": ["日記", "開始"],  
    "images": [  
      {  
        "imageId": "image-uuid-67890",  
        "url": "https://cdn.example.com/images/photo1.jpg"  
      }  
    \]  
  }

#### **GET /diaries/{diaryId}**

* **説明:** 特定の日記の詳細を取得します。  
* **レスポンス (200 OK):**  
  {  
    "diaryId": "diary-uuid-fghij",  
    "title": "新しい日記",  
    "content": "これは日記の本文です。",  
    "emotion": "neutral",  
    "createdAt": "2023-10-27T12:00:00Z",  
    "updatedAt": "2023-10-27T12:00:00Z",  
    "tags": ["日記", "開始"],  
    "images": [  
      {  
        "imageId": "image-uuid-67890",  
        "url": "https://cdn.example.com/images/photo1.jpg"  
      }  
    ]  
  }

#### **PUT /diaries/{diaryId}**

* **説明:** 特定の日記を更新します。  
* **リクエストボディ:** POST /diaries と同様の構造。  
* **レスポンス (200 OK):** GET /diaries/{diaryId} と同様の構造。

#### **DELETE /diaries/{diaryId}**

* **説明:** 特定の日記を削除します。  
* **レスポンス (204 No Content):** ボディなし。