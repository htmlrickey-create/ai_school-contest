package com.example.ai_school.dto;

// List型を扱うためのクラス
import java.util.List;

// Getterを自動生成するLombok
import lombok.Getter;

// Setterを自動生成するLombok
import lombok.Setter;

// GetterとSetterを自動生成する
@Getter 
@Setter 

public class DiagnosisAiResult {

    // AIが作成した診断理由を保存する
    private String reason;

    // AIが選んだおすすめ学校のコード一覧を保存する
    private List<String> schoolCodes;

    
}