package com.excelr.fooddeliveryapp.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.excelr.fooddeliveryapp.dto.DeliveryPersonRequestDTO;
import com.excelr.fooddeliveryapp.dto.DeliveryPersonResponseDTO;
import com.excelr.fooddeliveryapp.dto.LocationUpdateDTO;
import com.excelr.fooddeliveryapp.entity.DeliveryPerson;
import com.excelr.fooddeliveryapp.enums.OrderStatus;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.DeliveryPersonRepository;
import com.excelr.fooddeliveryapp.repository.OrdersRepository;
import com.excelr.fooddeliveryapp.service.DeliveryPersonService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryPersonServiceImpl implements DeliveryPersonService {
	
	private final DeliveryPersonRepository deliveryPersonRepository;
	private final OrdersRepository orderRepository;
	//private final LocationUpdateDTO locationUpdateDTO;
	
	private DeliveryPersonResponseDTO convertToDto(DeliveryPerson deliveryPerson) {
		return DeliveryPersonResponseDTO.builder()
				.id(deliveryPerson.getId())
				.name(deliveryPerson.getName())
				.phoneNumber(deliveryPerson.getPhoneNumber())
				.email(deliveryPerson.getEmail())
				.isAvailable(deliveryPerson.getIsAvailable())
				.currentLatitude(deliveryPerson.getCurrentLatitude())
				.currentLongitude(deliveryPerson.getCurrentLongitude())
				.build();
				
	}
	
	@Override
	@Transactional
	public DeliveryPersonResponseDTO createDeliveryPerson(DeliveryPersonRequestDTO deliveryPersonRequestDto) {
		DeliveryPerson deliveryPerson = DeliveryPerson.builder()
				.name(deliveryPersonRequestDto.getName())
				.phoneNumber(deliveryPersonRequestDto.getPhoneNumber())
				.email(deliveryPersonRequestDto.getEmail())
				.vehicleDetails(deliveryPersonRequestDto.getVehicleDetails())
				.isAvailable(deliveryPersonRequestDto.getIsAvailable() !=null? deliveryPersonRequestDto.getIsAvailable() : true)
				.currentLatitude(deliveryPersonRequestDto.getCurrentLatitude())
				.currentLongitude(deliveryPersonRequestDto.getCurrentLongitude())
				.build();
		
		DeliveryPerson savedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
				
		return convertToDto(savedDeliveryPerson);
	}

	

	@Override
	public DeliveryPersonResponseDTO getDeliveryPersonById(Long id) {
		DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Delivery person not found with id " + id) );
		return convertToDto(deliveryPerson);
	}

	@Override
	public List<DeliveryPersonResponseDTO> getAllDeliveryPersons() {
		return deliveryPersonRepository.findAll().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public DeliveryPersonResponseDTO updateDeliveryPerson(Long id, DeliveryPersonRequestDTO deliveryPersonRequestDto) {
		DeliveryPerson existingDeliveryPerson = deliveryPersonRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Delivery person not found with id"));
		
		existingDeliveryPerson.setName(deliveryPersonRequestDto.getName());
		existingDeliveryPerson.setPhoneNumber(deliveryPersonRequestDto.getPhoneNumber());
		existingDeliveryPerson.setEmail(deliveryPersonRequestDto.getEmail());
		existingDeliveryPerson.setIsAvailable(deliveryPersonRequestDto.getIsAvailable());
		
		if(deliveryPersonRequestDto.getCurrentLatitude() != null) {
			existingDeliveryPerson.setCurrentLatitude(deliveryPersonRequestDto.getCurrentLatitude());
		}
		
		if(deliveryPersonRequestDto.getCurrentLongitude() != null) {
			existingDeliveryPerson.setCurrentLongitude(deliveryPersonRequestDto.getCurrentLongitude());
		}
		
		DeliveryPerson updateDeliveryPerson = deliveryPersonRepository.save(existingDeliveryPerson);
		return convertToDto(updateDeliveryPerson);
	}

	@Override
	@Transactional
	public void deleteDeliveryPerson(Long id) {
	    DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Delivery Person not found with id : " + id));

	    // Check if delivery person is assigned to any active (non-final) order
	    List<OrderStatus> activeStatuses = List.of(
	            OrderStatus.PENDING,
	            OrderStatus.CONFIRMED,
	            OrderStatus.PREPARING,
	            OrderStatus.ASSIGNED,
	            OrderStatus.OUT_FOR_DELIVERY
	    );

	    boolean isAssignedToActiveOrder = orderRepository.existsByDeliveryPersonAndOrderStatusIn(deliveryPerson, activeStatuses);

	    if (isAssignedToActiveOrder) {
	        throw new IllegalStateException("Cannot delete delivery person: assigned to an active order");
	    }

	    deliveryPersonRepository.delete(deliveryPerson);
	}


	@Override
	@Transactional
	public DeliveryPersonResponseDTO updateDeliveryPersonLocation(Long id, LocationUpdateDTO locationUpdateDto) {
		
		DeliveryPerson existingDeliveryPerson = deliveryPersonRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Delivery Person not found with id " + id));
		
		if(locationUpdateDto.getLatitude() == null || locationUpdateDto.getLongitude() == null) {
			throw new IllegalArgumentException("Lattitude and Longitude cannot be null for location update");
		}
		
		existingDeliveryPerson.setCurrentLatitude(locationUpdateDto.getLatitude());
		existingDeliveryPerson.setCurrentLongitude(locationUpdateDto.getLongitude());
		
		DeliveryPerson updateDeliveryPerson= deliveryPersonRepository.save(existingDeliveryPerson);
		return convertToDto(updateDeliveryPerson);
	}

	@Override
	public List<DeliveryPersonResponseDTO> getAvailableDeliveryPersons() {
		
		
		return deliveryPersonRepository.findFirstByIsAvailableTrue().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}
	
	

}
