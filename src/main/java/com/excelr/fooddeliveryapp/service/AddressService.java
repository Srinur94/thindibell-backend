package com.excelr.fooddeliveryapp.service;

import com.excelr.fooddeliveryapp.dto.AddressRequestDTO;
import com.excelr.fooddeliveryapp.dto.AddressResponseDTO;

import java.util.List;

public interface AddressService {
    AddressResponseDTO addAddress(Long userId, AddressRequestDTO addressRequestDTO);
    List<AddressResponseDTO> getAddressesByUserId(Long userId);
    AddressResponseDTO updateAddress(Long userId, Long addressId, AddressRequestDTO addressRequestDTO); // CRITICAL: Declare update
    void deleteAddress(Long userId, Long addressId); // CRITICAL: Declare delete
}
