package com.example.ai_school.Controller;

// Controllerとして扱うためのアノテーション
import org.springframework.stereotype.Controller;

// HTMLにデータを渡すためのModel
import org.springframework.ui.Model;

// GETリクエストを受け取るためのアノテーション
import org.springframework.web.bind.annotation.GetMapping;

// POSTリクエストを受け取るためのアノテーション
import org.springframework.web.bind.annotation.PostMapping;

// ユーザーの認証情報を作成するためのクラス
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

// Spring Securityの認証情報を保持するためのクラス
import org.springframework.security.core.context.SecurityContext;

// 現在ログインしているユーザーの認証情報を管理するクラス
import org.springframework.security.core.context.SecurityContextHolder;

// パスワードの暗号化・照合を行うためのクラス
import org.springframework.security.crypto.password.PasswordEncoder;

// ログインフォームの入力内容を受け取るForm
import com.example.ai_school.form.LoginForm;

// 新規登録フォームの入力内容を受け取るForm
import com.example.ai_school.form.RegisterForm;

// ユーザーに関する処理を行うService
import com.example.ai_school.service.UserService;

// ログイン情報などを一時的に保存するSession
import jakarta.servlet.http.HttpSession;

// コンストラクタを自動生成するためのLombok
import lombok.RequiredArgsConstructor;

// Spring Securityの認証情報を表すインターフェース
import org.springframework.security.core.Authentication;

// ユーザーの権限を扱うためのクラス
import org.springframework.security.core.authority.AuthorityUtils;

// このクラスがControllerであることをSpringに知らせる
@Controller

// finalのフィールドを受け取るコンストラクタをLombokが自動生成する
@RequiredArgsConstructor
public class AuthController {

    // ユーザーに関する処理を行うUserService
    private final UserService service;

    // パスワードの照合を行うPasswordEncoder
    private final PasswordEncoder passwordEncoder;


    // ログインというhtmlファイルを表示するコード
    // /loginにGETアクセスされたときに実行する
    @GetMapping("/login")
    public String view(Model model, LoginForm form) {

        // HTMLに空のログインフォームを渡す
        model.addAttribute("loginForm", new LoginForm());

        // HTMLに空の新規登録フォームを渡す
        model.addAttribute("registerForm", new RegisterForm());

        // login.htmlを表示する
        return "login";
    }


    // 利用規約ページを表示する
    // /terms/registerにGETアクセスされたときに実行する
    @GetMapping("/terms/register")
    public String registerTerms() {

        // terms.htmlを表示する
        return "terms";
    }


    // 利用規約に同意したときの処理
    // /terms/register/agreeにPOSTされたときに実行する
    @PostMapping("/terms/register/agree")
    public String agreeRegisterTerms(HttpSession session) {

        // 利用規約に同意したことをSessionに保存する
        session.setAttribute("registerTermsAgreed", true);

        // 利用規約に同意したので新規登録画面へ移動する
        return "redirect:/register";
    }


    // /registerにGETアクセスされたときに実行する
    @GetMapping("/register")
    public String viewResiter(
            Model model,
            RegisterForm form,
            HttpSession session) {

        // 利用規約に同意していない場合
        if (!Boolean.TRUE.equals(
                session.getAttribute("registerTermsAgreed"))) {

            // 利用規約ページへ戻す
            return "redirect:/terms/register";
        }

        // HTMLに空の新規登録フォームを渡す
        model.addAttribute("registerForm", new RegisterForm());

        // register.htmlを表示する
        return "register";
    }


    // ログイン処理
    // /loginにPOSTされたときにログイン処理を行う
    @PostMapping("/login")
    public String login(
            Model model,
            LoginForm form,
            HttpSession session) {

        // 入力されたメールアドレスを使ってDBからユーザー情報を検索する
        var userInfo = service.searchUserById(form.getLoginMail());

        // ユーザーが存在していて、
        // さらに入力されたパスワードとDBのパスワードが一致しているか確認する
        var isCorrectUserAuth = userInfo.isPresent()
                && passwordEncoder.matches(
                    // 入力されたパスワード
                    form.getPassword(),

                    // DBに保存されている暗号化されたパスワード
                    userInfo.get().getPassword()
                );


        // Spring Securityで使用する認証情報を作成する
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(

                    // 認証するユーザー名としてメールアドレスを設定
                    form.getLoginMail(),

                    // パスワードはここでは保持しない
                    null,

                    // ユーザーに権限を設定しない
                    AuthorityUtils.NO_AUTHORITIES
                );


        // 空のSecurityContextを作成する
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        // 作成した認証情報をSecurityContextに設定する
        context.setAuthentication(authentication);

        // 作成したSecurityContextを現在のSecurityContextとして設定する
        SecurityContextHolder.setContext(context);

        // SecurityContextをSessionにも保存する
        session.setAttribute(
                // Session内でSecurityContextを保存するためのキー
                "SPRING_SECURITY_CONTEXT",

                // 保存するSecurityContext
                context
        );


        // ログイン情報が正しい場合
        if (isCorrectUserAuth) {

            // ログインしたユーザーのメールアドレスをSessionに保存する
            session.setAttribute(
                    "loginMail",
                    form.getLoginMail()
            );

            // ログイン後にhomeへ移動する
            return "redirect:/home";

        } else {

            // ログインに失敗したことをHTMLに伝える
            model.addAttribute(
                    "errorMsg",
                    "メールアドレスまたはパスワードが間違えています"
            );

            // 入力されたログインフォームをHTMLに戻す
            model.addAttribute(
                    "loginForm",
                    form
            );

            // 新規登録フォームもHTMLに渡す
            model.addAttribute(
                    "registerForm",
                    new RegisterForm()
            );

            // ログイン画面をもう一度表示する
            return "login";
        }
    }


    // 新規登録処理
    // /registerにPOSTされたときに新規登録処理を行う
    @PostMapping("/register")
    public String register(
            RegisterForm form,
            Model model,
            HttpSession session) {

        // 利用規約に同意していない場合
        if (!Boolean.TRUE.equals(
                session.getAttribute("registerTermsAgreed"))) {

            // 利用規約ページへ戻す
            return "redirect:/terms/register";
        }


        // 入力されたユーザー情報を使ってユーザーを登録する
        service.registerUser(form);

        // 登録したユーザーのメールアドレスをSessionに保存する
        session.setAttribute(
                "loginMail",
                form.getLoginMail()
        );


        // Spring Securityで使用する認証情報を作成する
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(

                    // 認証するユーザー名としてメールアドレスを設定
                    form.getLoginMail(),

                    // パスワードはここでは保持しない
                    null,

                    // ユーザーに権限を設定しない
                    AuthorityUtils.NO_AUTHORITIES
                );


        // 空のSecurityContextを作成する
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        // 作成した認証情報をSecurityContextに設定する
        context.setAuthentication(authentication);

        // 作成したSecurityContextを現在のSecurityContextとして設定する
        SecurityContextHolder.setContext(context);


        // SecurityContextをSessionに保存する
        session.setAttribute(
                // Session内でSecurityContextを保存するためのキー
                "SPRING_SECURITY_CONTEXT",

                // 保存するSecurityContext
                context
        );


        // 登録が完了したので、
        // 利用規約に同意した情報をSessionから削除する
        session.removeAttribute("registerTermsAgreed");


        // 新規登録後にhomeへ移動する
        return "redirect:/home";
    }
}