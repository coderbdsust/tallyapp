package com.udayan.tallyapp.user.mapper;


import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.UserDTO;
import com.udayan.tallyapp.user.address.Address;
import com.udayan.tallyapp.user.address.AddressDTO;
import com.udayan.tallyapp.user.address.AddressMapper;
import com.udayan.tallyapp.user.profile.ShortProfile;
import com.udayan.tallyapp.user.profile.ShortProfileDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserMapper {

    @Autowired
    AddressMapper addressMapper;

    public ShortProfileDTO.ShortProfileResponse shortProfileToResponse(ShortProfile shortProfile){
        return ShortProfileDTO.ShortProfileResponse.builder()
                .designation(shortProfile.getDesignation())
                .skills(shortProfile.getSkills())
                .companyName(shortProfile.getCompanyName())
                .id(shortProfile.getId())
                .build();
    }

    public UserDTO.RegisteredUserResponse toRegisteredUserResponse(User user){
        List<Address> addressList = user.getAddresses();

        List<AddressDTO.AddressResponse> addressResponseList = addressMapper.addressToAddressResponseList(addressList);

        List<ShortProfile> shortProfiles = user.getShortProfiles();

        List<ShortProfileDTO.ShortProfileResponse> shortProfileResponses
                = shortProfiles.stream().map(this::shortProfileToResponse).toList();

        return UserDTO.RegisteredUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .accountLocked(user.getAccountLocked())
                .isMobileNoVerified(user.getIsMobileNumberVerified())
                .dateOfBirth(user.getDateOfBirth())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .username(user.getUsername())
                .mobileNo(user.getMobileNo())
                .tfaEnabled(user.getTfaEnabled())
                .createdDate(user.getCreatedDate())
                .roles(user.getUserRoles())
                .addressList(addressResponseList)
                .shortProfileList(shortProfileResponses)
                .build();
    }
}
