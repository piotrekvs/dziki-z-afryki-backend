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
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique=true)
    @NotNull
    private String email;

    @Column
    @NotNull
    private String firstname;

    @Column
    @NotNull
    private String lastname;

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

    @ManyToMany
    @JoinTable(
            name = "courses_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    Set<CourseEntity> userCourses;

    @ManyToMany
    @JoinTable(
            name = "dates_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "date_id")
    )
    Set<DateEntity> userDates;

    public UserEntity(
            String email,
            String firstname,
            String lastname,
            UserRole role,
            Integer indexNumber,
            Boolean isVerified,
            String hashedPassword
    ) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
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
