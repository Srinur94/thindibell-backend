package com.excelr.fooddeliveryapp.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.excelr.fooddeliveryapp.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name="users")
public class User implements UserDetails {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	//@SequenceGenerator(name="user_gen",sequenceName="user_SEQ",allocationSize=1)
	private Long id;

    @Column(name="name", nullable = false)
	private String name;

	@Column(unique=true, nullable=false)
	private String email;

	@Column(name="password", nullable = false)
	private String password;

	 @Enumerated(EnumType.STRING)
	 @Column(nullable=false)
	private Role role;

	 @Override
	 public Collection<? extends GrantedAuthority> getAuthorities() {
	     return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
	 }

	    @Override
	    public String getUsername() {  //email as the login username
	        return email;
	    }

	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isEnabled() {
			return true;

	    }

	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Address> addresses;
	    
	    public String getFullName() {
	        return this.name + " " + this.name;
	    }



}
