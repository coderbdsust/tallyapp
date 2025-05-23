package com.udayan.tallyapp.user.address;

import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    public AddressDTO.AddressResponse saveOrUpdateAddress(AddressDTO.AddressRequest addressRequest, User user) {
        Address address = addressMapper.dtoToAddress(addressRequest, user);
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toAddressResponseDTO(savedAddress);
    }

    public List<AddressDTO.AddressResponse> saveAddressList(ArrayList<AddressDTO.AddressRequest> addressList, User user) throws InvalidDataException {
        if (addressList == null || addressList.isEmpty()) {
            throw new InvalidDataException("Address list is empty");
        }
        List<Address> addresses = addressMapper.requestDTOListToAddressList(addressList, user);
        List<Address> saveAddressList = addressRepository.saveAll(addresses);
        return addressMapper.addressToAddressResponseList(saveAddressList);
    }

    public List<AddressDTO.AddressResponse> getAddressResponseList(User user) {
        ArrayList<Address> addressList = addressRepository.findByUser(user);
        return addressMapper.addressToAddressResponseList(addressList);
    }

    public ApiResponse deleteAddress(UUID id, User currentUser) {

        addressRepository.deleteById(id);

        return ApiResponse
                .builder()
                .sucs(true)
                .businessCode(ApiResponse.BusinessCode.OK.getValue())
                .message("Address deleted successfully")
                .build();
    }
}
