package com.example.ai_school.service;

// Optionalを使用するためのimport
import java.util.Optional;

// パスワードのハッシュ化などを行うためのクラス
import org.springframework.security.crypto.password.PasswordEncoder;

// Serviceクラスとして登録するためのアノテーション
import org.springframework.stereotype.Service;

// Userエンティティを使用するためのimport
import com.example.ai_school.entity.User;

// 新規登録フォームのデータを受け取るためのクラス
import com.example.ai_school.form.RegisterForm;

// UserRepositoryを使用するためのimport
import com.example.ai_school.repository.UserRepository;

// コンストラクタを自動生成するLombok
import lombok.RequiredArgsConstructor;


// ユーザーに関する処理を担当するService
@Service

// finalが付いたフィールドを受け取るコンストラクタを自動生成する
@RequiredArgsConstructor
public class UserService {

    // ユーザー情報をDBから取得・保存するためのRepository
    private final UserRepository repository;

    // パスワードをハッシュ化するためのPasswordEncoder
    private final PasswordEncoder passwordEncoder;

    // メールアドレスを使ってユーザーを検索する
    public Optional <User> searchUserById(String loginMail){

        // loginMailをIDとしてDBからユーザーを検索する
        return repository.findById(loginMail);
    }
    
    //新規登録処理
    public void registerUser(RegisterForm form){

        // 新しくUserオブジェクトを作成する
        User user = new User();



    // 入力されたメールアドレスがすでにDBに存在するか確認する
    if (repository.existsById(form.getLoginMail())) {

        // すでに存在する場合はエラーを発生させる
        throw new IllegalArgumentException("このメールアドレスは既に登録されています");
    }


        // フォームから受け取った名前をUserに設定する
        user.setName(form.getName());

        // フォームから受け取ったメールアドレスをUserに設定する
        user.setLoginMail(form.getLoginMail());

        // フォームから受け取った都道府県をUserに設定する
        user.setPrefecture(form.getPrefecture());

        // フォームから受け取った年齢をUserに設定する
        user.setAge(form.getAge());

        // フォームから受け取った学年をUserに設定する
        user.setGrade(form.getGrade());

        //パスワードをハッシュ化してからDBに保存
        user.setPassword(
            passwordEncoder.encode(form.getPassword())
        );

        // 作成したUser情報をDBに保存する
        repository.save(user);
    }

    // DBに登録されているユーザー数を取得する
    public long getUserCount() {

        // UserRepositoryのcount()を使ってユーザー数を取得する
        return repository.count();
}
}