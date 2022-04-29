package com.edu.moneywayapi.dataAccess.repository.jpa;

import com.edu.moneywayapi.dataAccess.dal.GroupDAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaGroupRepository extends JpaRepository<GroupDAL, Long> {
    GroupDAL findByToken(String token);

    @Transactional
    @Modifying
    @Query(value =  "delete from user_group u_g" +
                    "   where u_g.group_id = ?1 and u_g.user_id = ?2",
            nativeQuery = true)
    void deleteUser(Long groupId, Long userId);
}
