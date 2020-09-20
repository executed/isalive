package com.devserbyn.isalive.repository;


import com.devserbyn.isalive.model.UserConfig;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {

}
