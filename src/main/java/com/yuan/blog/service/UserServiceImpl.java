package com.yuan.blog.service;

import com.yuan.blog.config.IntegrationAuthentication;
import com.yuan.blog.config.IntegrationAuthenticationContext;
import com.yuan.blog.domain.Authority;
import com.yuan.blog.domain.User;
import com.yuan.blog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Transactional
    public User saveOrUpdateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User registerUser(User user) throws Exception {
        // 判断email重复
        User byEmail = userRepository.findByEmail(user.getEmail());
        if (byEmail != null) {
            throw new Exception("邮箱重复!");
        }
        User byUsername = userRepository.findByUsername(user.getUsername());
        if (byUsername != null) {
            throw new Exception("账号重复!");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public Page<User> listUsersByNameLike(String name, Pageable pageable) {
        name = "%" + name + "%";
        return userRepository.findByNameLike(name, pageable);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void removeUsersInBatch(List<User> users) {

    }

    //  实现 UserDetailsService 的方法
    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            IntegrationAuthentication authentication = IntegrationAuthenticationContext.get();
            String authType = authentication == null ? "" : authentication.getAuthType();
            log.info("the autgType is " + authType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> listUsersByUsernames(Collection<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }

    @Override
    public List<User> getUserByAuthority(Authority authority) {
        return userRepository.findByAuthoritiesContains(authority);
    }
}
