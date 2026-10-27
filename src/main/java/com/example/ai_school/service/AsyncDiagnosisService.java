package com.example.ai_school.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.example.ai_school.dto.DiagnosisAiResult;
import com.example.ai_school.dto.DiagnosisResultDto;
import com.example.ai_school.entity.DiagnosisResult;
import com.example.ai_school.entity.School;
import com.example.ai_school.repository.DiagnosisResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AsyncDiagnosisService {

    private final GeminiService geminiService;
    private final DiagnosisResultRepository diagnosisResultRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 同時実行数を制限する
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    // 診断ごとの状態を保存する
    private final ConcurrentHashMap<String, DiagnosisJob> jobs =
            new ConcurrentHashMap<>();

    public AsyncDiagnosisService(
            GeminiService geminiService,
            DiagnosisResultRepository diagnosisResultRepository) {

        this.geminiService = geminiService;
        this.diagnosisResultRepository = diagnosisResultRepository;
    }

    // 診断の状態と結果を保持するクラス
    public static class DiagnosisJob {

        private final String loginMail;
        private volatile String status = "PROCESSING";
        private volatile String reason;
        private volatile List<String> schoolCodes;

        public DiagnosisJob(String loginMail) {
            this.loginMail = loginMail;
        }

        public String getLoginMail() {
            return loginMail;
        }

        public String getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }

        public List<String> getSchoolCodes() {
            return schoolCodes;
        }
    }

    // 非同期診断を開始する
    public String startDiagnosis(
            String loginMail,
            DiagnosisResultDto request,
            Integer age,
            String prefecture,
            List<School> schools) {

        String diagnosisId = UUID.randomUUID().toString();

        DiagnosisJob job = new DiagnosisJob(loginMail);
        jobs.put(diagnosisId, job);

        CompletableFuture.runAsync(() -> {

            try {
                // Geminiに診断を依頼
                String result = geminiService.diagnosis(
                        request,
                        age,
                        prefecture,
                        schools
                );

                // JSON形式に整える
                String json = result
                        .replace("```json", "")
                        .replace("```", "")
                        .trim();

                DiagnosisAiResult aiResult = objectMapper.readValue(
                        json,
                        DiagnosisAiResult.class
                );

                // 診断結果の形式を確認
                if (aiResult.getSchoolCodes() == null
                        || aiResult.getSchoolCodes().size() != 3
                        || aiResult.getReason() == null) {

                    throw new IllegalStateException("AI診断結果が不正です");
                }

                // DBに保存
                DiagnosisResult diagnosisResult = new DiagnosisResult();

                diagnosisResult.setLoginMail(loginMail);
                diagnosisResult.setReason(aiResult.getReason());
                diagnosisResult.setSchoolCodes(
                        String.join(",", aiResult.getSchoolCodes())
                );
                diagnosisResult.setCreatedAt(LocalDateTime.now());

                diagnosisResultRepository.save(diagnosisResult);

                // 完了結果を保持
                job.reason = aiResult.getReason();
                job.schoolCodes = List.copyOf(aiResult.getSchoolCodes());
                job.status = "COMPLETED";

            } catch (Exception e) {

                job.status = "FAILED";
                System.err.println("非同期診断エラー: " + diagnosisId);
                e.printStackTrace();
            }

        }, executor);

        return diagnosisId;
    }

    // 診断IDから状態を取得
    public DiagnosisJob getJob(String diagnosisId, String loginMail) {

        DiagnosisJob job = jobs.get(diagnosisId);

        if (job == null || !job.getLoginMail().equals(loginMail)) {
            return null;
        }

        return job;
    }
}
