package com.udayan.tallyapp.user.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Query(value = """
        SELECT t FROM Token t
        WHERE t.user.id = :id
        AND t.tokenType = :tokenType
        AND (t.expired = false OR t.revoked = false)
    """)
    List<Token> findAllValidTokenByUser(UUID id, int tokenType);

    Optional<Token> findByToken(String token);
}
