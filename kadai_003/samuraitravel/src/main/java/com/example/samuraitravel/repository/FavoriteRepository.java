package com.example.samuraitravel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraitravel.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    // ======================
    // お気に入り存在チェック
    // ======================
    @Query("""
        SELECT f
        FROM Favorite f
        JOIN f.user u
        WHERE f.house.id = :houseId
        AND u.email = :email
    """)
    Optional<Favorite> findByHouseIdAndUserEmail(
            @Param("houseId") Integer houseId,
            @Param("email") String email
    );

    Optional<Favorite> findByUserIdAndHouseId(Integer userId, Integer houseId);

    // ======================
    // ★お気に入り一覧取得（シンプル版）
    // ======================
    List<Favorite> findByUserId(Integer userId);


    // ======================
    // ★おすすめ（プロ仕様）
    // N+1問題を防ぐ高速版
    // ======================
    @Query("""
        SELECT f
        FROM Favorite f
        JOIN FETCH f.house
        WHERE f.user.id = :userId
        ORDER BY f.id DESC
    """)
    List<Favorite> findByUserIdWithHouse(@Param("userId") Integer userId);

}
