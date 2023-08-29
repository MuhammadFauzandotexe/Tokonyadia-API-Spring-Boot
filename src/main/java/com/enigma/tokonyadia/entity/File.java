package com.enigma.tokonyadia.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_file")
@Inheritance(strategy = InheritanceType.JOINED)
public class File extends BaseEntity {
    private String name;
    private String contentType;
    private String path;
    private Long size;
}
