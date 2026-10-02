package com.example.ai_school.entity;

// 診断結果を作成した日時を扱うためのクラス
import java.time.LocalDateTime;

// DBのカラムを設定するためのアノテーション
import jakarta.persistence.Column;

// Entityクラスであることを示すアノテーション
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

// DBのテーブル名をdiagnosis_resultに指定する
@Table (name = "diagnosis_result")

// Getterを自動生成する
@Getter

// Setterを自動生成する
@Setter

public class DiagnosisResult {

    // このフィールドをDBの主キーとして設定する
    @Id 

    // IDをDB側で自動的に生成する
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 診断を行ったユーザーのメールアドレスを保存する
    private String loginMail;

    // AIが作成した診断理由をTEXT型で保存する
    @Column(columnDefinition = "TEXT")
    private String reason;

    // AIが選んだ学校コードをTEXT型で保存する
    @Column(columnDefinition = "TEXT")
    private String schoolCodes;

    // 診断結果を作成した日時を保存する
    private LocalDateTime createdAt;
}