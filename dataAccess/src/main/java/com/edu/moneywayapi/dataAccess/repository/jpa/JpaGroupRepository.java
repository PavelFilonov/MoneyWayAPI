package com.edu.moneywayapi.dataAccess.repository.jpa;

import com.edu.moneywayapi.dataAccess.dal.GroupDAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaGroupRepository extends JpaRepository<GroupDAL, Long> {
    GroupDAL findByToken(String token);

    @Query(value =  "delete from user_group u_g" +
                    "   where u_g.user_id = ?1",
            nativeQuery = true)
    void deleteUser(Long userId);
}
