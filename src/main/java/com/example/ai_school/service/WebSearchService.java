package com.example.ai_school.service;

// application.propertiesなどの設定値を取得するためのアノテーション
import org.springframework.beans.factory.annotation.Value;

// APIから返ってくるデータの型を指定するためのクラス
import org.springframework.core.ParameterizedTypeReference;

// Serviceクラスとして登録するためのアノテーション
import org.springframework.stereotype.Service;

// HTTP通信を行うためのRestClient
import org.springframework.web.client.RestClient;

// List型を使用するためのimport
import java.util.List;

// Map型を使用するためのimport
import java.util.Map;


// Webサイト検索に関する処理を担当するService
@Service
public class WebSearchService {

    // SerpApiと通信するためのRestClient
    private final RestClient restClient;

    // application.propertiesなどからSerpApiのAPIキーを取得する
    @Value("${serpapi.api-key}")
    private String apiKey;

    // コンストラクタ
    // SerpApiのURLを基本URLとしてRestClientを作成する
    public WebSearchService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://serpapi.com")
                .build();
    }

    // 学校名を使ってWebサイトを検索するメソッド
    public String search(String schoolName) {

        // Webサイト検索を開始したことをコンソールに表示する
        System.out.println("ウェブサイト検索開始: " + schoolName);

        // SerpApiにGETリクエストを送信してGoogle検索を行う
        Map<String, Object> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        // SerpApiの検索APIを指定する
                        .path("/search")

                        // Google検索を使用するように指定する
                        .queryParam("engine", "google")

                        // 学校名＋学校公式ホームページを検索キーワードにする
                        .queryParam("q", schoolName + " 学校公式ホームページ")

                        // SerpApiのAPIキーを指定する
                        .queryParam("api_key", apiKey)

                        // 検索地域を日本に指定する
                        .queryParam("gl", "jp")

                        // 検索言語を日本語に指定する
                        .queryParam("hl", "ja")

                        // URLを完成させる
                        .build())
                .retrieve()

                // APIから返ってきたJSONをMapとして受け取る
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        // Web検索の結果をコンソールに表示する
        System.out.println("Web検索結果 response: " + response);

        // レスポンスが存在しない場合はnullを返す
        if (response == null) {
            return null;
        }

        // レスポンスから通常の検索結果を取得する
        Object result = response.get("organic_results");

        // 検索結果がList型でない場合はnullを返す
        if (!(result instanceof List<?> searchResults)) {
            System.out.println("organic_results がありません");
            return null;
        }

        // 検索結果が0件の場合はnullを返す
        if (searchResults.isEmpty()) {
            System.out.println("Web検索結果が0件です");
            return null;
        }

        // 検索結果の最初のデータを取得する
        Object first = searchResults.get(0);

        // 最初の検索結果がMap型でない場合はnullを返す
        if (!(first instanceof Map<?, ?> firstResult)) {
            return null;
        }

        // 最初の検索結果からWebサイトのリンクを取得する
        Object link = firstResult.get("link");

        // 取得したWebサイトURLをコンソールに表示する
        System.out.println("取得したWebサイトURL: " + link);

        // URLがString型の場合、そのURLを返す
        if (link instanceof String url) {
            return url;
        }

        // WebサイトURLを取得できなかった場合はnullを返す
        return null;
    }
}