package com.example.ai_school.service;

// Optionalを使用するためのimport
import java.util.Optional;

// Serviceクラスとして登録するためのアノテーション
import org.springframework.stereotype.Service;

// DiagnosisResultエンティティを使用するためのimport
import com.example.ai_school.entity.DiagnosisResult;

// DiagnosisResultRepositoryを使用するためのimport
import com.example.ai_school.repository.DiagnosisResultRepository;


// 診断結果に関する処理を担当するService
@Service
public class DiagnosisResultService {

    // DiagnosisResultRepositoryを保持する
    private final DiagnosisResultRepository repository;

    // コンストラクタ
    // SpringからDiagnosisResultRepositoryを受け取って設定する
    public DiagnosisResultService(DiagnosisResultRepository repository) {
        this.repository = repository;
    }

    // 指定されたIDの診断結果を削除する
    public void deleteDiagnosis(Long id , String loginMail) {

        // IDを使ってDBから診断結果を検索する
        // 診断結果が存在する場合はDiagnosisResultが入る
        Optional<DiagnosisResult> result = repository.findById(id);

        // 診断結果が存在するか確認する
        if(result.isPresent()){
            // Optionalから実際のDiagnosisResultを取り出す
            DiagnosisResult diagnosis = result.get();

            // 診断結果のメールアドレスとログイン中のユーザーのメールアドレスが一致するか確認する
            if(diagnosis.getLoginMail().equals(loginMail)){
                // 本人の診断結果だった場合、そのIDの診断結果をDBから削除する
                repository.deleteById(id);
            }
        }
    }
}