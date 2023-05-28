package com.project.todo.models;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;
}
