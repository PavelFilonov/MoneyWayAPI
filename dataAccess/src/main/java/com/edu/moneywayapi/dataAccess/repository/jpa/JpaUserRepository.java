package com.edu.moneywayapi.dataAccess.repository.jpa;

import com.edu.moneywayapi.dataAccess.dal.UserDAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<UserDAL, Long> {
    UserDAL findByLogin(String login);

    UserDAL findByEmail(String email);

    boolean existsByLogin(String login);

    @Modifying
    @Query(value =  "update user1 u" +
                    "set " +
                        "u.email = ?1,   " +
                        "u.login = ?2,   " +
                        "u.password = ?3 " +
                    "where u.id = ?4",
            nativeQuery = true)
    void update(String email, String login, String password, Long id);
}
