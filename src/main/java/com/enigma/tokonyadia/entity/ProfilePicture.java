package com.enigma.tokonyadia.entity;


import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "m_profile_picture")
public class ProfilePicture extends File {
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserCredential user;
}
