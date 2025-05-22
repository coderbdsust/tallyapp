package com.udayan.tallyapp.user.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserOTPRepository extends JpaRepository<UserOTP, UUID> {

    @Query(value = """
      select t from UserOTP t
      where (t.user.username=:param or t.user.email=:param) and t.otp=:otp and t.otpType = :otpType and t.isActive=true
      """)
    Optional<UserOTP> findActiveOTPByUserParamAndCode(String param, String otp, int otpType);

    @Query(value = """
      select t from UserOTP t
      where t.user.id=:userId and t.otp=:otp and t.otpType = :otpType and t.isActive=true
      """)
    Optional<UserOTP> findActiveOTPByUserIdAndCode(UUID userId, String otp, int otpType);

    @Modifying
    @Query(value = """
     UPDATE UserOTP t set t.isActive=false, t.updatedDate=CURRENT_TIMESTAMP
     WHERE t.user.id=:userId and t.otpType = :otpType and t.isActive=true
     """)
    void revokeAllOTPByUserIDAndOtpType(UUID userId, int otpType);

    Optional<UserOTP> findByIdAndOtp(UUID id, String otp);

}
