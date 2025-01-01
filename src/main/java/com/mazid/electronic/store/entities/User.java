package com.mazid.electronic.store.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    private String userId;

    @Column(name = "name")
    private String name;

    @Column(name = "User_email",unique = true)
    private String email;

    @Column(name = "User_password",length = 100)
    private String password;

    private String gender;

    @Column(length = 1000)
    private String about;

    @Column(name = "user_image_url")
    private String imageName;

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy = "user",fetch = FetchType.LAZY)
    private List<Order> orders= new ArrayList<>(); // one user has many orders <1:1>

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Role> roles= new ArrayList<>(); // one user has many roles <1:1>

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
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
}
