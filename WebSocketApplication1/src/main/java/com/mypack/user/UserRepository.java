package com.mypack.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

	List<User> findAllByStatus(Status online);
    // nickName is the ID now

	
}
