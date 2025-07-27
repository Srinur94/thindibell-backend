package com.excelr.fooddeliveryapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.excelr.fooddeliveryapp.dto.DeliveryPersonRequestDTO;
import com.excelr.fooddeliveryapp.dto.DeliveryPersonResponseDTO;
import com.excelr.fooddeliveryapp.service.DeliveryPersonService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/delivery-persons")
@RequiredArgsConstructor

public class DeliveryPersonController {
	
	private final DeliveryPersonService deliveryPersonService;
	
	//create a new delivery person
	@PostMapping
	public ResponseEntity<DeliveryPersonResponseDTO> createDeliveryPerson(@Valid @RequestBody DeliveryPersonRequestDTO deliveryPersonRequestDto){
		DeliveryPersonResponseDTO createDeliveryPerson = deliveryPersonService.createDeliveryPerson(deliveryPersonRequestDto);
		
		return new ResponseEntity<>(createDeliveryPerson, HttpStatus.CREATED);
		
	}
	
	@GetMapping("/{deliveryPersonId}")
	public ResponseEntity<DeliveryPersonResponseDTO> getDeliveryPersonById(@PathVariable Long deliveryPersonId){
		DeliveryPersonResponseDTO deliveryPerson = deliveryPersonService.getDeliveryPersonById(deliveryPersonId);
		return  ResponseEntity.ok(deliveryPerson);
		
	}
	
	@GetMapping
	public ResponseEntity<List<DeliveryPersonResponseDTO>> getAllDeliveryPersond(){
		List<DeliveryPersonResponseDTO> deliveryPersons = deliveryPersonService.getAllDeliveryPersons();
		return ResponseEntity.ok(deliveryPersons);
	}
	
	//Get all Available delivery persons (Accessible by ADMIN, RESTAURANT_OWNER) for assignment
	@GetMapping("/available")
	@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER')")
	public ResponseEntity<List<DeliveryPersonResponseDTO>> getAvailableDeliveryPersons() {
	    List<DeliveryPersonResponseDTO> availableDeliveryPersons = deliveryPersonService.getAvailableDeliveryPersons();
	    return ResponseEntity.ok(availableDeliveryPersons);
	}

	

}
