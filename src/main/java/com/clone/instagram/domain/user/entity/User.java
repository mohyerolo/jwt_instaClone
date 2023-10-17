package com.clone.instagram.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String userName;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String name;

    private String profileImgUrl;

    @Column(columnDefinition = "TEXT", length = 100)
    private String description;

    @NotNull
    @ColumnDefault("false")
    private boolean nonPublic;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @Builder
    public User(String userName, String email, String password, String name, String profileImgUrl, String description, boolean nonPublic) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.description = "";
        this.nonPublic = false;
        this.roles = Collections.singletonList("ROLE_USER");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return userName;
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
