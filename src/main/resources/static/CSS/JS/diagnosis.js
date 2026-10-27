/* =========================
   診断データ
========================= */

// 診断で使用する質問を配列としてまとめる
const questions = [

    {
        // 質問のタイトル
        title: "どんなことに興味がありますか？",

        // 質問についての補足説明
        description: "今の時点で気になるものを選んでください。",

        // 回答を保存するときに使用する名前
        key: "interest",

        // trueの場合、複数の回答を選択できる
        multiple: true,

        // この質問で選択できる回答
        answers: [
            "IT・プログラミング",
            "美容・ファッション",
            "医療・福祉",
            "スポーツ",
            "芸術・デザイン",
            "まだ決まっていない"
        ]
    },

    {
        title: "あなたの偏差値を教えてください",
        description: "現時点のあなたの偏差値を選んでください",

        // この質問の回答をanswersのdeviationという名前で保存する
        key: "deviation",

        // 選択肢
        answers: [
            "偏差値70以上",
            "偏差値60以上",
            "偏差値50",
            "偏差値40",
            "わからない"
        ]
    },

    {
        title: "高校生活で大切にしたいことは？",
        description: "一番近いと思うものを選んでください。",
        key: "schoolLife",

        // 複数の回答を選択できる質問
        multiple: true,

        answers: [
            "勉強をしっかり頑張りたい",
            "部活動を頑張りたい",
            "友達と楽しく過ごしたい",
            "自分のペースで通いたい",
            "専門的なことを学びたい",
            "まだ決まっていない"
        ]
    },

    {
        title: "将来について、今の気持ちに近いのは？",
        description: "まだ具体的に決まっていなくても大丈夫です。",
        key: "future",
        multiple: true,

        answers: [
            "大学や短大に進学したい",
            "専門学校に進みたい",
            "高校卒業後に就職したい",
            "資格を取りたい",
            "まだ決まっていない"
        ]
    },

    {
        title: "どんな学び方が自分に合いそう？",
        description: "高校での過ごし方をイメージして選んでください。",
        key: "learning",
        multiple: true,

        answers: [
            "授業を中心にコツコツ学びたい",
            "実習や体験をしながら学びたい",
            "専門分野を深く学びたい",
            "自分のペースで学びたい",
            "いろいろなことに挑戦したい"
        ]
    },

    {
        title: "高校選びで気になることは？",
        description: "学校を探すときに重視したいものを選んでください。",
        key: "priority",
        multiple: true,

        answers: [
            "学校の雰囲気",
            "進学実績",
            "就職実績",
            "部活動",
            "資格・専門的な学習",
            "通いやすさ"
        ]
    },

    {
        title: "今、学校選びについてどのくらい決まっていますか？",
        description: "今のあなたの状態に近いものを選んでください。",
        key: "decision",

        answers: [
            "行きたい学校がほぼ決まっている",
            "いくつか候補がある",
            "興味のある学校を探している",
            "まだ何も決まっていない"
        ]
    }

];


/* =========================
   状態管理
========================= */

// 現在表示している質問の番号
// 0から始まるため、最初の質問は0
let currentQuestion = 0;

// ユーザーが選択した回答を保存するオブジェクト
// 例：answers["interest"] = ["IT・プログラミング"]
const answers = {};


/* =========================
   DOM取得
========================= */

// HTMLから各要素を取得する
// JavaScriptからHTMLの表示・非表示や内容変更を行うために使用する

// 診断開始前の画面
const intro = document.getElementById("intro");

// 質問画面
const quiz = document.getElementById("quiz");

// 結果確認画面
const result = document.getElementById("result");

// 診断開始ボタン
const startButton = document.getElementById("startButton");

// 前へ・次へボタン
const previousButton = document.getElementById("previousButton");
const nextButton = document.getElementById("nextButton");

// 現在の質問番号と進捗バー
const currentNumber = document.getElementById("currentNumber");
const progress = document.getElementById("progress");

// 質問番号・タイトル・説明
const questionNumber = document.getElementById("questionNumber");
const questionTitle = document.getElementById("questionTitle");
const questionDescription = document.getElementById("questionDescription");

// 回答ボタンを入れるHTML要素
const answerList = document.getElementById("answerList");

// 結果画面に回答を表示する場所
const resultAnswers = document.getElementById("resultAnswers");

// 診断結果を見るボタン
const resultButton = document.getElementById("resultButton");


/* =========================
   診断開始
========================= */

// 診断開始ボタンがクリックされたときに実行する
startButton.addEventListener("click", () => {

    // 診断開始前の画面を非表示にする
    intro.classList.add("hidden");

    // 質問画面を表示する
    quiz.classList.remove("hidden");

    // 最初の質問から開始する
    currentQuestion = 0;

    // 現在の質問を画面に表示する
    showQuestion();

});


/* =========================
   質問表示
========================= */

