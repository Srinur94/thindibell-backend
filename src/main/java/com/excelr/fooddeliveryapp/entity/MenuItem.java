package com.excelr.fooddeliveryapp.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "menuitems")
public class MenuItem implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@SequenceGenerator(name="menu_items_gen", sequenceName = "menu_items_seq", allocationSize = 1)
	private Long id;

	@Column(name="name", nullable = false)
	private String name;

	@Column(name="price", nullable = false)
	private BigDecimal price;

	@Column(name="description")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="restaurant_id")
	private Restaurant restaurant;

    private Boolean isPopular;
    
    private String category; // Biriyani, curry, Desert, Beverage
    
    private String imageUrl;
    
    @PrePersist
    public void prePersist() {
    	if(this.isPopular == null) {
    		this.isPopular = false;
    	}
    }

}
