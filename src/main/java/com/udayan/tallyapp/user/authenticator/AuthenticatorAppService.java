package com.udayan.tallyapp.user.authenticator;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.UserDTO;
import com.udayan.tallyapp.user.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthenticatorAppService {

    @Autowired
    UserRepository userRepository;

    private static final String ISSUER = "TallyApp";

    // Generate a new TOTP key
    public String generateKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    // Validate the TOTP code
    public boolean isValid(String secret, int code) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator(
                new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build()
        );
        return gAuth.authorize(secret, code);
    }

    // Generate a QR code URL for Google Authenticator
    public String generateQRUrl(String secret, String username) {
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                ISSUER,
                username,
                new GoogleAuthenticatorKey.Builder(secret).build());
        try {
            return generateQRBase64(url);
        } catch (Exception e) {
            log.error("",e);
            return null;
        }
    }

    // Generate a QR code image in Base64 format
    public static String generateQRBase64(String qrCodeText) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 200, 200, hintMap);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (WriterException | IOException e) {
            log.error("",e);
            return null;
        }
    }

    public UserDTO.AuthenticatorAppResponse authenticatorAppRegister(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new InvalidDataException("User not found"));
        String authenticatorAppSecret = generateKey();
        String qrCode = generateQRUrl(authenticatorAppSecret, user.getEmail());
        user.setTfaAuthenticatorSecret(authenticatorAppSecret);
        userRepository.save(user);
        return UserDTO.AuthenticatorAppResponse.builder()
                .qrCode(qrCode)
                .issuer(ISSUER)
                .user(user.getEmail())
                .build();
    }

    public ApiResponse enableTfa(UserDTO.@Valid AuthenticatorTfaRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new InvalidDataException("User not found"));

        if(user.getTfaAuthenticatorSecret()==null || user.getTfaAuthenticatorSecret().isEmpty()){
            throw new InvalidDataException("Please register your authenticator app");
        }

        boolean isValid = isValid(user.getTfaAuthenticatorSecret(), request.getCode());

        if(isValid){
            user.setTfaEnabled(true);
            user.setTfaByAuthenticator(true);
            userRepository.save(user);
            return  ApiResponse.builder()
                    .sucs(true)
                    .userDetail(user.getUsername())
                    .message("TFA is enabled using authenticator app")
                    .build();
        }else{
            return  ApiResponse.builder()
                    .sucs(false)
                    .message("Couldn't validate your TFA code")
                    .build();
        }
    }

    public ApiResponse disableAuthenticatorApp(UserDTO.AuthenticatorTfaRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new InvalidDataException("User not found"));

        boolean isValid = isValid(user.getTfaAuthenticatorSecret(), request.getCode());

        if(!isValid){
            throw new InvalidDataException("Invalid OTP");
        }

        if(user.getTfaByMobile()==false && user.getTfaByEmail()==false){
            user.setTfaEnabled(false);
        }

        user.setTfaByAuthenticator(false);
        user.setTfaAuthenticatorSecret(null);
        userRepository.save(user);
        return ApiResponse.builder()
                .sucs(true)
                .userDetail(username)
                .message("TFA by authenticator app disabled")
                .build();
    }
}
