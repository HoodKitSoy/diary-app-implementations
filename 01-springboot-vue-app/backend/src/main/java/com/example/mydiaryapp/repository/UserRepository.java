package com.example.mydiaryapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mydiaryapp.entity.User;

/**
 * ユーザーデータに対する永続化操作を行うリポジトリインターフェースです。
 * JpaRepositoryを継承し、ユーザー検索や存在チェックメソッドを提供します。
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * メールアドレスでユーザーを取得します。
     * @param email メールアドレス
     * @return Optionalでラップされたユーザーエンティティ
     */
    Optional<User> findByEmail(String email);

    /**
     * ユーザー名でユーザーを取得します。
     * @param username ユーザー名
     * @return Optionalでラップされたユーザーエンティティ
     */
    Optional<User> findByUsername(String username);

    /**
     * 指定メールアドレスのユーザーが存在するかチェックします。
     * @param email メールアドレス
     * @return 存在する場合 true
     */
    boolean existsByEmail(String email);

    /**
     * 指定ユーザー名のユーザーが存在するかチェックします。
     * @param username ユーザー名
     * @return 存在する場合 true
     */
    boolean existsByUsername(String username);
}