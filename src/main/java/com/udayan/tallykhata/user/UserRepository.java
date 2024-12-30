package com.udayan.tallykhata.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

     @Query("SELECT u FROM User u WHERE u.enabled=true and (u.username = :param OR u.email = :param OR u.mobileNo = :param)")
     Optional<User> findByUsernameOrEmailOrMobileNo(@Param("param") String param);
     Optional<User> findByEmail(String email);
     Optional<User> findByUsername(String username);
     @Query("SELECT u FROM User u WHERE u.username = :param OR u.email = :param")
     Optional<User> findByUsernameOrEmail(@Param("param") String param);
     Optional<User> findByMobileNo(String mobileNo);
     Optional<User> findByEmailOrMobileNo(String email, String mobileNo);

}
