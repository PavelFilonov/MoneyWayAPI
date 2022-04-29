package com.edu.moneywayapi.dataAccess.repository.jpa;

import com.edu.moneywayapi.dataAccess.dal.CategoryDAL;
import com.edu.moneywayapi.dataAccess.dal.OperationDAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaOperationRepository extends JpaRepository<OperationDAL, Long> {
    List<OperationDAL> findByCategoryDAL(CategoryDAL categoryDAL);

    @Query(value =  "select o.*" +
                    "   from operation o" +
                    "       join user_category u_c " +
                    "           on u_c.user_id = ?1 and o.category_id = u_c.category_id" +
                    "   where o.type = ?2 and o.date_operation between ?3 and ?4",
            nativeQuery = true)
    List<OperationDAL> findByUserIdAndTypeOperationAndPeriod(Long userId, String typeOperation,
                                                             LocalDateTime fromDate, LocalDateTime toDate);
}
