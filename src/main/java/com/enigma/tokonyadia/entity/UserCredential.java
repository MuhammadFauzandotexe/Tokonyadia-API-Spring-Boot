package com.enigma.tokonyadia.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_user_credential")
public class UserCredential extends BaseEntity{
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "password")
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m_user_role",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            ))
    private Set<Role> roles;
    @OneToOne(mappedBy = "user")
    private ProfilePicture profilePicture;

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void addRole(Role role) {
        roles.add(role);
    }
}
