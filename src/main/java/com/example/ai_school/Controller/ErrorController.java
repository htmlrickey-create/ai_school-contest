package com.example.ai_school.Controller;

// アプリ全体のControllerで発生した例外をまとめて処理するためのアノテーション
import org.springframework.web.bind.annotation.ControllerAdvice;

// 特定の例外が発生したときに処理するメソッドを指定するアノテーション
import org.springframework.web.bind.annotation.ExceptionHandler;

// このクラスをController全体に適用する例外処理クラスとして登録する
@ControllerAdvice

public class ErrorController {

    // Exceptionが発生した場合にこのメソッドを実行する
    @ExceptionHandler(Exception.class)

    public String handleException(Exception e) {

        // 発生したエラーの詳細をコンソールに表示する
        e.printStackTrace();

        // error.htmlを表示する
        return "error";

    }

}