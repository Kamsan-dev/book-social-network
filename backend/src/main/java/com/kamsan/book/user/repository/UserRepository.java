package com.kamsan.book.user.repository;

import com.kamsan.book.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    Optional<User> findByPublicId(UUID userPublicId);

    @Modifying
    @Query("UPDATE User u SET u.profileImageId = :imageId WHERE u.publicId = :userPublicId")
    int updateUserProfileImage(String imageId, UUID userPublicId);

    Page<User> findAll(Pageable pageable);

}
