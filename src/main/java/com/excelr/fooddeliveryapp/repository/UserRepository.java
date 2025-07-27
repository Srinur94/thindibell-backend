package com.excelr.fooddeliveryapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   Optional<User> findByEmail(String email);

   List<User>findByRole(Role role);

  // Optional<User> findByEmailOrPhone(String email, String phone);
}
