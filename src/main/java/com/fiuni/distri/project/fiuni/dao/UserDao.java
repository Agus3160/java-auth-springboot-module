package com.fiuni.distri.project.fiuni.dao;

import com.fiuni.distri.project.fiuni.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
