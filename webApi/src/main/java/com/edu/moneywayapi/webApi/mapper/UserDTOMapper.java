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
        return null;
    }

    @Override
    public User map(UserDTO obj) {
        return null;
    }
}
