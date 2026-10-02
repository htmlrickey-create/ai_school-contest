package com.example.ai_school.repository;

// List型を使用するためのimport
import java.util.List;

// Spring Data JPAのRepository機能を利用するためのimport
import org.springframework.data.jpa.repository.JpaRepository;

// DiagnosisResultエンティティを使用するためのimport
import com.example.ai_school.entity.DiagnosisResult;


// DiagnosisResult用のRepository
// JpaRepositoryを継承することで、DBへの保存・検索・削除などの機能を利用できる
public interface DiagnosisResultRepository extends JpaRepository<DiagnosisResult, Long> {

    // loginMailを条件にして診断結果を検索する
    // 同じユーザーの診断結果を複数取得できる
    List<DiagnosisResult> findByLoginMail(String loginMail); 
    
}