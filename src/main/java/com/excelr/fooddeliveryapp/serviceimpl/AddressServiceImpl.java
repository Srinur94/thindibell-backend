package com.excelr.fooddeliveryapp.serviceimpl;

import com.excelr.fooddeliveryapp.dto.AddressRequestDTO;
import com.excelr.fooddeliveryapp.dto.AddressResponseDTO;
import com.excelr.fooddeliveryapp.entity.Address;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.AddressRepository;
import com.excelr.fooddeliveryapp.repository.UserRepository;
import com.excelr.fooddeliveryapp.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    private AddressResponseDTO convertToDto(Address address) {
        return AddressResponseDTO.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .name(address.getName())
                .fullAddress(address.getFullAddress())
                .doorNo(address.getDoorNo())
                .street(address.getStreet())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    private Address convertToEntity(Long userId, AddressRequestDTO dto, Address address) {
        if (address == null) {
            address = new Address();
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        address.setUser(user);
        address.setName(dto.getName());
        address.setFullAddress(dto.getFullAddress());
        address.setDoorNo(dto.getDoorNo());
        address.setStreet(dto.getStreet());
        address.setLandmark(dto.getLandmark());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());
        address.setCountry(dto.getCountry());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        return address;
    }

    @Override
    @Transactional
    public AddressResponseDTO addAddress(Long userId, AddressRequestDTO addressRequestDTO) {
        Address address = convertToEntity(userId, addressRequestDTO, null);
        Address savedAddress = addressRepository.save(address);
        return convertToDto(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        return addressRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponseDTO updateAddress(Long userId, Long addressId, AddressRequestDTO addressRequestDTO) {
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        // Ensure the address belongs to the user or is being updated by an admin
        if (!existingAddress.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address with ID " + addressId + " does not belong to user ID " + userId);
        }

        Address updatedAddress = convertToEntity(userId, addressRequestDTO, existingAddress);
        Address savedAddress = addressRepository.save(updatedAddress);
        return convertToDto(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address addressToDelete = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        // Ensure the address belongs to the user or is being deleted by an admin
        if (!addressToDelete.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address with ID " + addressId + " does not belong to user ID " + userId);
        }

        addressRepository.delete(addressToDelete);
    }
}
