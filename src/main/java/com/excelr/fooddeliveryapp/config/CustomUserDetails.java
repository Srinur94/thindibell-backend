package com.excelr.fooddeliveryapp.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.excelr.fooddeliveryapp.entity.User;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;
    
    public CustomUserDetails(Long id, String username, String password, 
                           Collection<? extends GrantedAuthority> authorities, boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
    }
    
    public CustomUserDetails(User user) {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
        return id;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
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
        return enabled;
    }
}