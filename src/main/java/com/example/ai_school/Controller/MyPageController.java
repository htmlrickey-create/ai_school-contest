package com.example.ai_school.Controller;

// 診断履歴に関する処理を行うService
import com.example.ai_school.service.DiagnosisResultService;

// お気に入りに関する処理を行うService
import com.example.ai_school.service.FavoriteService;

// ユーザーに関する処理を行うService
import com.example.ai_school.service.UserService;

// お気に入りの学校情報をまとめて扱うDTO
import com.example.ai_school.dto.FavoriteSchoolDto;

// 診断結果を扱うEntity
import com.example.ai_school.entity.DiagnosisResult;

// お気に入り情報を扱うEntity
import com.example.ai_school.entity.Favorite;

// ユーザー情報を扱うEntity
import com.example.ai_school.entity.User;

// 診断結果をDBから取得するRepository
import com.example.ai_school.repository.DiagnosisResultRepository;

// お気に入り情報をDBから取得するRepository
import com.example.ai_school.repository.FavoriteRepository;

// 学校情報をDBから取得するRepository
import com.example.ai_school.repository.SchoolRepository;

// Controllerとして扱うためのアノテーション
import org.springframework.stereotype.Controller;

// GETリクエストを受け取るためのアノテーション
import org.springframework.web.bind.annotation.GetMapping;

// POSTリクエストを受け取るためのアノテーション
import org.springframework.web.bind.annotation.PostMapping;

// URLやフォームからパラメータを受け取るためのアノテーション
import org.springframework.web.bind.annotation.RequestParam;

// HTMLにデータを渡すためのModel
import org.springframework.ui.Model;

// 学校情報を扱うEntity
import com.example.ai_school.entity.School;

// Sessionを扱うためのクラス
import jakarta.servlet.http.HttpSession;

// List型を扱うためのクラス
import java.util.List;

// 値が存在するかどうかを扱うOptional
import java.util.Optional;


// このクラスがControllerであることをSpringに知らせる
@Controller

public class MyPageController {

    // ユーザーに関する処理を行うUserService
    private final UserService UserService;

    // 診断結果をDBから取得するRepository
    private final DiagnosisResultRepository diagnosisResultRepository;

    // お気に入り情報をDBから取得するRepository
    private final FavoriteRepository favoriteRepository;

    // 学校情報をDBから取得するRepository
    private final SchoolRepository schoolRepository;

    // 診断結果の削除などを行うService
    private final DiagnosisResultService diagnosisResultService;

    // お気に入りの削除などを行うService
    private final FavoriteService favoriteService;

    // コンストラクタ
    // 必要なServiceやRepositoryをSpringから受け取る
    public MyPageController(UserService UserService, DiagnosisResultRepository diagnosisResultRepository, FavoriteRepository favoriteRepository, SchoolRepository schoolRepository , DiagnosisResultService diagnosisResultService, FavoriteService favoriteService) {

        // UserServiceをフィールドに保存する
        this.UserService = UserService;

        // DiagnosisResultRepositoryをフィールドに保存する
        this.diagnosisResultRepository = diagnosisResultRepository;

        // FavoriteRepositoryをフィールドに保存する
        this.favoriteRepository = favoriteRepository;

        // SchoolRepositoryをフィールドに保存する
        this.schoolRepository = schoolRepository;

        // DiagnosisResultServiceをフィールドに保存する
        this.diagnosisResultService = diagnosisResultService;

        // FavoriteServiceをフィールドに保存する
        this.favoriteService = favoriteService;

    }

    // 診断履歴を削除する処理
    // /diagnosis/deleteにPOSTされたときに実行する
    @PostMapping("/diagnosis/delete")

    public String deleteDiagnosis(@RequestParam  Long id, HttpSession session){

        // Sessionからログイン中のメールアドレスを取得する
        String loginMail = (String) session.getAttribute("loginMail");

        // ログインしていない場合はログイン画面へ移動する
        if(loginMail == null){

            return "redirect:/login";

        }

        // 診断結果のIDとログイン中のメールアドレスを使って診断履歴を削除する
        diagnosisResultService.deleteDiagnosis(id, loginMail);


        // 削除後にマイページへ戻る
        return "redirect:/Mypage";
    }

    // お気に入りの学校を削除する処理
    // /mypage/favorite/deleteにPOSTされたときに実行する
    @PostMapping ("/mypage/favorite/delete")

    public String deleteFavorite(@RequestParam  Long id, HttpSession session){

        // Sessionからログイン中のメールアドレスを取得する
        String loginMail = (String) session.getAttribute("loginMail");

        // ログインしていない場合はログイン画面へ移動する
        if(loginMail == null){

            return "redirect:/login";

        }

        // お気に入りIDとログイン中のメールアドレスを使ってお気に入りを削除する
        favoriteService.deleteFavorite(id, loginMail);

        // 削除後にマイページへ戻る
        return "redirect:/Mypage";
    }

    // /MypageにGETアクセスされたときにマイページを表示する
    @GetMapping("/Mypage")

    public String getMethodName(HttpSession session, Model model) {

        // Sessionからログイン中のメールアドレスを取得
        String loginMail = (String) session.getAttribute("loginMail");

        // ログインしていない場合はログイン画面へ移動する
        if(loginMail == null){

            return "redirect:/login";

        }

        // メールアドレスを使ってDBからユーザー情報を検索する
        Optional<User> user = UserService.searchUserById(loginMail);

        // ユーザー情報が存在する場合に処理する
        if (user.isPresent()) {

            // 取得したユーザー情報をUserDataとして扱う
            User userData = user.get();

            // ログイン中のユーザーの診断履歴をDBから取得する
            List<DiagnosisResult> diagnosisResults =

                diagnosisResultRepository.findByLoginMail(loginMail);

            // ログイン中のユーザーのお気に入り情報をDBから取得する
            List<Favorite> favorites =

                favoriteRepository.findByLoginMail(loginMail);

            // お気に入り情報から学校情報も取得して、
            // FavoriteSchoolDtoのListに変換する
            List<FavoriteSchoolDto> favoriteSchoolList = favorites.stream()

                // お気に入り1件ずつに対して処理する
                .map(favorite -> {

                    // お気に入りに登録されている学校コードを使って学校情報を検索する
                    School school = schoolRepository

                        .findById(favorite.getSchoolCode())

                        // 学校が見つからなかった場合はnullにする
                        .orElse(null);

                    // お気に入り情報と学校情報をまとめてDTOにする
                    return new FavoriteSchoolDto(favorite, school);

                })

                // Streamの結果をListに変換する
                .toList();

        // お気に入り学校一覧をHTMLに渡す
        model.addAttribute("favoriteSchoolList", favoriteSchoolList);

        // 診断履歴をHTMLに渡す
        model.addAttribute("diagnosisResults", diagnosisResults);

        // ログインメールアドレスをHTMLに渡す
        model.addAttribute("loginMail", userData.getLoginMail());

        // ユーザーの都道府県をHTMLに渡す
        model.addAttribute("prefecture", userData.getPrefecture());

        // ユーザーの年齢をHTMLに渡す
        model.addAttribute("age", userData.getAge());

        // ユーザーの学年をHTMLに渡す
        model.addAttribute("grade", userData.getGrade());

        // ユーザーの名前をHTMLに渡す
        model.addAttribute("name", userData.getName());

    }

        // Mypage.htmlを表示する
        return "Mypage";

    }

}