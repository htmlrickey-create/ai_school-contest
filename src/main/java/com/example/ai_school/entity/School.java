package com.example.ai_school.entity;

// Entityクラスを作成するためのアノテーション
import jakarta.persistence.Entity;

// 主キー（ID）を指定するアノテーション
import jakarta.persistence.Id;

// DBのテーブル名を指定するアノテーション
import jakarta.persistence.Table;

// GetterとSetterなどを自動生成するLombok
import lombok.Data;

// Getterを自動生成するLombok
import lombok.Getter;

// Setterを自動生成するLombok
import lombok.Setter;


// このクラスをDBのテーブルと対応させる
@Entity 

// Getterを自動生成する
@Getter

// Setterを自動生成する
@Setter

// DBのテーブル名をschoolsに指定する
@Table (name = "schools")

// GetterやSetterなどを自動生成する
@Data

public class School {

    // このフィールドをDBの主キーとして設定する
    @Id

    // 学校を識別するコードを保存する
    private String code;

    // 学校の種類を保存する
    private String schoolType;

    // 学校名を保存する
    private String name;

    // 学校の所在地を保存する
    private String location;

    // 学校の郵便番号を保存する
    private String postalCode;

    // 学校の画像URLを保存する
    private String imageUrl;

    // 学校の公式サイトURLを保存する
    private String websiteUrl;
    
}