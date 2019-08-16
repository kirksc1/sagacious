package com.github.kirksc1.sagacious.repository;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "saga")
public class Saga {

    @Id
    private String identifier;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "sagaId")
    private List<Participant> participants = new ArrayList<>();
    private boolean failed = false;
    private boolean completed = false;

}
