package com.excelr.fooddeliveryapp.service;

import java.util.List;

import com.excelr.fooddeliveryapp.dto.DeliveryPersonRequestDTO;
import com.excelr.fooddeliveryapp.dto.DeliveryPersonResponseDTO;
import com.excelr.fooddeliveryapp.dto.DeliveryStatusDTO;
import com.excelr.fooddeliveryapp.dto.LocationUpdateDTO;
import com.excelr.fooddeliveryapp.entity.DeliveryStatus;

public interface DeliveryPersonService {

	DeliveryPersonResponseDTO createDeliveryPerson(DeliveryPersonRequestDTO deliveryPersonRequestDto);
	
	DeliveryPersonResponseDTO getDeliveryPersonById(Long id);
	
	List<DeliveryPersonResponseDTO> getAllDeliveryPersons();
	
	DeliveryPersonResponseDTO updateDeliveryPerson(Long id, DeliveryPersonRequestDTO deliveryPersonRequestDto);
	
	void deleteDeliveryPerson(Long id);
	
	DeliveryPersonResponseDTO updateDeliveryPersonLocation(Long id, LocationUpdateDTO locationUpdateDto);
	
	List<DeliveryPersonResponseDTO> getAvailableDeliveryPersons();
	

//	DeliveryPersonResponseDTO markAvailable(Long deliveryPersonId);
//
//	DeliveryStatusDTO updateOrderDeliveryStatus(Long orderId, DeliveryStatus status);

	
}
