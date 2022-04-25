package com.edu.moneywayapi.dataAccess.mapper;

import com.edu.moneywayapi.dataAccess.dal.UserDAL;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDALMapper implements UserMapper<UserDAL> {

    @Autowired
    private GroupDALMapper groupDALMapper;

    @Autowired
    private CategoryDALMapper categoryDALMapper;

    @Override
    public UserDAL map(User user) {
        if (user == null)
            return null;

        return UserDAL.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .password(user.getPassword())
                .groups(groupDALMapper.mapListToDAL(user.getGroups()))
                .categories(categoryDALMapper.mapListToDAL(user.getCategories()))
                .build();
    }

    @Override
    public User map(UserDAL obj) {
        if (obj == null)
            return null;

        return User.builder()
                .id(obj.getId())
                .email(obj.getEmail())
                .login(obj.getLogin())
                .password(obj.getPassword())
                .groups(groupDALMapper.mapListToEntity(obj.getGroups()))
                .categories(categoryDALMapper.mapListToEntity(obj.getCategories()))
                .build();
    }
}
