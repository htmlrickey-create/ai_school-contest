package com.example.ai_school.entity;

// Entityクラスを作成するためのアノテーション
import jakarta.persistence.Entity;

// IDを自動生成するためのアノテーション
import jakarta.persistence.GeneratedValue;

// IDの生成方法を指定するためのクラス
import jakarta.persistence.GenerationType;

// 主キー（ID）を指定するアノテーション
import jakarta.persistence.Id;

// DBのテーブル名を指定するアノテーション
import jakarta.persistence.Table;

// Getterを自動生成するLombok
import lombok.Getter;

// Setterを自動生成するLombok
import lombok.Setter;


// このクラスをDBのテーブルと対応させる
@Entity 

// DBのテーブル名をFavoritesに指定する
@Table (name = "Favorites")

// Getterを自動生成する
@Getter 

// Setterを自動生成する
@Setter 

public class Favorite {
     
    // このフィールドをDBの主キーとして設定する
    @Id 

    // IDをDB側で自動的に生成する
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // お気に入り登録したユーザーのメールアドレスを保存する
    private String loginMail;

    // お気に入り登録した学校のコードを保存する
    private String schoolCode;
}