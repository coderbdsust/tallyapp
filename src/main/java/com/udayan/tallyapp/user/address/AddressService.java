package com.udayan.tallykhata.user.address;

import com.udayan.tallykhata.user.User;
import com.udayan.tallykhata.customexp.InvalidDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
