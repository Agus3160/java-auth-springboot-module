package com.fiuni.distri.project.fiuni.config;

import com.fiuni.distri.project.fiuni.dao.RoleDao;
import com.fiuni.distri.project.fiuni.domain.Role;
import com.fiuni.distri.project.fiuni.exceptions.ApiException;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Autowired
    RoleDao roleDao;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<Set<Integer>, Set<Role>>() {
            @Override
            protected void configure() {
                using(context -> {
                    Set<Integer> roleIds = (Set<Integer>) context.getSource();
                    Set<Role> roles = new HashSet<>();
                    for (Integer roleId : roleIds) {
                        Optional<Role> role = roleDao.findById(roleId);
                        if (role.isEmpty()) {
                            throw new ApiException(HttpStatus.BAD_REQUEST, "Role not found");
                        }
                        roles.add(role.get());
                    }
                    return roles;
                });
            }
        });

        return modelMapper;
    }
}
