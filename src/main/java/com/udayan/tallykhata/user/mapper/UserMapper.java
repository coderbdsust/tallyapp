package com.udayan.tallykhata.user.mapper;


import com.udayan.tallykhata.user.RegisteredUserResponse;
import com.udayan.tallykhata.user.User;
import com.udayan.tallykhata.user.address.Address;
import com.udayan.tallykhata.user.address.AddressDTO;
import com.udayan.tallykhata.user.address.AddressMapper;
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
        log.debug("toRegisteredUserResponse");

        List<Address> addressList = user.getAddresses();
        List<AddressDTO.AddressResponse> addressResponseList = addressMapper.addressToAddressResponseList(addressList);

        return RegisteredUserResponse.builder()
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
