package com.edu.moneywayapi.webApi.mapper;

import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.mapper.GroupMapper;
import com.edu.moneywayapi.webApi.dto.GroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupDTOMapper implements GroupMapper<GroupDTO> {

    private final CategoryDTOMapper categoryDTOMapper;

    @Autowired
    public GroupDTOMapper(CategoryDTOMapper categoryDTOMapper) {
        this.categoryDTOMapper = categoryDTOMapper;
    }

    @Override
    public GroupDTO map(Group group) {
        if (group == null)
            return null;

        return GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .token(group.getToken())
                .ownerId(group.getOwnerId())
                .categories(categoryDTOMapper.mapListToDTO(group.getCategories()))
                .build();
    }

    @Override
    public Group map(GroupDTO obj) {
        if (obj == null)
            return null;

        return Group.builder()
                .id(obj.getId())
                .name(obj.getName())
                .token(obj.getToken())
                .ownerId(obj.getOwnerId())
                .categories(categoryDTOMapper.mapListToEntity(obj.getCategories()))
                .build();
    }

    public List<GroupDTO> mapListToDTO(List<Group> groups) {
        List<GroupDTO> groupsDTO = new ArrayList<>();

        for (Group group : groups) {
            groupsDTO.add(map(group));
        }

        return groupsDTO;
    }

    public List<Group> mapListToEntity(List<GroupDTO> groupsDTO) {
        List<Group> groups = new ArrayList<>();

        for (GroupDTO groupDTO : groupsDTO) {
            groups.add(map(groupDTO));
        }

        return groups;
    }
}
