package com.example.ai_school.Controller;

// 現在の日時を取得するためのクラス
import java.time.LocalDateTime;

// List型を扱うためのクラス
import java.util.List;

// Controllerとして扱うためのアノテーション
import org.springframework.stereotype.Controller;

// HTMLにデータを渡すためのModel
import org.springframework.ui.Model;

// POSTリクエストを受け取るためのアノテーション
import org.springframework.web.bind.annotation.PostMapping;

// HTTPリクエストのBodyをJavaオブジェクトとして受け取るためのアノテーション
import org.springframework.web.bind.annotation.RequestBody;

// Geminiから返されたAI診断結果を扱うDTO
import com.example.ai_school.dto.DiagnosisAiResult;

// 診断時の回答データを扱うDTO
import com.example.ai_school.dto.DiagnosisResultDto;

// 診断結果をDBに保存するEntity
import com.example.ai_school.entity.DiagnosisResult;

// 学校情報を扱うEntity
import com.example.ai_school.entity.School;

// ユーザー情報を扱うEntity
import com.example.ai_school.entity.User;

// 診断結果をDBに保存するRepository
import com.example.ai_school.repository.DiagnosisResultRepository;

// ユーザー情報をDBから取得するRepository
import com.example.ai_school.repository.UserRepository;

// Gemini AIを利用するService
import com.example.ai_school.service.GeminiService;

// 学校情報を取得するService
import com.example.ai_school.service.SchoolService;

// JSONをJavaオブジェクトに変換するためのクラス
import com.fasterxml.jackson.databind.ObjectMapper;

// ログイン情報などを一時的に保存するSession
import jakarta.servlet.http.HttpSession;

// このクラスがControllerであることをSpringに知らせる
@Controller

public class DiagnosisResultController {

    // GeminiServiceを使ってAI診断を行う
    private final GeminiService geminiService;

    // UserRepositoryを使ってユーザー情報を取得する
    private final UserRepository userRepository;

    // SchoolServiceを使って学校情報を取得する
    private final SchoolService schoolService;

    // DiagnosisResultRepositoryを使って診断結果をDBに保存する
    private final DiagnosisResultRepository diagnosisResultRepository;

    // コンストラクタ
    // Springから必要なServiceやRepositoryを受け取る
    public DiagnosisResultController(

            GeminiService geminiService,

            UserRepository userRepository,

            SchoolService schoolService,

            DiagnosisResultRepository diagnosisResultRepository) {

        // 受け取ったGeminiServiceをフィールドに保存する
        this.geminiService = geminiService;

        // 受け取ったUserRepositoryをフィールドに保存する
        this.userRepository = userRepository;

        // 受け取ったSchoolServiceをフィールドに保存する
        this.schoolService = schoolService;

        // 受け取ったDiagnosisResultRepositoryをフィールドに保存する
        this.diagnosisResultRepository = diagnosisResultRepository;

    }

    // /diagnosisにPOSTされたときにAI診断結果を作成する
    @PostMapping("/diagnosis")

    public String diagnosisResult(

            // リクエストBodyに送られてきた診断回答をDiagnosisResultDtoとして受け取る
            @RequestBody DiagnosisResultDto request,

            // 診断結果をHTMLに渡すためのModel
            Model model,

            // ログインユーザーの情報を取得するためのSession
            HttpSession session) throws Exception {

        // Sessionからログインしているユーザーのメールアドレスを取得する
        String loginMail =

                (String) session.getAttribute("loginMail");

        // メールアドレスを使ってDBからユーザー情報を取得する
        User user = userRepository.findById(loginMail)

                // ユーザーが見つからなかった場合はエラーを発生させる
                .orElseThrow(() ->

                        new RuntimeException("ユーザーが見つかりません"));

        // ユーザー名をHTMLに渡す
        model.addAttribute("userName" , user.getName());

        // ユーザーの都道府県を使って学校一覧を取得する
        List<School> schools =

                schoolService.getSchool(

                        user.getPrefecture()

                );


        // 診断の回答・ユーザーの年齢・都道府県・学校一覧を
        // GeminiServiceに渡してAI診断を実行する
        String result =

                geminiService.diagnosis(

                        request,

                        user.getAge(),

                        user.getPrefecture(),

                        schools

                );

        // Geminiから返ってきた結果をコンソールに表示する
        System.out.println("Geminiの結果:");

        // Geminiから返ってきた実際の結果を表示する
        System.out.println(result);


        // JSONをJavaオブジェクトに変換するためのObjectMapperを作成する
        ObjectMapper objectMapper = new ObjectMapper(); 

        // Geminiから返されたJSONの前後にある```jsonなどを取り除く
        String json = result

        .replace("```json", "")

        .replace("```", "")

        .trim();


        // JSON形式のAI結果をDiagnosisAiResult型に変換する
        DiagnosisAiResult aiResult =

                objectMapper.readValue(

                        json,

                        DiagnosisAiResult.class

                );

        // DBに保存するためのDiagnosisResultオブジェクトを作成する
        DiagnosisResult diagnosisResult = new DiagnosisResult();

        // 診断結果にログインユーザーのメールアドレスを設定する
        diagnosisResult.setLoginMail(loginMail);

        // AIが判断した診断理由を設定する
        diagnosisResult.setReason(aiResult.getReason());

        // AIが選んだ学校コードのListをカンマ区切りのStringに変換して設定する
        diagnosisResult.setSchoolCodes(

                        String.join(",", aiResult.getSchoolCodes())

        );

        // 診断結果を作成した日時を設定する
        diagnosisResult.setCreatedAt(LocalDateTime.now());

        // 診断結果をDBに保存する
        diagnosisResultRepository.save(diagnosisResult);



        // AIが選んだ学校コードを使っておすすめ学校の情報を取得する
        List<School> recommendedSchools =

                schoolService.getSchoolsByCodes(

                        aiResult.getSchoolCodes()

                );

        // AIが作成した診断理由をHTMLに渡す
        model.addAttribute(

                "reason",

                aiResult.getReason()

        );

        // AIが選んだおすすめ学校一覧をHTMLに渡す
        model.addAttribute(

                "schools",

                recommendedSchools

        );

        // diagnosis-result.htmlを表示する
        return "diagnosis-result";

    }

}