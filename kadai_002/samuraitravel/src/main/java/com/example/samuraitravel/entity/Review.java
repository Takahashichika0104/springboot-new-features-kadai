package com.example.samuraitravel.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data // ★ 追加：getter / setter / toString など自動生成
@Entity
@Table(
    name = "reviews",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "house_id"}
    )
)
public class Review {

    // -----------------------------
    // PK
    // -----------------------------

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id") 
    private Integer id;

    // -----------------------------
    // 外部キー（ユーザー）
    // -----------------------------

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // -----------------------------
    // 外部キー（民宿）
    // -----------------------------

    @ManyToOne
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    // -----------------------------
    // 評価
    // -----------------------------

    @Column(name = "rating", nullable = false) 
    private Integer rating;

    // -----------------------------
    // コメント
    // -----------------------------

    @Column(name = "comment", length = 500) 
    private String comment;

    // -----------------------------
    // 作成日時
    // -----------------------------

    @Column(name = "created_at") 
    private LocalDateTime createdAt;

    // -----------------------------
    // 更新日時
    // -----------------------------

    @Column(name = "updated_at") 
    private LocalDateTime updatedAt;

    // -----------------------------
    // 自動設定
    // -----------------------------

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
