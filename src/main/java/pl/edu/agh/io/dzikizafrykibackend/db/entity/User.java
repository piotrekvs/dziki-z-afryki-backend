package pl.edu.agh.io.dzikizafrykibackend.db.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_account")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique=true)
    @NotNull
    private String email;

    @Column
    @NotNull
    private String firstName;

    @Column
    @NotNull
    private String lastName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRole role;

    @Column(unique=true)
    private Integer indexNumber;

    @Column
    @NotNull
    private Boolean isVerified;

    @Column
    @NotNull
    private String hashedPassword;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users")
    Set<CourseEntity> userCourses;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "dates_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "date_id")
    )
    Set<DateEntity> userDates;

    public User(
            String email,
            String firstName,
            String lastName,
            UserRole role,
            Integer indexNumber,
            Boolean isVerified,
            String hashedPassword
    ) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.indexNumber = indexNumber;
        this.isVerified = isVerified;
        this.hashedPassword = hashedPassword;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Here a full role name is required, to force spring to not interpret it as an Authority
        return List.of(new SimpleGrantedAuthority(role.getRoleName()));
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
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
        return isVerified;
    }
}
