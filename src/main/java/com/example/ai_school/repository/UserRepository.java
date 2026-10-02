package com.example.ai_school.repository;

// JpaRepositoryを使用するためのimport
import org.springframework.data.jpa.repository.JpaRepository;

// Userエンティティを使用するためのimport
import com.example.ai_school.entity.User;


// User用のRepository
// JpaRepositoryを継承することで、Userテーブルの保存・検索・削除などの機能を利用できる
// UserのID（主キー）はString型なので、2つ目の型にStringを指定する
public interface UserRepository  extends JpaRepository <User, String>{

    // DBに登録されているユーザーの件数を取得する
    long count();


}