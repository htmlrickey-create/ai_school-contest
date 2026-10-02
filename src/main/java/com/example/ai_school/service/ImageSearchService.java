package com.example.ai_school.service;

// 設定ファイルから値を取得するためのアノテーション
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


// 画像検索に関する処理を担当するService
@Service
public class ImageSearchService {

    // SerpApiと通信するためのRestClient
    private final RestClient restClient;

    // application.propertiesなどからSerpApiのAPIキーを取得する
    @Value("${serpapi.api-key}")
    private String apiKey;

    // コンストラクタ
    // SerpApiのURLを基本URLとしてRestClientを作成する
    public ImageSearchService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://serpapi.com")
                .build();
    }

    // 学校名を使って画像を検索するメソッド
    public String search(String schoolName) {

        // 画像検索を開始したことをコンソールに表示する
        System.out.println("画像検索開始: " + schoolName);

        // SerpApiにGETリクエストを送信して画像検索を行う
        Map<String, Object> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        // SerpApiの検索APIを指定する
                        .path("/search")

                        // Google画像検索を使用するように指定する
                        .queryParam("engine", "google_images")

                        // 検索キーワードとして学校名＋高校を指定する
                        .queryParam("q", schoolName + " 高校")

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

        // SerpApiから返ってきたレスポンスをコンソールに表示する
        System.out.println("SerpApi response: " + response);

        // レスポンスが存在しない場合はnullを返す
        if (response == null) {
            return null;
        }

        // レスポンスから画像検索結果を取得する
        Object result = response.get("images_results");

        // 画像検索結果がList型でない場合はnullを返す
        if (!(result instanceof List<?> imageResults)) {
            System.out.println("image_results がありません");
            return null;
        }

        // 画像検索結果が0件の場合はnullを返す
        if (imageResults.isEmpty()) {
            System.out.println("画像検索結果が0件です");
            return null;
        }

        // 画像検索結果の最初のデータを取得する
        Object first = imageResults.get(0);

        // 最初の画像データがMap型でない場合はnullを返す
        if (!(first instanceof Map<?, ?> firstImage)) {
            return null;
        }

        // 最初の画像データから元画像のURLを取得する
        Object imageUrl = firstImage.get("original");

        // 取得した画像URLをコンソールに表示する
        System.out.println("取得した画像URL: " + imageUrl);

        // 画像URLがString型の場合、そのURLを返す
        if (imageUrl instanceof String url) {
            return url;
        }

        // 画像URLを取得できなかった場合はnullを返す
        return null;
    }
}