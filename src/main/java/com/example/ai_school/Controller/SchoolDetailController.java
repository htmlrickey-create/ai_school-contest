package com.example.ai_school.Controller;

// Controllerとして扱うためのアノテーション
import org.springframework.stereotype.Controller;

// HTMLにデータを渡すためのModel
import org.springframework.ui.Model;

// GETリクエストを受け取るためのアノテーション
import org.springframework.web.bind.annotation.GetMapping;

// URLの{code}の部分を受け取るためのアノテーション
import org.springframework.web.bind.annotation.PathVariable;

// 学校情報を扱うEntity
import com.example.ai_school.entity.School;

// お気に入り情報をDBから取得するRepository
import com.example.ai_school.repository.FavoriteRepository;

// 学校に関する処理を行うService
import com.example.ai_school.service.SchoolService;

// application.propertiesなどの設定値を取得するためのアノテーション
import org.springframework.beans.factory.annotation.Value;

// ログイン情報などを一時的に保存するSession
import jakarta.servlet.http.HttpSession;

// このクラスがControllerであることをSpringに知らせる
@Controller 

public class SchoolDetailController {

    // 学校に関する処理を行うSchoolService
    private final SchoolService schoolService;

    // お気に入り情報を扱うFavoriteRepository
    private final FavoriteRepository favoriteRepository;

    // Google Maps APIのキーを設定ファイルから取得する
    @Value("${google.maps.api-key}")

    private String googleMapsApiKey;

    // コンストラクタ
    // SpringからSchoolServiceとFavoriteRepositoryを受け取る
    public SchoolDetailController (SchoolService schoolService, FavoriteRepository favoriteRepository){

        // 受け取ったSchoolServiceをフィールドに保存する
        this.schoolService = schoolService;

        // 受け取ったFavoriteRepositoryをフィールドに保存する
        this.favoriteRepository = favoriteRepository;

    }

    // /schools/{code}にGETアクセスされたときに学校詳細ページを表示する
    @GetMapping("/schools/{code}")

    public String schoolDetail(

            // URLの{code}から学校コードを取得する
            @PathVariable String code,

            // HTMLにデータを渡すためのModel
            Model model, HttpSession session) {

        // 学校コードを使って学校の詳細情報を取得する
        School school = schoolService.getSchoolDetail(code);

        // Sessionからログイン中のメールアドレスを取得する
        String loginMail = (String) session.getAttribute("loginMail");

        // 最初はお気に入り登録されていない状態にする
        boolean isFavorite = false;

        // ログインしている場合だけお気に入り登録の確認を行う
        if(loginMail != null){

            // ログイン中のユーザーがこの学校をお気に入り登録しているか確認する
            isFavorite=

                    favoriteRepository

                        .existsByLoginMailAndSchoolCode(loginMail, code);

        }

        // 学校情報をHTMLに「school」という名前で渡す
        model.addAttribute("school", school);

        // Google Maps APIキーをHTMLに渡す
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        // お気に入り登録されているかどうかをHTMLに渡す
        model.addAttribute("isFavorite", isFavorite);

        // school-detail.htmlを表示する
        return "school-detail";

    }

}