package com.fiuni.distri.project.fiuni.service;

import com.fiuni.distri.project.fiuni.jwt.JwtUtils;
import com.fiuni.distri.project.fiuni.dao.RoleDao;
import com.fiuni.distri.project.fiuni.dao.UserDao;
import com.fiuni.distri.project.fiuni.domain.Role;
import com.fiuni.distri.project.fiuni.domain.User;
import com.fiuni.distri.project.fiuni.dto.UserDto;
import com.fiuni.distri.project.fiuni.exceptions.ApiException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthUserService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtils jwtUtils;


    public UserDto getUserInfoByAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) throw new ApiException(HttpStatus.UNAUTHORIZED, "No posee credenciales para ejecutar este metodo");

        String token = (String) authentication.getCredentials(); // Se obtiene el jwt

        if (token == null || token.isEmpty()){
            System.out.println(token);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token is missing or invalid");
        }

        String userId = jwtUtils.getSubject(token);  // Se obtiene el subject del token, el id del usuario

        User user = userDao.findById(Integer.parseInt(userId)).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return modelMapper.map(user, UserDto.class);
    }

    public void registerNewUser(UserDto userDto) {

        //Map the dto to domain and set the role to the user
        User newUser = modelMapper.map(userDto, User.class);

        //Add all the roles that the user has
        Set<Role> roleSet = new HashSet<>();
        userDto.getRol_id().forEach(rolId -> {
            Optional<Role> role = this.roleDao.findById(rolId);
            if (role.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "Role not found");
            roleSet.add(role.get());
        });
        newUser.setRoles(roleSet);

        //Hash the password and set it to the user
        newUser.setPassword(this.passwordEncoder.encode(userDto.getPassword()));

        //Save the user into the db
        this.userDao.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //Verifies if the user exists
        Optional<User> userOptional = userDao.findByEmail(email);
        if (userOptional.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
        User user = userOptional.get();

        //Get the authority of the user (Their ROL)
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRol()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                email,
                user.getPassword(),
                authorities
        );
    }
}
