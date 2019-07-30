package com.github.kirksc1.sagacious;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompensatingActionDefinition {

    private List<Attribute> attributes = new ArrayList<>();
    private List<Header> headers = new ArrayList<>();
    private String body;
    private String uri;
    private String method;

}
