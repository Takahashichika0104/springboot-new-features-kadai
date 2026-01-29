// 追加：ReviewRepository.java
package com.example.samuraitravel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByHouseOrderByCreatedAtDesc(House house);

    Optional<Review> findByHouseAndUser(House house, User user);

    long countByHouse(House house);
}
