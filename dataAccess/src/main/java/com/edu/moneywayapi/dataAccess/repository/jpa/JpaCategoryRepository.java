package com.edu.moneywayapi.dataAccess.repository.jpa;

import com.edu.moneywayapi.dataAccess.dal.CategoryDAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryDAL, Long> {

    @Transactional
    @Modifying
    @Query(value = "update category set name = ?2 where id = ?1", nativeQuery = true)
    void rename(Long id, String name);

    @Transactional
    @Modifying
    @Query(value = "insert into user_category (user_id, category_id) values (?2, ?1)", nativeQuery = true)
    void saveToUser(Long categoryId, Long userId);

    @Transactional
    @Modifying
    @Query(value = "insert into group_category (group_id, category_id) values (?2, ?1)", nativeQuery = true)
    void saveToGroup(Long categoryId, Long groupId);

    @Query(value =  "select c.* " +
                    "   from category c " +
                    "       join user_category u_c on c.id = u_c.category_id" +
                    "       join user u on u.id u_c.user_id" +
                    "   where u.login = ?1",
            nativeQuery = true)
    List<CategoryDAL> findByUser(String username);
}
