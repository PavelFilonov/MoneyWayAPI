package com.edu.moneywayapi.domain.mapper;

import com.edu.moneywayapi.domain.entity.Group;

public interface GroupMapper <T> {
    T map(Group group);
    Group map(T obj);
}
