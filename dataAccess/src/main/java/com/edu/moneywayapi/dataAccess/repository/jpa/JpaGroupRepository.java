package com.edu.moneywayapi.dataAccess.repository.jpa;

import com.edu.moneywayapi.dataAccess.dal.GroupDAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JpaGroupRepository extends JpaRepository<GroupDAL, Long> {
    GroupDAL findByToken(String token);

    @Transactional
    @Modifying
    @Query(value =  "delete from user_group" +
                    "   where group_id = ?1 and user_id = ?2",
            nativeQuery = true)
    void deleteUser(Long groupId, Long userId);

    @Query(value =  "select u.login " +
                    "   from user1 u " +
                    "       join user_group u_g " +
                    "           on u_g.group_id = ?1 and u.id = u_g.user_id",
            nativeQuery = true)
    List<String> getUsers(Long groupId);

    @Query(value =  "select g.owner_id " +
                    "   from group1 g  " +
                    "   where id = ?1  ",
            nativeQuery = true)
    Long getOwnerId(Long groupId);

    @Transactional
    @Modifying
    @Query(value = "insert into user_group (user_id, group_id) values (?2, ?1)", nativeQuery = true)
    void addUser(Long groupId, Long userId);

    @Transactional
    @Modifying
    @Query(value = "update group1 set name = ?2 where id = ?1", nativeQuery = true)
    void updateName(Long groupId, String name);
}
