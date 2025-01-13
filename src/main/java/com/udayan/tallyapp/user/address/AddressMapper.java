package com.udayan.tallyapp.user.address;

import com.udayan.tallyapp.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class AddressMapper {

    public Address dtoToAddress(AddressDTO.AddressRequest request, User user) {
        Address address = new Address();
        if (request.getId() != null) {
            address.setId(request.getId());
            address.setUpdatedDate(LocalDateTime.now());
        }

        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostCode(request.getPostCode());
        address.setAddressLine(request.getAddressLine());
        address.setUser(user);
        return address;
    }

    public List<Address> requestDTOListToAddressList(ArrayList<AddressDTO.AddressRequest> addressList, User user) {
        return addressList.stream().map(m -> dtoToAddress(m, user)).collect(Collectors.toList());
    }

    public List<AddressDTO.AddressResponse> addressToAddressResponseList(List<Address> addressList) {
        return addressList.stream().map(this::toAddressResponseDTO).collect(Collectors.toList());
    }

    public AddressDTO.AddressResponse toAddressResponseDTO(Address address) {
        return AddressDTO.AddressResponse.builder()
                .id(address.getId())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postCode(address.getPostCode())
                .build();

    }
}
