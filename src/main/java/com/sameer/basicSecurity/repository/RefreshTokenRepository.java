package com.sameer.basicSecurity.repository;

import com.sameer.basicSecurity.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken,String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);

}
