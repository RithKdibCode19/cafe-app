package com.example.backend.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.backend.model.SystemSettingEntity;
import com.example.backend.repository.SystemSettingRepository;


@Service
public class SystemSettingService {

    private final SystemSettingRepository repository;

    public SystemSettingService(SystemSettingRepository repository) {
        this.repository = repository;
    }

    public List<SystemSettingEntity> getAllSettings() {
        return repository.findAll();
    }

    public String getValue(String key) {
        return repository.findByKey(key)
                .map(SystemSettingEntity::getValue)
                .orElse(null);
    }

    public SystemSettingEntity updateSetting(String key, String value) {
        SystemSettingEntity setting = repository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("Setting not found: " + key));
        setting.setValue(value);
        return repository.save(setting);
    }
    
    public void updateSettings(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            SystemSettingEntity setting = repository.findByKey(entry.getKey())
                .orElse(null);
            
            if (setting != null) {
                setting.setValue(entry.getValue());
                repository.save(setting);
            } else {
                // Create new setting if it doesn't exist
                SystemSettingEntity newSetting = new SystemSettingEntity();
                newSetting.setKey(entry.getKey());
                newSetting.setValue(entry.getValue());
                newSetting.setDescription(entry.getKey().replace("_", " "));
                newSetting.setGroup("NOTIFICATIONS");
                repository.save(newSetting);
            }
        }
    }
}
