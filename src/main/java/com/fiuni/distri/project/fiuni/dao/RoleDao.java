package com.fiuni.distri.project.fiuni.dao;

import com.fiuni.distri.project.fiuni.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {

    Optional<Role> findByRol(String rol);

}
