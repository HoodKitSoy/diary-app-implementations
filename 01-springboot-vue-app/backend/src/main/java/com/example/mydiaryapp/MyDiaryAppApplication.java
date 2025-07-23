package com.example.mydiaryapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * アプリケーションのエントリポイントを定義するクラスです。
 * Spring Bootの自動設定とBean登録を有効にし、
 * アプリケーションの起動を担当します。
 */
@SpringBootApplication
public class MyDiaryAppApplication {

    /**
     * Spring Bootアプリケーションを起動するメインメソッド。
     * @param args コマンドライン引数（必要に応じて起動オプションを指定可能）
     */
    public static void main(String[] args) {
        SpringApplication.run(MyDiaryAppApplication.class, args);
    }

}
