package com.example.ai_school.service;

// List型を使用するためのimport
import java.util.List;

// Serviceクラスとして登録するためのアノテーション
import org.springframework.stereotype.Service;

// 診断フォームから送られてきた回答を扱うDTO
import com.example.ai_school.dto.DiagnosisResultDto;

// Gemini APIを利用するためのClient
import com.google.genai.Client;

// Gemini APIから返ってくるレスポンスを扱うクラス
import com.google.genai.types.GenerateContentResponse;

// 学校情報を扱うEntity
import com.example.ai_school.entity.School;

import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.ThinkingConfig;
import com.google.genai.types.ThinkingLevel;
import com.google.genai.types.ThinkingConfig;

// Geminiを使ったAI処理を担当するService
@Service
public class GeminiService {


    // Gemini APIと通信するためのClient
    private final Client client;

    // コンストラクタ
    // Gemini APIを利用するためのClientを作成する
    public GeminiService() {
        this.client = new Client();
    }



    public String ask(String prompt) {

        GenerateContentConfig config = GenerateContentConfig.builder()
                .thinkingConfig(
                        ThinkingConfig.builder()
                                .thinkingLevel("LOW")
                                .build()
                )
                .maxOutputTokens(1024)
                .build();

            long start = System.currentTimeMillis();

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-3.8-flash",
                    prompt,
                    config
            );

            long elapsed = System.currentTimeMillis() - start;

            System.out.println("Gemini API処理時間: " + elapsed + "ms");

            return response.text();
    }

    // ユーザーの診断結果と学校情報を使ってAIにおすすめの高校を選んでもらうメソッド
    public String diagnosis (DiagnosisResultDto request, Integer age, String prefecture , List<School> schools){

        // AIに渡す高校候補の一覧を作成するためのStringBuilder
        StringBuilder schoolList = new StringBuilder(); 

        // 都道府県・年齢に該当する高校を1校ずつ処理する
        for(School school : schools){
            // 学校コード・学校名・所在地を高校候補の一覧に追加する
            schoolList.append(
                "コード:" + school.getCode()
                +",学校名:" + school.getName()
                + ",所在地:" + school.getLocation()
                +"\n"
            );
        }


        // Geminiに送信するプロンプトを作成する
        String prompt = """
            あなたは高校選びをサポートするAIです。

            以下はユーザーの情報です。

            [年齢]
            %s

            [都道府県]
            %s

            [興味]
            %s

            [偏差値]
            %s

            [学校生活]
            %s

            [将来]
            %s

            [学び方]
            %s

            [重視すること]
            %s

            [進路について]
            %s

            以下は、このユーザーの都道府県・年齢に該当する高校の候補です。

            [高校候補]
            %s

            ユーザーの回答を総合的に分析し、
            候補高校の中から特に合っていて偏差値もユーザーが選択した偏差値と近い高校を3校選んでください。

            必ず[高校候補]に存在する学校だけを選んでください。
            存在しない学校コードを作ってはいけません。

            回答は必ず次の形式だけにしてください。

            {
              "reason": "おすすめの理由",
              "schoolCodes": ["学校コード1", "学校コード2", "学校コード3"]
            }
            """.formatted(
                // %sの部分にユーザーの年齢を入れる
                age,

                // %sの部分にユーザーの都道府県を入れる
                prefecture,

                // ユーザーが診断で選択した興味を入れる
                request.getInterest(),

                // ユーザーが診断で選択した偏差値を入れる
                request.getDeviation(),

                // ユーザーが診断で選択した学校生活への希望を入れる
                request.getSchoolLife(),

                // ユーザーが診断で選択した将来についての回答を入れる
                request.getFuture(),

                // ユーザーが診断で選択した学び方を入れる
                request.getLearning(),

                // ユーザーが診断で重視していることを入れる
                request.getPriority(),

                // ユーザーが診断で回答した進路についての内容を入れる
                request.getDecision(),

                // 作成した高校候補の一覧を入れる
                schoolList
            );

    // 作成したプロンプトをGeminiに送信して、AIの診断結果を返す
    return ask(prompt);



    }
}
