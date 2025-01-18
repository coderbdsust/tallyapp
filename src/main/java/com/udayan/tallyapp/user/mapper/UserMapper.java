package com.udayan.tallyapp.user.mapper;


import com.udayan.tallyapp.user.RegisteredUserResponse;
import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.address.Address;
import com.udayan.tallyapp.user.address.AddressDTO;
import com.udayan.tallyapp.user.address.AddressMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserMapper {

    @Autowired
    AddressMapper addressMapper;

    public RegisteredUserResponse toRegisteredUserResponse(User user){
        List<Address> addressList = user.getAddresses();
        List<AddressDTO.AddressResponse> addressResponseList = addressMapper.addressToAddressResponseList(addressList);

        return RegisteredUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .accountLocked(user.getAccountLocked())
                .isMobileNoVerified(user.getIsMobileNumberVerified())
                .dateOfBirth(user.getDateOfBirth())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .mobileNo(user.getMobileNo())
                .tfaEnabled(user.getTfaEnabled())
                .createdDate(user.getCreatedDate())
                .roles(user.getUserRoles())
                .addressList(addressResponseList)
                .build();
    }
}
