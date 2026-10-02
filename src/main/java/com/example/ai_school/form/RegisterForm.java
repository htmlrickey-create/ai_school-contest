package com.example.ai_school.form;

// GetterやSetterなどを自動生成するLombok
import lombok.Data;

// GetterやSetterなどを自動生成する
@Data
public class RegisterForm {

    // 登録するユーザーの名前を受け取る
    private String name;

    // 登録するユーザーのメールアドレスを受け取る
    private String loginMail;

    // 登録するユーザーのパスワードを受け取る
    private String password;    

    // 登録するユーザーの年齢を受け取る
    private Integer age;

    // 登録するユーザーの学年を受け取る
    private String grade;

    // 登録するユーザーの都道府県を受け取る
    private String prefecture;
}