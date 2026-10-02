package com.example.ai_school.dto;

// Favorite Entityを扱うためのクラス
import com.example.ai_school.entity.Favorite;

// School Entityを扱うためのクラス
import com.example.ai_school.entity.School;

// Getterを自動生成するLombok
import lombok.Getter;

// Getterを自動生成する
@Getter 

public class FavoriteSchoolDto {

    // お気に入り情報を保存する
    private Favorite favorite;

    // お気に入りに登録されている学校情報を保存する
    private School school;

    // FavoriteとSchoolを受け取ってFavoriteSchoolDtoを作成する
    public FavoriteSchoolDto(Favorite favorite, School school){

        // 受け取ったFavoriteをフィールドに保存する
        this.favorite = favorite;

        // 受け取ったSchoolをフィールドに保存する
        this.school = school;

    }
    
}