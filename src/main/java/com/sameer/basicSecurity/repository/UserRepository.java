package com.sameer.basicSecurity.repository;

import com.sameer.basicSecurity.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository  extends MongoRepository<User,String> {
     Optional<User> findByEmail(String email);
}
