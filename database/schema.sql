-- My Diary App - MySQL Database Schema
-- ---
-- このファイルは、My Diary Appのデータベース構造を定義するSQLスクリプトです。
-- ---

-- ---
-- データベースの作成
-- ---
-- `my_diary_app` という名前のデータベースがまだ存在しない場合のみ作成します。
-- CHARACTER SET utf8mb4: 絵文字など4バイトの文字も保存できるように設定します。
-- COLLATE utf8mb4_unicode_ci: 文字の比較や並び替えのルールを定めます。大文字小文字を区別しません。
CREATE DATABASE IF NOT EXISTS `my_diary_app` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- これから実行するSQLが、どのデータベースに対するものかを指定します。
USE `my_diary_app`;

-- ---
-- Table `users`
-- ユーザー情報を管理するためのテーブルです。
-- ---
CREATE TABLE IF NOT EXISTS `users` (
  -- `id`: ユーザーを一位に識別するためのID。UUIDv4などの36文字の文字列を想定しています。
  `id` CHAR(36) NOT NULL,
  -- `username`: ユーザーの表示名。50文字まで。空にすることはできません (NOT NULL)。
  `username` VARCHAR(50) NOT NULL,
  -- `email`: ログインや通知に使用するメールアドレス。255文字まで。空にはできません。
  `email` VARCHAR(255) NOT NULL,
  -- `password_hash`: パスワードをハッシュ化した文字列。セキュリティのため、元のパスワードは保存しません。
  `password_hash` VARCHAR(255) NOT NULL,
  -- `theme`: アプリの表示テーマ ('light' または 'dark')。デフォルトは 'light' です。
  `theme` VARCHAR(10) NOT NULL DEFAULT 'light',
  -- `reminder_time`: 日記の書き忘れを通知する時間。設定しない場合はNULL (空) になります。
  `reminder_time` TIME NULL,
  -- `created_at`: このレコードが作成された日時。DEFAULT CURRENT_TIMESTAMPで自動的に現在日時が記録されます。
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  -- `updated_at`: このレコードが更新された日時。ON UPDATE CURRENT_TIMESTAMPで更新時に自動的に現在日時が記録されます。
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  -- `PRIMARY KEY`: このテーブルの主キーを設定します。`id`カラムがユーザーを一位に特定します。
  PRIMARY KEY (`id`),
  -- `UNIQUE KEY`: このカラムの値がテーブル内で重複しないことを保証します。同じユーザー名での登録を防ぎます。
  UNIQUE KEY `idx_users_username` (`username`),
  -- `UNIQUE KEY`: 同じメールアドレスでの登録を防ぎます。
  UNIQUE KEY `idx_users_email` (`email`)
) ENGINE=InnoDB; -- FOREIGN KEY制約やトランザクションをサポートするInnoDBエンジンを使用します。

-- ---
-- Table `tags`
-- 日記に付けるタグ情報を一意に管理します。
-- ---
CREATE TABLE IF NOT EXISTS `tags` (
  -- `id`: タグを一位に識別するためのID。AUTO_INCREMENTで新しいタグが作られるたびに自動で連番が振られます。
  `id` INT NOT NULL AUTO_INCREMENT,
  -- `name`: タグの名前 (例: "旅行", "仕事")。50文字まで。
  `name` VARCHAR(50) NOT NULL,
  -- `PRIMARY KEY`: `id`カラムをこのテーブルの主キーに設定します。
  PRIMARY KEY (`id`),
  -- `UNIQUE KEY`: 同じ名前のタグが複数作られるのを防ぎます。
  UNIQUE KEY `idx_tags_name` (`name`)
) ENGINE=InnoDB;

-- ---
-- Table `diaries`
-- 日記の基本情報を格納します。
-- ---
CREATE TABLE IF NOT EXISTS `diaries` (
  -- `id`: 日記を一位に識別するためのID。
  `id` CHAR(36) NOT NULL,
  -- `user_id`: この日記を書いたユーザーのID。`users`テーブルの`id`と関連付けられます。
  `user_id` CHAR(36) NOT NULL,
  -- `title`: 日記のタイトル。255文字まで。
  `title` VARCHAR(255) NOT NULL,
  -- `content`: 日記の本文。TEXT型で長い文章も保存できます。
  `content` TEXT,
  -- `emotion`: その日の感情を表す文字列 (例: 'happy', 'sad')。
  `emotion` VARCHAR(20),
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  -- `INDEX`: `user_id`での検索を高速化するためのインデックスです。
  INDEX `idx_diaries_user_id` (`user_id`),
  -- `CONSTRAINT`: テーブル間の関連性を定義する「制約」です。`fk_diaries_users`という名前を付けています。
  CONSTRAINT `fk_diaries_users`
    -- `FOREIGN KEY`: `user_id`を外部キーとして設定します。
    FOREIGN KEY (`user_id`)
    -- `REFERENCES`: `users`テーブルの`id`カラムを参照します。
    REFERENCES `users` (`id`)
    -- `ON DELETE CASCADE`: 参照先のユーザーが削除された場合、この日記も自動的に削除されます。
    ON DELETE CASCADE
    -- `ON UPDATE CASCADE`: 参照先のユーザーIDが更新された場合、この日記の`user_id`も自動的に更新されます。
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---
-- Table `images`
-- 日記に添付された画像を管理します。
-- ---
CREATE TABLE IF NOT EXISTS `images` (
  `id` CHAR(36) NOT NULL,
  -- `diary_id`: この画像が添付されている日記のID。`diaries`テーブルと関連付けられます。
  `diary_id` CHAR(36) NOT NULL,
  -- `url`: 画像が保存されている場所のURL。2048文字まで。
  `url` VARCHAR(2048) NOT NULL,
  -- `filename`: 元のファイル名。255文字まで。
  `filename` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_images_diary_id` (`diary_id`),
  CONSTRAINT `fk_images_diaries`
    FOREIGN KEY (`diary_id`)
    REFERENCES `diaries` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---
-- Table `diary_tags`
-- 日記とタグの多対多の関連を管理する中間テーブルです。
-- これにより、1つの日記に複数のタグを、1つのタグを複数の日記に付けることができます。
-- ---
CREATE TABLE IF NOT EXISTS `diary_tags` (
  -- `diary_id`: 関連付ける日記のID。
  `diary_id` CHAR(36) NOT NULL,
  -- `tag_id`: 関連付けるタグのID。
  `tag_id` INT NOT NULL,
  -- `PRIMARY KEY`: `diary_id`と`tag_id`の組み合わせを主キーとします。これにより、同じ日記に同じタグが複数登録されるのを防ぎます。
  PRIMARY KEY (`diary_id`, `tag_id`),
  INDEX `idx_diary_tags_diary_id` (`diary_id`),
  INDEX `idx_diary_tags_tag_id` (`tag_id`),
  CONSTRAINT `fk_diary_tags_diaries`
    FOREIGN KEY (`diary_id`)
    REFERENCES `diaries` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diary_tags_tags`
    FOREIGN KEY (`tag_id`)
    REFERENCES `tags` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
