package com.smartlibrary.dto;

import jakarta.validation.constraints.NotBlank;

public class SettingDto {
    @NotBlank(message = "Key cannot be empty")
    private String key;

    @NotBlank(message = "Value cannot be empty")
    private String value;

    private String description;

    // Constructors
    public SettingDto() {
    }

    public SettingDto(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
