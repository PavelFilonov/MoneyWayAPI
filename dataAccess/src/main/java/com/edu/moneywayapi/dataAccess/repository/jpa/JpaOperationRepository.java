package com.edu.moneywayapi.dataAccess.repository.jpa;

import com.edu.moneywayapi.dataAccess.dal.OperationDAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaOperationRepository extends JpaRepository<OperationDAL, Long> {

    @Query(value =  "select o.*" +
                    "   from operation o" +
                    "   where o.category_id = ?1 and o.date_operation between ?2 and ?3",
            nativeQuery = true)
    List<OperationDAL> findByCategoryAndPeriod(Long categoryId, LocalDateTime fromDate, LocalDateTime toDate);
}
