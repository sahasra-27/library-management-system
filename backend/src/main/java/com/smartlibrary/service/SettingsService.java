package com.smartlibrary.service;

import com.smartlibrary.entity.Setting;
import com.smartlibrary.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    private SettingRepository settingRepository;

    public String getSettingValue(String key, String defaultValue) {
        Optional<Setting> setting = settingRepository.findByKey(key);
        if (setting.isPresent()) {
            return setting.get().getValue();
        }
        // Initialize default if not exists
        Setting newSetting = Setting.builder()
                .key(key)
                .value(defaultValue)
                .description("Auto-initialized setting")
                .build();
        settingRepository.save(newSetting);
        return defaultValue;
    }

    public BigDecimal getFineAmount() {
        String val = getSettingValue("fine_amount", "5");
        return new BigDecimal(val);
    }

    public int getBorrowDurationDays() {
        String val = getSettingValue("borrow_duration", "14");
        return Integer.parseInt(val);
    }

    public int getMaxBooksLimit() {
        String val = getSettingValue("max_books", "5");
        return Integer.parseInt(val);
    }

    public Map<String, String> getAllSettings() {
        // Ensure defaults exist
        getSettingValue("library_name", "BookVerse");
        getSettingValue("library_address", "123 Library Road, Campus");
        getSettingValue("email", "info@smartlibrary.com");
        getSettingValue("phone", "+91-9876543210");
        getSettingValue("fine_amount", "5");
        getSettingValue("borrow_duration", "14");
        getSettingValue("max_books", "5");

        List<Setting> settings = settingRepository.findAll();
        Map<String, String> map = new HashMap<>();
        for (Setting s : settings) {
            map.put(s.getKey(), s.getValue());
        }
        return map;
    }

    public void updateSetting(String key, String value) {
        Setting s = settingRepository.findByKey(key)
                .orElse(Setting.builder().key(key).description("Custom Setting").build());
        s.setValue(value);
        settingRepository.save(s);
    }
}
