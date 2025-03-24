package com.udayan.tallyapp.user;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

     @Query("SELECT u FROM User u WHERE u.username = :param OR u.email = :param OR u.mobileNo = :param")
     Optional<User> findByUsernameOrEmailOrMobileNo(@Param("param") String param);
     Optional<User> findByEmail(String email);
     Optional<User> findByUsername(String username);
     @Query("SELECT u FROM User u WHERE u.username = :param OR u.email = :param")
     Optional<User> findByUsernameOrEmail(@Param("param") String param);
     Optional<User> findByMobileNo(String mobileNo);
     Optional<User> findByEmailOrMobileNo(String email, String mobileNo);

     @Query("""
       SELECT u FROM User u WHERE
            LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(u.mobileNo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
     Page<User> searchUsers(String keyword, Pageable pageable);

     @Query("""
       SELECT u FROM User u WHERE
            LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
     Page<User> searchUserByUsernameOrEmail(String keyword, Pageable pageable);

}
