package com.excelr.fooddeliveryapp.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.excelr.fooddeliveryapp.enums.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="deliverystatus")
public class DeliveryStatus implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@SequenceGenerator(name="delivery_status_gen", sequenceName = "delivery_status_gen",allocationSize = 1)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name="order_id")
	private Order order;

	   @Enumerated(EnumType.STRING)
	   @Column(nullable = false)
	    private OrderStatus status;

	    @Column(nullable = false)
	    private LocalDateTime updatedAt;


}
