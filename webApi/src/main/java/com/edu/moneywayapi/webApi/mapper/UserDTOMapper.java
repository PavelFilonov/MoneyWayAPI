package com.edu.moneywayapi.webApi.mapper;

import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.mapper.UserMapper;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper implements UserMapper<UserDTO> {

    @Autowired
    private GroupDTOMapper groupDTOMapper;

    @Autowired
    private CategoryDTOMapper categoryDTOMapper;

    @Override
    public UserDTO map(User user) {
        if (user == null)
            return null;

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .password(user.getPassword())
                .groups(groupDTOMapper.mapListToDTO(user.getGroups()))
                .categories(categoryDTOMapper.mapListToDTO(user.getCategories()))
                .build();
    }

    @Override
    public User map(UserDTO obj) {
        if (obj == null)
            return null;

        return User.builder()
                .id(obj.getId())
                .email(obj.getEmail())
                .login(obj.getLogin())
                .password(obj.getPassword())
                .groups(groupDTOMapper.mapListToEntity(obj.getGroups()))
                .categories(categoryDTOMapper.mapListToEntity(obj.getCategories()))
                .build();
    }
}
