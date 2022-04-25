package com.edu.moneywayapi.domain.mapper;

import com.edu.moneywayapi.domain.entity.Operation;

public interface OperationMapper <T> {
    T map(Operation operation);
    Operation map(T obj);
}