// 現在の質問をHTMLに表示する関数
function showQuestion() {

    // questions配列から現在の質問を取得する
    const question = questions[currentQuestion];

    // 以前選択した回答があれば取得する
    // 「前へ」で戻ったときに選択状態を復元するために使用する
    const savedAnswer = answers[question.key];


    /* 質問番号 */

    // 画面に現在の質問番号を表示する
    currentNumber.textContent = currentQuestion + 1;

    // QUESTION 01、QUESTION 02のような形式で表示する
    questionNumber.textContent =
        `QUESTION ${String(currentQuestion + 1).padStart(2, "0")}`;


    /* 質問内容 */

    // 質問タイトルを表示する
    questionTitle.textContent = question.title;

    // 質問の説明を表示する
    questionDescription.textContent = question.description;


    /* プログレス */

    // 全質問のうち、現在何％まで進んでいるか計算する
    const progressPercent =
        ((currentQuestion + 1) / questions.length) * 100;

    // 計算した割合をプログレスバーの横幅に設定する
    progress.style.width = `${progressPercent}%`;


    /* 回答欄をリセット */

    // 前の質問で作成した回答ボタンをすべて削除する
    answerList.innerHTML = "";


    /* 回答ボタン作成 */

    // 現在の質問にある回答を1つずつ処理する
    question.answers.forEach((answer) => {

        // 回答用のbutton要素をJavaScriptで作成する
        const button = document.createElement("button");

        // buttonの種類を通常のボタンにする
        button.type = "button";

        // CSSでデザインするためのクラスを設定する
        button.className = "answer-button";

        // ボタンに回答文を表示する
        button.textContent = answer;

        /*
         * 前のページから戻ってきた場合、
         * 保存されている回答を選択状態にする
         */

        // 複数選択の質問の場合
        if (question.multiple) {

            // 保存されている回答の中に現在の回答が含まれているか確認する
            if (savedAnswer && savedAnswer.includes(answer)) {

                // 含まれていれば選択状態にする
                button.classList.add("selected");
            }

        } else {

            // 単一選択の場合、保存されている回答と一致するか確認する
            if (savedAnswer === answer) {

                // 一致していれば選択状態にする
                button.classList.add("selected");
            }

        }


        // 回答ボタンがクリックされたときの処理
        button.addEventListener("click", () => {

            // 複数選択の質問の場合
            if (question.multiple) {

                //複数選択

                // 選択状態と未選択状態を切り替える
                button.classList.toggle("selected");

                // まだこの質問の回答が保存されていなければ空の配列を作る
                if (!answers[question.key]) {
                    answers[question.key] = [];
                }

                // ボタンが選択状態になった場合
                if (button.classList.contains("selected")) {

                    // 選択した回答を配列に追加する
                    answers[question.key].push(answer);
                } else {

                    // 選択を解除した回答を配列から削除する
                    answers[question.key] =
                        answers[question.key].filter(
                            item => item !== answer
                        );
                }

                // 回答が1つ以上選択されている場合だけ次へボタンを有効にする
                nextButton.disabled =
                    answers[question.key].length === 0;


            } else {

                //同じ質問の選択を解除

                // 現在の質問にあるすべての回答ボタンを取得する
                const buttons =
                    answerList.querySelectorAll(".answer-button");

                // すべてのボタンから選択状態を解除する
                buttons.forEach((item) => {
                    item.classList.remove("selected");
                });

                //今選んだ回答を選択

                // クリックされたボタンを選択状態にする
                button.classList.add("selected");

                //回答を保存

                // 選択した回答をanswersに保存する
                answers[question.key] = answer;

                //次への有効化

                // 回答したので「次へ」ボタンを有効にする
                nextButton.disabled = false;
            }
        });


        // 作成した回答ボタンをHTMLの回答欄に追加する
        answerList.appendChild(button);

    });


    /* 保存済み回答があるか */

    // 複数選択の質問の場合
    if (question.multiple) {

        // 回答が存在しない、または回答数が0なら「次へ」を無効にする
        nextButton.disabled =
            !savedAnswer ||
            savedAnswer.length === 0;
    } else {

        // 単一選択の場合、回答がなければ「次へ」を無効にする
        nextButton.disabled =
            !savedAnswer;
    }

    /* 前へボタン */

    // 最初の質問の場合
    if (currentQuestion === 0) {

        // 「前へ」ボタンを非表示にする
        previousButton.classList.add("hidden");

    } else {

        // 2問目以降なら「前へ」ボタンを表示する
        previousButton.classList.remove("hidden");

    }


    /* 最後の質問 */

    // 現在が最後の質問か確認する
    if (currentQuestion === questions.length - 1) {

        // 最後の質問では「次へ」ではなく「診断結果を見る」に変更する
        nextButton.innerHTML =
            `診断結果を見る <span>→</span>`;

    } else {

        // 最後の質問以外では通常の「次へ」にする
        nextButton.innerHTML =
            `次へ <span>→</span>`;

    }

}


