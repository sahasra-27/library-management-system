package com.smartlibrary.dto;

import jakarta.validation.constraints.NotBlank;

public class PublisherDto {
    private Long id;

    @NotBlank(message = "Publisher name is required")
    private String name;

    private String address;
    private String phone;

    // Constructors
    public PublisherDto() {
    }

    public PublisherDto(Long id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
