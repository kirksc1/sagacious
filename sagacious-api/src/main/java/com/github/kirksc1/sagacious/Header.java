package com.github.kirksc1.sagacious;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class Header {

    @NonNull
    private String name;
    @NonNull
    private String value;
}
