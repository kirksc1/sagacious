package com.github.kirksc1.sagacious.repository;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "participant")
public class Participant {

    @Id
    private String identifier;
    private String actionDefinition;
    private Boolean failCompleted;
    private Integer orderIndex;

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setActionDefinition(String actionDefinition) {
        this.actionDefinition = actionDefinition;
    }

    public void setFailCompleted(Boolean failCompleted) {
        this.failCompleted = failCompleted;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
