package com.smartlibrary.controller;

import com.smartlibrary.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping
    public ResponseEntity<Map<String, String>> getSettings() {
        return ResponseEntity.ok(settingsService.getAllSettings());
    }

    @PostMapping
    public ResponseEntity<?> updateSetting(
            @RequestParam("key") String key,
            @RequestParam("value") String value) {
        settingsService.updateSetting(key, value);
        return ResponseEntity.ok().body("Setting updated successfully");
    }
}
