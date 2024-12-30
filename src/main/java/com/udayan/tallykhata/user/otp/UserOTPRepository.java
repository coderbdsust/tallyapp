package com.udayan.tallykhata.user.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOTPRepository extends JpaRepository<UserOTP, Long> {

    @Query(value = """
      select t from UserOTP t\s
      where t.user.id=:userId and t.otp=:otp and t.otpType = :otpType and t.isActive=true\s
      """)
    Optional<UserOTP> findActiveOTPByUserIdAndCode(long userId, String otp, int otpType);

}