/* =========================
   次へ
========================= */

// 「次へ」ボタンがクリックされたときの処理
nextButton.addEventListener("click", () => {

    // ボタンが無効になっている場合は何もしない
    if (nextButton.disabled) {
        return;
    }


    /* 最後の質問なら結果へ */

    // 現在が最後の質問なら診断結果確認画面へ進む
    if (currentQuestion === questions.length - 1) {

        // 結果画面を表示する
        showResult();

        return;
    }


    // 現在の質問番号を1つ進める
    currentQuestion++;

    // 次の質問を画面に表示する
    showQuestion();

});


/* =========================
   前へ
========================= */

// 「前へ」ボタンがクリックされたときの処理
previousButton.addEventListener("click", () => {

    // すでに最初の質問なら何もしない
    if (currentQuestion <= 0) {
        return;
    }

    // 現在の質問番号を1つ戻す
    currentQuestion--;

    // 戻った質問を画面に表示する
    // 保存されている回答があれば選択状態も復元される
    showQuestion();

});


/* =========================
   戻る
========================= */

// 「最初からやり直す」などの戻るボタンがクリックされたときの処理
document.getElementById("backButton")
    .addEventListener("click", () => {

        // 診断を最初からやり直すか確認する
        if (
            confirm("診断を最初からやり直しますか？")
        ) {

            // 質問番号を最初に戻す
            currentQuestion = 0;

            // answersに保存されているすべての回答を削除する
            Object.keys(answers).forEach((key) => {
                delete answers[key];
            });

            // 質問画面を非表示にする
            quiz.classList.add("hidden");

            // 診断開始画面を表示する
            intro.classList.remove("hidden");

        }

    });


/* =========================
   結果表示
========================= */

// 回答確認画面を表示する関数
function showResult() {

    // 質問画面を非表示にする
    quiz.classList.add("hidden");

    // 結果画面を表示する
    result.classList.remove("hidden");


    // 以前表示されていた回答を削除する
    resultAnswers.innerHTML = "";


    // すべての質問について回答を表示する
    questions.forEach((question) => {

        // 質問と回答をまとめるためのdivを作成する
        const wrapper =
            document.createElement("div");

        // CSS用のクラスを設定する
        wrapper.className = "result-answer";


        // 質問タイトルを表示するspanを作成する
        const label =
            document.createElement("span");

        label.className = "result-answer-label";

        // 質問タイトルを表示する
        label.textContent = question.title;


        // 回答を表示するspanを作成する
        const value =
            document.createElement("span");

        value.className = "result-answer-value";

        // 保存されている回答を表示する
        // 回答がなければ「未回答」と表示する
        value.textContent =
            answers[question.key] || "未回答";


        // 質問タイトルをwrapperに追加する
        wrapper.appendChild(label);

        // 回答をwrapperに追加する
        wrapper.appendChild(value);

        // 完成した質問・回答を結果画面に追加する
        resultAnswers.appendChild(wrapper);

    });

}


/* =========================
   結果 → 診断結果
========================= */

// 「診断結果を見る」ボタンがクリックされたときの処理
// asyncを付けることで、サーバーからの回答を待つことができる
resultButton.addEventListener("click", async () => {

    console.log("① 診断開始");

    result.classList.add("hidden");

    const loading = document.getElementById("diagnosisLoading");
    loading.classList.remove("hidden");

    try {
        const csrfToken =
            document.querySelector('meta[name="_csrf"]').content;

        const csrfHeader =
            document.querySelector('meta[name="_csrf_header"]').content;

        // 診断開始
        const response = await fetch("/diagnosis", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(answers)
        });

        if (!response.ok) {
            throw new Error("診断開始エラー: " + response.status);
        }

        const data = await response.json();
        const diagnosisId = data.diagnosisId;

        console.log("診断ID:", diagnosisId);

        // 3秒ごとに診断状態を確認
        while (true) {

            await new Promise(resolve => setTimeout(resolve, 3000));

            const statusResponse = await fetch(
                "/diagnosis/status/" + encodeURIComponent(diagnosisId),
                { cache: "no-store" }
            );

            if (!statusResponse.ok) {
                throw new Error(
                    "状態確認エラー: " + statusResponse.status
                );
            }

            const statusData = await statusResponse.json();

            console.log("診断状態:", statusData.status);

            if (statusData.status === "COMPLETED") {

                // 完了したら診断結果ページに移動
                window.location.href =
                    "/diagnosis/result/" + encodeURIComponent(diagnosisId);

                return;
            }

            if (statusData.status === "FAILED") {
                throw new Error("AI診断に失敗しました");
            }
        }

    } catch (error) {

        console.error("診断エラー:", error);

        loading.classList.add("hidden");
        result.classList.remove("hidden");

        alert("診断中にエラーが発生しました。もう一度お試しください。");
    }
});
