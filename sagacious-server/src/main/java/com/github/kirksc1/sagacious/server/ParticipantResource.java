package com.github.kirksc1.sagacious.server;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ParticipantResource {

    @NotNull
    private String identifier;
    @Valid
    @NotNull
    private CompensatingActionDefinitionResource action;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public CompensatingActionDefinitionResource getAction() {
        return action;
    }

    public void setAction(CompensatingActionDefinitionResource action) {
        this.action = action;
    }
}
