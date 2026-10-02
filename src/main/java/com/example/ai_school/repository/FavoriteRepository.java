package com.example.ai_school.repository;

// List型を使用するためのimport
import java.util.List;

// Spring Data JPAのRepository機能を利用するためのimport
import org.springframework.data.jpa.repository.JpaRepository;

// Favoriteエンティティを使用するためのimport
import com.example.ai_school.entity.Favorite;


// Favorite用のRepository
// JpaRepositoryを継承することで、DBへの保存・検索・削除などの機能を利用できる
public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {

    // loginMailを条件にして、そのユーザーのお気に入りをすべて検索する
    List<Favorite> findByLoginMail(String loginMail);

    // loginMailとschoolCodeの両方が一致するお気に入りが存在するか確認する
    // お気に入り登録済みかどうかの確認に使用する
    boolean existsByLoginMailAndSchoolCode(
            String loginMail,
            String schoolCode
    );

    // loginMailとschoolCodeの両方が一致するお気に入りを削除する
    void deleteByLoginMailAndSchoolCode(
            String loginMail,
            String schoolCode
    );
}