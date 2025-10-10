package com.example.alert_module.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToggleRequest {
    @JsonProperty("isActived")
    private boolean isActived;
}
