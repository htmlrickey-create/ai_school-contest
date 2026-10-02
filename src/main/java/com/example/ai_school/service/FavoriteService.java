package com.example.ai_school.service;

// Optionalを使用するためのimport
import java.util.Optional;

// Serviceクラスとして登録するためのアノテーション
import org.springframework.stereotype.Service;

// Favoriteエンティティを使用するためのimport
import com.example.ai_school.entity.Favorite;

// FavoriteRepositoryを使用するためのimport
import com.example.ai_school.repository.FavoriteRepository;


// お気に入りに関する処理を担当するService
@Service 
public class FavoriteService {

    // FavoriteRepositoryを保持する
    private final FavoriteRepository repository;

    // コンストラクタ
    // SpringからFavoriteRepositoryを受け取って設定する
    public FavoriteService( FavoriteRepository repository){
        this.repository = repository;
    }

    // 指定されたIDのお気に入りを削除する
    public void  deleteFavorite(Long id, String loginMail){

        // IDを使ってDBからお気に入りを検索する
        // お気に入りが存在する場合はFavoriteが入る
        Optional<Favorite> result = repository.findById(id);

        // お気に入りが存在するか確認する
        if(result.isPresent()){

            // Optionalから実際のFavoriteを取り出す
            Favorite favorite = result.get();

            // お気に入りのメールアドレスとログイン中のユーザーのメールアドレスが一致するか確認する
            if(favorite.getLoginMail().equals(loginMail)){
                // 本人のお気に入りだった場合、そのIDのお気に入りをDBから削除する
                repository.deleteById(id);
            }
        }
        
    }
    
}