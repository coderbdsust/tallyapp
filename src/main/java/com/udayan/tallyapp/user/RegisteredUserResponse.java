package com.udayan.tallyapp.user;


import com.udayan.tallyapp.user.address.AddressDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RegisteredUserResponse {
    private UUID id;
    private String username;
    private String email;
    private String mobileNo;
    private String fullName;
    private LocalDate dateOfBirth;
    private boolean enabled;
    private boolean accountLocked;
    private boolean isMobileNoVerified;
    private boolean tfaEnabled;
    private LocalDateTime createdDate;
    private ArrayList<String> roles;
    private List<AddressDTO.AddressResponse> addressList;
}
