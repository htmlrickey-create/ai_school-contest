// 診断履歴の削除対象となるformを一時的に保存する変数
let deleteForm = null;


// 診断履歴の削除確認モーダルを開く関数
function openDeleteModal(button) {

    // クリックされたボタンが入っている削除用formを取得
    deleteForm = button.closest(".delete-form");

    // 診断履歴削除用のモーダル要素を取得
    const modal = document.getElementById("diagnosisDeleteModal");

    // モーダルが存在する場合のみ処理する
    if (modal) {
        // モーダルを表示する
        modal.style.display = "flex";
    }
}


// 診断履歴の削除確認モーダルを閉じる関数
function closeDeleteModal() {

    // 診断履歴削除用のモーダル要素を取得
    const modal = document.getElementById("diagnosisDeleteModal");

    // モーダルが存在する場合のみ処理する
    if (modal) {
        // モーダルを非表示にする
        modal.style.display = "none";
    }

    // 保存していた削除対象のformをリセットする
    deleteForm = null;
}


// 診断履歴の削除を実行する関数
function submitDelete() {

    // 削除対象のformが保存されている場合のみ送信する
    if (deleteForm) {
        // 保存していたformを送信する
        deleteForm.submit();
    }
}


// お気に入りの削除対象となるformを一時的に保存する変数
let favoriteDeleteForm = null;


// お気に入り削除確認モーダルを開く関数
function openFavoriteDeleteModal(button) {

    // クリックされたボタンが入っているお気に入り削除用formを取得
    favoriteDeleteForm = button.closest(".favorite-delete-form");

    // お気に入り削除用のモーダル要素を取得
    const modal = document.getElementById("favoriteDeleteModal");

    // モーダルが存在する場合のみ処理する
    if (modal) {
        // モーダルを表示する
        modal.style.display = "flex";
    }
}


// お気に入り削除確認モーダルを閉じる関数
function closeFavoriteDeleteModal() {

    // お気に入り削除用のモーダル要素を取得
    const modal = document.getElementById("favoriteDeleteModal");

    // モーダルが存在する場合のみ処理する
    if (modal) {
        // モーダルを非表示にする
        modal.style.display = "none";
    }

    // 保存していた削除対象のformをリセットする
    favoriteDeleteForm = null;
}


// お気に入りの削除を実行する関数
function submitFavoriteDelete() {

    // 削除対象のformが保存されている場合のみ送信する
    if (favoriteDeleteForm) {
        // 保存していたformを送信する
        favoriteDeleteForm.submit();
    }
}
