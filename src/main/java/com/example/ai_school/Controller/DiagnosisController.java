package com.example.ai_school.Controller;

// ユーザー情報を扱うEntity
import com.example.ai_school.entity.User;

// Controllerとして扱うためのアノテーション
import org.springframework.stereotype.Controller;

// HTMLにデータを渡すためのModel
import org.springframework.ui.Model;

// GETリクエストを受け取るためのアノテーション
import org.springframework.web.bind.annotation.GetMapping;

// ユーザー情報をDBから取得するRepository
import com.example.ai_school.repository.UserRepository;

// ログイン情報などを一時的に保存するSession
import jakarta.servlet.http.HttpSession;

// このクラスがControllerであることをSpringに知らせる
@Controller

public class DiagnosisController {


    // UserRepositoryを使ってDBからユーザー情報を取得する
    private final UserRepository userRepository;

    // コンストラクタ
    // SpringからUserRepositoryを受け取る
    public DiagnosisController( UserRepository userRepository){

        // 受け取ったUserRepositoryをフィールドに保存する
        this.userRepository = userRepository;

    }

    // /diagnosisにGETアクセスされたときに診断画面を表示する
    @GetMapping("/diagnosis")

    public String DiagnosisView(HttpSession session , Model model ) {

        // Sessionからログインしているユーザーのメールアドレスを取得する
        String loginMail = (String) session.getAttribute("loginMail");

        // メールアドレスを使ってDBからユーザー情報を検索する
        User user = userRepository.findById(loginMail)

                    // ユーザーが見つからなかった場合はエラーを発生させる
                    .orElseThrow(() -> 

                            new RuntimeException("ユーザーが見つかりません"));

        // DBから取得したユーザー名をHTMLに「userName」という名前で渡す
        model.addAttribute("userName", user.getName());


        // diagnosis.htmlを表示する
        return "diagnosis";

    }


}