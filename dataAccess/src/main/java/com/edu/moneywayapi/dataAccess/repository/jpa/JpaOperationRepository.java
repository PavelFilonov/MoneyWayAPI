package com.edu.moneywayapi.dataAccess.repository.jpa;

import com.edu.moneywayapi.dataAccess.dal.CategoryDAL;
import com.edu.moneywayapi.dataAccess.dal.OperationDAL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaOperationRepository extends JpaRepository<OperationDAL, Long> {
    List<OperationDAL> findByCategoryDAL(CategoryDAL categoryDAL);
}
