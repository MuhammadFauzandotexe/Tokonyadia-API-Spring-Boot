package com.enigma.tokonyadia.entity;

import com.enigma.tokonyadia.constant.ERole;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_role")
public class Role extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ERole role;
}
