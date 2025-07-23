# My Diary App (Spring Boot & Vue.js)

「My Diary App」をSpring BootとVue.jsを用いて実装・実行するための手順を説明するドキュメントです。

既存の[API仕様書]および[データモデル定義書]を元に、バックエンドとフロントエンドを構築します。

-----

## 🛠️ 技術スタック

このプロジェクトで使用する主な技術は以下の通りです。

#### **バックエンド**

  * **Java**: `17.0.12 LTS`
  * **Spring Boot**: `3.4.7`
  * Spring Web
  * Spring Data JPA
  * Spring Security
  * MySQL Driver
  * lombok
  * JJWT (JSON Web Token)
  * Maven

#### **フロントエンド**

  * **Vue.js**: `3.5.8`
  * **Vite**
  * Vue Router
  * Pinia
  * Axios

#### **データベース**

  * **MySQL**: `8.0.42`

-----

## ✅ 前提条件

アプリケーションを実行する前に、以下のソフトウェアがインストールされていることを確認してください。

  * Java 17 (JDK)
  * Maven
  * Node.js & npm
  * MySQL

-----

## 📂 プロジェクト構成

バックエンドとフロントエンドは、それぞれ独立したディレクトリで管理します。

```plaintext
01-springboot-vue-app/
├── backend/      # Spring Boot プロジェクト
└── frontend/     # Vue.js プロジェクト
```

-----

## 🚀 アプリケーションの実行

### ステップ1: バックエンドの起動

1.  ターミナルを開き、`backend` ディレクトリに移動します。
    ```bash
    cd backend
    ```
2.  以下のMavenコマンドを実行して、Spring Bootアプリケーションを起動します。
    ```bash
    mvn spring-boot:run
    ```
    成功すると、`http://localhost:8080` でバックエンドAPIが待機状態になります。

### ステップ2: フロントエンドの起動

1.  **別のターミナルを**開き、`frontend` ディレクトリに移動します。
    ```bash
    cd frontend
    ```
2.  必要なパッケージをインストールします。（初回のみ）
    ```bash
    npm install
    ```
3.  以下のコマンドを実行して、Vue.jsの開発サーバーを起動します。
    ```bash
    npm run dev
    ```
    成功すると、ターミナルに表示されるURL（通常は `http://localhost:5173`）にブラウザでアクセスすると、アプリケーションが表示されます。