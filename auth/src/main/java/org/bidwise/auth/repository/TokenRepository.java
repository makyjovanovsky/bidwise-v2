package org.bidwise.auth.repository;

import org.bidwise.auth.entity.token.TokenEntity;
import org.bidwise.auth.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity,Long> {

    List<TokenEntity> findAllByUser(UserEntity user);

    Optional<TokenEntity> findByToken(String token);
}
