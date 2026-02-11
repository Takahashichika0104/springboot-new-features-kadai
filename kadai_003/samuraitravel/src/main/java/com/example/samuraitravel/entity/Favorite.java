package com.example.samuraitravel.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "house_id"})
    }
)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ======================
    // ユーザー
    // ======================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ======================
    // 民宿
    // ======================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    // ======================
    // 作成日時
    // ======================
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ======================
    // 更新日時
    // ======================
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ======================
    // 保存前処理
    // ======================
    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // ======================
    // 更新前処理
    // ======================
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ======================
    // getter / setter
    // ======================

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
