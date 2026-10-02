package com.example.ai_school.entity;

// DBのカラムを指定するためのアノテーション
import jakarta.persistence.Column;

// このクラスをDBのテーブルと対応させるためのアノテーション
import jakarta.persistence.Entity;

// 主キーを指定するためのアノテーション
import jakarta.persistence.Id;

// DBのテーブル名を指定するためのアノテーション
import jakarta.persistence.Table;

// GetterやSetterなどを自動生成するLombok
import lombok.Data;


// このクラスをDBのテーブルと対応させる
@Entity

// DBのテーブル名をuser_infoに指定する
@Table(name = "user_info")

// GetterやSetterなどを自動生成する
@Data
public class User {

    // このフィールドをDBの主キーとして設定する
    @Id

    // DB上のカラム名をlogin_mailに指定する
    @Column(name = "login_mail")

    // ユーザーのログイン用メールアドレスを保存する
    private String loginMail;

    // ユーザーの名前を保存する
    private String name;

    // ユーザーのパスワードを保存する
    private String password;

    // ユーザーの年齢を保存する
    private Integer age;

    // ユーザーの学年を保存する
    private String grade;

    // ユーザーの都道府県を保存する
    private String prefecture;
}