package com.example.ai_school.form;

// GetterやSetterなどを自動生成するLombok
import lombok.Data;


// フォームクラスとして、ログイン画面から送られた情報を受け取る
@Data
public class LoginForm {

    // ログイン用のメールアドレスを受け取る
    private String loginMail;

    // パスワードを受け取る
    private String password;

}