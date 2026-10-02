package com.example.ai_school.config;

// SpringのBeanを登録するために使用
import org.springframework.context.annotation.Bean;

// 設定クラスであることを示す
import org.springframework.context.annotation.Configuration;

// パスワードを暗号化するためのクラス
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// パスワード暗号化の共通インターフェース
import org.springframework.security.crypto.password.PasswordEncoder;

// Spring SecurityのHTTPセキュリティ設定を行うためのクラス
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// Spring Securityのセキュリティフィルターを設定するためのクラス
import org.springframework.security.web.SecurityFilterChain;


// Spring Securityに関する設定をまとめるクラス
@Configuration
public class SecurityConfig {

    // PasswordEncoderをSpringに登録する
    @Bean
    public PasswordEncoder passwordEncoder() {

        // BCryptを使ってパスワードを暗号化する
        return new BCryptPasswordEncoder();
    }

    // Spring Securityのルールを設定する
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

            // CSRF対策の設定
            .csrf(csrf -> csrf

                // /diagnosisへのリクエストではCSRFチェックを無視する
                .ignoringRequestMatchers("/diagnosis")
            )

        // URLごとのアクセス権限を設定する
        .authorizeHttpRequests(auth -> auth

                // ログインしていなくてもアクセスできるURLを指定
                .requestMatchers("/", "/login","/terms/register","/terms/register/agree", "/register", "/CSS/**").permitAll()

                // それ以外のページはログインしているユーザーだけアクセス可能
                .anyRequest().authenticated() 
            )

            // ログアウトに関する設定
            .logout(logout -> logout

                // ログアウト後にログイン画面へ移動する
                .logoutSuccessUrl("/login?logout")

                // ログアウト時にセッションを無効にする
                .invalidateHttpSession(true)

                // ログアウト時にJESSIONIDというCookieを削除する
                .deleteCookies("JESSIONID")

                // ログアウト処理はログインしていなくても実行可能
                .permitAll()
            );
        

        // 設定したSecurityFilterChainを返す
        return http.build();
    }
}