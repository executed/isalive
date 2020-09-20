package com.devserbyn.isalive.repository;


import com.devserbyn.isalive.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
