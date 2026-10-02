package com.example.ai_school.repository;

// Schoolエンティティを使用するためのimport
import com.example.ai_school.entity.School;

// List型を使用するためのimport
import java.util.List;

// Spring Data JPAのRepository機能を利用するためのimport
import org.springframework.data.jpa.repository.JpaRepository;


// School用のRepository
// JpaRepositoryを継承することで、DBへの保存・検索・削除などの機能を利用できる
// SchoolのIDはString型なので、JpaRepositoryの2つ目の型にStringを指定する
public interface SchoolRepository extends JpaRepository<School, String> {

    // locationに指定した都道府県が含まれていて、
    // schoolTypeも一致する学校を検索する
    List<School> findByLocationContainingAndSchoolType(
            String prefecture,
            String schoolType
    );

    // 渡された学校コードの一覧に一致する学校を検索する
    // AI診断で選ばれた学校コードから学校情報を取得するときなどに使用する
    List<School> findByCodeIn(List<String> codes);
}