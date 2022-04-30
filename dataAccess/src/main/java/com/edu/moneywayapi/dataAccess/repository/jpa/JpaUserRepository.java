package com.edu.moneywayapi.dataAccess.repository.jpa;

import com.edu.moneywayapi.dataAccess.dal.UserDAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserDAL, Long> {
    UserDAL findByLogin(String login);

    UserDAL findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query(value =  "update user1   " +
                    "set email = ?1 " +
                    "where id = ?2  ",
            nativeQuery = true)
    void updateEmail(String email, Long id);

    @Transactional
    @Modifying
    @Query(value =  "update user1   " +
                    "set login = ?1 " +
                    "where id = ?2  ",
            nativeQuery = true)
    void updateLogin(String login, Long id);

    @Transactional
    @Modifying
    @Query(value =  "update user1      " +
                    "set password = ?1 " +
                    "where id = ?2     ",
            nativeQuery = true)
    void updatePassword(String password, Long id);
}
