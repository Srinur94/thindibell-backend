package com.excelr.fooddeliveryapp.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="reviews")
public class Reviews implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@SequenceGenerator(name="reviews_gen", sequenceName = "reviews_seq", allocationSize = 1)
	private Long id;

	@Column(name="rating", nullable = false)
	private int rating;

	@Column(name="comments", columnDefinition = "TEXT")
	private String comments;


	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;           //More reviews on one user

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="restaurant_id")
	private Restaurant restaurant;  // More Reviews in one restaurant

}
