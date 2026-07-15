package com.smartlibrary.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "settings")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "settings_key", nullable = false, unique = true, length = 100)
    private String key;

    @Column(name = "settings_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Constructors
    public Setting() {
    }

    public Setting(Long id, String key, String value, String description) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    // Builder
    public static SettingBuilder builder() {
        return new SettingBuilder();
    }

    public static class SettingBuilder {
        private Long id;
        private String key;
        private String value;
        private String description;

        SettingBuilder() {
        }

        public SettingBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SettingBuilder key(String key) {
            this.key = key;
            return this;
        }

        public SettingBuilder value(String value) {
            this.value = value;
            return this;
        }

        public SettingBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Setting build() {
            return new Setting(id, key, value, description);
        }
    }
}
