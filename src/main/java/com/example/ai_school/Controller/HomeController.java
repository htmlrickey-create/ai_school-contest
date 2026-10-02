package com.example.ai_school.Controller;

// 学校に関する処理を行うService
import com.example.ai_school.service.SchoolService;

// ユーザーに関する処理を行うService
import com.example.ai_school.service.UserService;

// 現在ログインしているユーザーの認証情報を取得するためのクラス
import org.springframework.security.core.context.SecurityContextHolder;

// Controllerとして扱うためのアノテーション
import org.springframework.stereotype.Controller;

// GETリクエストを受け取るためのアノテーション
import org.springframework.web.bind.annotation.GetMapping;

// HTMLにデータを渡すためのModel
import org.springframework.ui.Model;

// Sessionを扱うためのクラス
import jakarta.servlet.http.HttpSession;

// 学校情報を扱うEntity
import com.example.ai_school.entity.School;

// List型を扱うためのクラス
import java.util.List;

// ユーザー情報を扱うEntity
import com.example.ai_school.entity.User;

// Userが存在するかどうかを扱うOptional
import java.util.Optional;

// Listの順番をランダムに並び替えるためのクラス
import java.util.Collections;


// このクラスがControllerであることをSpringに知らせる
@Controller

public class HomeController {

    // 学校に関する処理を行うSchoolService
    private final SchoolService schoolService;

    // ユーザーに関する処理を行うUserService
    private final UserService userService;

    // コンストラクタ
    // SpringからSchoolServiceとUserServiceを受け取る
    public HomeController(

            SchoolService schoolService,

            UserService userService) {

        // 受け取ったSchoolServiceをフィールドに保存する
        this.schoolService = schoolService;

        // 受け取ったUserServiceをフィールドに保存する
        this.userService = userService;

    }

    // "/"にGETアクセスされたときにトップページを表示する
    @GetMapping("/")

    public String viewIndex(Model model) {

        // DBに登録されているユーザー数を取得する
        long userCount = userService.getUserCount();

        // ユーザー数をHTMLに「userCount」という名前で渡す
        model.addAttribute("userCount", userCount);

        // index.htmlを表示する
        return "index";

    }

    // "/home"にGETアクセスされたときにホーム画面を表示する
    @GetMapping("/home")

    public String getMethodName(HttpSession session, Model model) {

        // 現在のSecurityContextに保存されている認証情報をコンソールに表示する
        System.out.println(

            SecurityContextHolder

                .getContext()

                .getAuthentication()

        );

        // 現在ログインしているユーザーのメールアドレスを取得する
        String loginMail = SecurityContextHolder

                    // 現在のSecurityContextを取得する
                    .getContext()

                    // 現在の認証情報を取得する
                    .getAuthentication()

                    // 認証されているユーザーの名前を取得する
                    .getName();

        // メールアドレスを使ってDBからユーザー情報を検索する
        Optional<User> user = userService.searchUserById(loginMail);

        // ユーザー情報が存在する場合に処理する
        if(user.isPresent()){

            // ユーザーが登録している都道府県を取得する
            String prefecture = user.get().getPrefecture();

            // 都道府県を条件に学校一覧を取得する
            List<School> schools = schoolService.getSchool(prefecture);

            // 取得した学校一覧をランダムな順番に並び替える
            Collections.shuffle(schools);

            // ランダムに並び替えた学校一覧から最初の3件を取得する
            List<School> recommendedSchools = schools.subList(0,3);



            // 取得した都道府県をコンソールに表示する
            System.out.println("都道府県:" + prefecture);

            // 取得した学校の件数をコンソールに表示する
            System.out.println("取得件数:"+ schools.size());



           // ランダムに選ばれたおすすめ学校3件をHTMLに渡す
           model.addAttribute("schools", recommendedSchools);

           // ユーザーの都道府県をHTMLに渡す
           model.addAttribute("prefecture", prefecture);

        }

        // DBに登録されているユーザー数を取得する
        long userCount = userService.getUserCount();

        // ユーザー数をHTMLに「userCount」という名前で渡す
        model.addAttribute("userCount", userCount);


        // home.htmlを表示する
        return "home";

    }

}