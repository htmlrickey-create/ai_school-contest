package com.example.ai_school.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.ai_school.dto.DiagnosisResultDto;
import com.example.ai_school.entity.School;
import com.example.ai_school.entity.User;
import com.example.ai_school.repository.UserRepository;
import com.example.ai_school.service.AsyncDiagnosisService;
import com.example.ai_school.service.SchoolService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DiagnosisResultController {

    private final AsyncDiagnosisService asyncDiagnosisService;
    private final UserRepository userRepository;
    private final SchoolService schoolService;

    public DiagnosisResultController(
            AsyncDiagnosisService asyncDiagnosisService,
            UserRepository userRepository,
            SchoolService schoolService) {

        this.asyncDiagnosisService = asyncDiagnosisService;
        this.userRepository = userRepository;
        this.schoolService = schoolService;
    }

    // 診断開始API
    @PostMapping("/diagnosis")
    public ResponseEntity<?> startDiagnosis(
            @RequestBody DiagnosisResultDto request,
            HttpSession session) {

        String loginMail = (String) session.getAttribute("loginMail");

        if (loginMail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "ログインが必要です"));
        }

        User user = userRepository.findById(loginMail).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "ユーザーが見つかりません"));
        }

        List<School> schools = schoolService.getSchool(
                user.getPrefecture()
        );

        String diagnosisId = asyncDiagnosisService.startDiagnosis(
                loginMail,
                request,
                user.getAge(),
                user.getPrefecture(),
                schools
        );

        // Geminiの完了を待たずにIDを返す
        return ResponseEntity.accepted()
                .body(Map.of(
                        "diagnosisId", diagnosisId,
                        "status", "PROCESSING"
                ));
    }

    // 診断状態確認API
    @GetMapping("/diagnosis/status/{diagnosisId}")
    public ResponseEntity<?> getStatus(
            @PathVariable String diagnosisId,
            HttpSession session) {

        String loginMail = (String) session.getAttribute("loginMail");

        if (loginMail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AsyncDiagnosisService.DiagnosisJob job =
                asyncDiagnosisService.getJob(diagnosisId, loginMail);

        if (job == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "status", job.getStatus()
        ));
    }

    // 診断結果HTMLを表示
    @GetMapping("/diagnosis/result/{diagnosisId}")
    public String showResult(
            @PathVariable String diagnosisId,
            HttpSession session,
            Model model) {

        String loginMail = (String) session.getAttribute("loginMail");

        if (loginMail == null) {
            return "redirect:/login";
        }

        AsyncDiagnosisService.DiagnosisJob job =
                asyncDiagnosisService.getJob(diagnosisId, loginMail);

        if (job == null || !"COMPLETED".equals(job.getStatus())) {
            return "redirect:/";
        }

        User user = userRepository.findById(loginMail).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        List<School> recommendedSchools =
                schoolService.getSchoolsByCodes(job.getSchoolCodes());

        model.addAttribute("userName", user.getName());
        model.addAttribute("reason", job.getReason());
        model.addAttribute("schools", recommendedSchools);

        return "diagnosis-result";
    }
}
