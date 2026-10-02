package com.example.ai_school.Controller;

// 学校の一覧を扱うためのList
import java.util.List;

// Userが存在するかどうかを扱うOptional
import java.util.Optional;

// ログインしているユーザーの認証情報を取得するためのクラス
import org.springframework.security.core.context.SecurityContextHolder;

// Controllerとして扱うためのアノテーション
import org.springframework.stereotype.Controller;

// HTMLにデータを渡すためのModel
import org.springframework.ui.Model;

// GETリクエストを受け取るためのアノテーション
import org.springframework.web.bind.annotation.GetMapping;

// 学校情報を扱うEntity
import com.example.ai_school.entity.School;

// ユーザー情報を扱うEntity
import com.example.ai_school.entity.User;

// 学校に関する処理を行うService
import com.example.ai_school.service.SchoolService;

// ユーザーに関する処理を行うService
import com.example.ai_school.service.UserService;


// このクラスがControllerであることをSpringに知らせる
@Controller

public class AllSchoolController {

    // SchoolServiceを使うためのフィールド
    private final SchoolService schoolService;

    // UserServiceを使うためのフィールド
    private final UserService userService;

    // コンストラクタ
    // SpringがSchoolServiceとUserServiceを自動的に渡してくれる
    public AllSchoolController(

            SchoolService schoolService,

            UserService userService) {

        // 渡されたSchoolServiceをフィールドに保存
        this.schoolService = schoolService;

        // 渡されたUserServiceをフィールドに保存
        this.userService = userService;
    }

    // /allschoolにGETアクセスされたときにこのメソッドを実行する
    @GetMapping("/allschool")

    public String View(Model model) {

        // 現在ログインしているユーザーのメールアドレスを取得する
        String loginMail = SecurityContextHolder

                // 現在の認証情報を取得
                .getContext()

                // 認証されているユーザーの情報を取得
                .getAuthentication()

                // ログイン時に使用した名前（ここではメールアドレス）を取得
                .getName();

        // loginMailを使ってDBからユーザー情報を検索する
        Optional<User> user =

                userService.searchUserById(loginMail);

        // ユーザー情報が存在する場合だけ処理する
        if (user.isPresent()) {

            // DBからユーザーの都道府県を取得する
            String prefecture = user.get().getPrefecture();

            // ユーザーの都道府県を条件に学校一覧を取得する
            List<School> schools =

                    schoolService.getSchool(prefecture);

            // 取得した学校一覧をHTMLに「schools」という名前で渡す
            model.addAttribute("schools", schools);

            // 都道府県をHTMLに「prefecture」という名前で渡す
            model.addAttribute("prefecture", prefecture);
        }

        // allschool.htmlを表示する
        return "allschool";

    }

}