package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements OAuth2User, UserDetails  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    @Column(unique = true, nullable = false)
    private String userName;
    @Column(unique = true, nullable = false)
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole role;


    /**OAuth 관련**/
    private String oauthId;
    private String name;
    @Transient  //jpa에서 Column으로 쓰이지 않음
    private Map<String, Object> attributes; // oauthId, name, email


    public User update(String userName, String email){
        this.userName = userName;
        this.email = email;
        return this;
    }


    /**UserDetails**/
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getRole().name()));
    }
    @Override
    public String getUsername() {
        return this.userName;
    }
    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return this.password;
    }




    /**OAuth2User**/
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
    @Override
    public String getName() {
        return this.userName;
    }
}
