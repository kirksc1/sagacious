package com.github.kirksc1.sagacious.server;

import com.github.kirksc1.sagacious.Attribute;
import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompensatingActionDefinitionAssembler {

    public CompensatingActionDefinition toCompensatingActionDefinition(CompensatingActionDefinitionResource resource) {
        CompensatingActionDefinition retVal = null;
        if (resource != null) {
            retVal = new CompensatingActionDefinition();
            retVal.setBody(resource.getBody());
            retVal.setUri(resource.getUri());
            retVal.setHeaders(toHeaderList(resource.getHeaders()));
            retVal.setAttributes(toAttirbuteList(resource.getAttributes()));
        }
        return retVal;
    }

    private List<Header> toHeaderList(List<HeaderResource> headers) {
        List<Header> retVal = new ArrayList<>();
        if (headers != null && !headers.isEmpty()) {
            retVal.addAll(headers.stream()
                .map(header -> new Header(header.getName(), header.getValue()))
                .collect(Collectors.toList()));
        }

        return retVal;
    }

    private List<Attribute> toAttirbuteList(List<AttributeResource> attributes) {
        List<Attribute> retVal = new ArrayList<>();
        if (attributes != null && !attributes.isEmpty()) {
            retVal.addAll(attributes.stream()
                    .map(attribute -> new Attribute(attribute.getName(), attribute.getValue()))
                    .collect(Collectors.toList()));
        }

        return retVal;
    }
}
