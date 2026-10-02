package com.example.ai_school.service;

// HTTP通信を行うためのRestClient
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// APIから返ってくるデータの型を指定するためのクラス
import org.springframework.core.ParameterizedTypeReference;

// List型を使用するためのimport
import java.util.List;

// Schoolエンティティを使用するためのimport
import com.example.ai_school.entity.School;

// SchoolRepositoryを使用するためのimport
import com.example.ai_school.repository.SchoolRepository;

// ArrayListを使用するためのimport
import java.util.ArrayList;


// 学校に関する処理を担当するService
@Service
public class SchoolService {

    // 学校APIと通信するためのRestClient
    private final RestClient restClient;

    // 学校情報をDBから取得・保存するためのRepository
    private final SchoolRepository schoolRepository;

    // 学校画像を検索するService
    private final ImageSearchService imageSearchService;

    // 学校のWebサイトを検索するService
    private final WebSearchService webSearchService;

    // コンストラクタ
    // 必要なRepositoryやServiceをSpringから受け取る
    public SchoolService(
            SchoolRepository schoolRepository,
            ImageSearchService imageSearchService, WebSearchService webSearchService) {

        // 受け取ったSchoolRepositoryを設定する
        this.schoolRepository = schoolRepository;

        // 受け取ったImageSearchServiceを設定する
        this.imageSearchService = imageSearchService;

        // 受け取ったWebSearchServiceを設定する
        this.webSearchService = webSearchService;

        // 学校APIの基本URLを指定してRestClientを作成する
        this.restClient = RestClient.builder()
                .baseUrl("https://school.teraren.com")
                .build();
    }

// 学校コードの一覧から学校情報を取得する
public List<School> getSchoolsByCodes(List<String> codes) {

    // 渡された学校コードを使ってDBから学校を検索する
    List<School> schools =
            schoolRepository.findByCodeIn(codes);

    // 取得した学校を1校ずつ処理する
    for (School school : schools) {

        // 画像がDBにない場合だけ画像検索APIを実行
        if (school.getImageUrl() == null ||
                school.getImageUrl().isBlank()) {

            // 画像検索APIを実行することをコンソールに表示する
            System.out.println(
                    "画像検索API実行: " + school.getName()
            );

            // 学校名を使って画像検索APIから画像URLを取得する
            String imageUrl =
                    imageSearchService.search(school.getName());

            // 取得した画像URLをコンソールに表示する
            System.out.println(
                    "取得した画像URL: " + imageUrl
            );

            // 画像URLを取得できた場合
            if (imageUrl != null && !imageUrl.isBlank()) {

                // 学校の画像URLに取得したURLを設定する
                school.setImageUrl(imageUrl);

                // DBに保存
                schoolRepository.save(school);
            }
        }
    }

    // 学校情報の一覧を返す
    return schools;
}

    // 指定された都道府県の高校を取得する
    public List<School> getSchool(String prefecture) {

        // 都道府県が指定されていない場合はエラーにする
        if (prefecture == null || prefecture.isBlank()) {
            throw new IllegalArgumentException("都道府県が指定されていません");
        }

        // 高校を表す学校種別を指定する
        String schoolType = "D1";

        // まずDBから指定された都道府県と学校種別に一致する学校を検索する
        List<School> schools = schoolRepository.findByLocationContainingAndSchoolType(prefecture,schoolType);

        // DBに学校が存在する場合は、その学校一覧をそのまま返す
        if (!schools.isEmpty()) {
            return schools;
        }

        // APIから取得するページ番号を1に設定する
        int page = 1;

        // APIから取得したすべての学校を保存するList
        List<School> allSchools = new ArrayList<>();

        // APIから学校を取得し続ける
        while (true) {

            // 学校APIにGETリクエストを送り、学校一覧を取得する
            List<School> schools1 = restClient.get()
                    .uri("/schools.json?s=" + prefecture
                            + "&school_type=" + schoolType
                            + "&page=" + page)
                    .retrieve()

                    // APIから返ってきたJSONをSchoolのListとして受け取る
                    .body(new ParameterizedTypeReference<List<School>>() {
                    });

            // 今回取得した学校を全体のListに追加する
            allSchools.addAll(schools1);

            // 取得件数が50件未満なら、最後のページと判断してループを終了する
            if (schools1.size() < 50) {
                break;
            }

            // APIへ連続してアクセスしすぎないように1秒待機する
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                // 割り込み状態を元に戻す
                Thread.currentThread().interrupt();

                // ループを終了する
                break;
            }

            // 次のページへ進む
            page++;
        }

        // APIから取得した学校一覧をDBに保存する
        schoolRepository.saveAll(allSchools);

        // 取得した学校一覧を返す
        return allSchools;
    }

    // 学校コードを使って学校の詳細情報を取得する
    public School getSchoolDetail(String code) {

        // DBから学校コードを使って学校を検索する
        // 学校が見つからない場合はエラーにする
        School school = schoolRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("学校が見つかりません"));

        // DBの学校情報に変更があったかを管理する
        boolean updated = false;

        // 画像がDBにない場合だけ検索
        if (school.getImageUrl() == null ||
                school.getImageUrl().isBlank()) {

            // 学校名を使って画像検索APIから画像URLを取得する
            String imageUrl = imageSearchService.search(school.getName());

            // 画像URLを取得できた場合
            if (imageUrl != null && !imageUrl.isBlank()) {

                // 学校情報に画像URLを設定する
                school.setImageUrl(imageUrl);

                // 学校情報が変更されたことを記録する
                updated = true;
            }
        }

        // WebサイトがDBにない場合だけ検索
        if (school.getWebsiteUrl() == null ||
                school.getWebsiteUrl().isBlank()) {

            // 学校名を使ってWebサイトを検索する
            String websiteUrl = webSearchService.search(school.getName());

            // WebサイトURLを取得できた場合
            if (websiteUrl != null && !websiteUrl.isBlank()) {

                // 学校情報にWebサイトURLを設定する
                school.setWebsiteUrl(websiteUrl);

                // 学校情報が変更されたことを記録する
                updated = true;
            }
        }

        // 変更があった場合だけDB保存
        if (updated) {
            schoolRepository.save(school);
        }

        // 学校の詳細情報を返す
        return school;
    }
}