package com.example.ai_school.dto;

// List型を扱うためのクラス
import java.util.List;

// Getterを自動生成するLombok
import lombok.Getter;

// Setterを自動生成するLombok
import lombok.Setter;

// Getterを自動生成する
@Getter

// Setterを自動生成する
@Setter

public class DiagnosisResultDto {

    // 興味・関心についての回答を保存する
    private List<String> interest;

    // 学校生活についての回答を保存する
    private List<String> schoolLife;

    // 将来についての回答を保存する
    private List<String> future;

    // 学習についての回答を保存する
    private List<String> learning;

    // 学校選びで重視することを保存する
    private List<String> priority;

    // 偏差値についての回答を保存する
    private String deviation;

    // 最終的な進路選択・意思決定についての回答を保存する
    private String decision;
    
}