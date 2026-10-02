// 「パスワードを表示・非表示」にするボタンをすべて取得する
const toggles = document.querySelectorAll(".password-toggle");

// 取得したすべてのパスワード表示切り替えボタンに処理を設定する
toggles.forEach(toggle => {

    // ボタンがクリックされたときに処理を実行する
    toggle.addEventListener("click", () => {

        // data-targetに設定されているIDを使って、
        // 表示・非表示を切り替えるinput要素を取得する
        const input = document.getElementById(
            toggle.dataset.target
        );

        // 現在のinputがパスワード非表示状態か確認する
        if (input.type === "password") {

            // inputのtypeをtextに変更して、パスワードを表示する
            input.type = "text";

            // 目のアイコンを非表示状態のアイコンに変更する
            toggle.classList.remove("fa-eye");

            // 「パスワードが表示されている」ことを表すアイコンを追加する
            toggle.classList.add("fa-eye-slash");

        } else {

            // inputのtypeをpasswordに戻して、パスワードを非表示にする
            input.type = "password";

            // 「パスワードが表示されている」アイコンを削除する
            toggle.classList.remove("fa-eye-slash");

            // 「パスワードが非表示」状態を表す目のアイコンを追加する
            toggle.classList.add("fa-eye");

        }

    });

});
