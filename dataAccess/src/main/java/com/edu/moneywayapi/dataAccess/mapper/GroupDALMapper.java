package com.edu.moneywayapi.dataAccess.mapper;

import com.edu.moneywayapi.dataAccess.dal.GroupDAL;
import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupDALMapper implements GroupMapper<GroupDAL> {

    private final CategoryDALMapper categoryDALMapper;

    @Autowired
    public GroupDALMapper(CategoryDALMapper categoryDALMapper) {
        this.categoryDALMapper = categoryDALMapper;
    }

    @Override
    public GroupDAL map(Group group) {
        if (group == null)
            return null;

        return GroupDAL.builder()
                .id(group.getId())
                .name(group.getName())
                .token(group.getToken())
                .ownerId(group.getOwnerId())
                .categories(categoryDALMapper.mapListToDAL(group.getCategories()))
                .build();
    }

    @Override
    public Group map(GroupDAL obj) {
        if (obj == null)
            return null;

        return Group.builder()
                .id(obj.getId())
                .name(obj.getName())
                .token(obj.getToken())
                .ownerId(obj.getOwnerId())
                .categories(categoryDALMapper.mapListToEntity(obj.getCategories()))
                .build();
    }

    public List<GroupDAL> mapListToDAL(List<Group> groups) {
        if (groups == null)
            return new ArrayList<>();

        List<GroupDAL> groupsDAL = new ArrayList<>();

        for (Group group : groups) {
            groupsDAL.add(map(group));
        }

        return groupsDAL;
    }

    public List<Group> mapListToEntity(List<GroupDAL> groupsDAL) {
        if (groupsDAL == null)
            return new ArrayList<>();

        List<Group> groups = new ArrayList<>();

        for (GroupDAL groupDAL : groupsDAL) {
            groups.add(map(groupDAL));
        }

        return groups;
    }
}
