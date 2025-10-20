package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.SystemConfig;
import com.basketball.mapper.SystemConfigMapper;
import com.basketball.service.ISystemConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements ISystemConfigService {

    @Override
    public SystemConfig getConfigByKey(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        return getOne(wrapper);
    }

    @Override
    public Map<String, Object> getGroupConfigs(String groupName) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getGroupName, groupName)
                .orderByAsc(SystemConfig::getSortOrder)
                .orderByAsc(SystemConfig::getId);

        List<SystemConfig> configs = list(wrapper);
        Map<String, Object> result = new HashMap<>();

        for (SystemConfig config : configs) {
            Object value = convertConfigValue(config.getConfigValue(), config.getConfigType());
            result.put(config.getConfigKey(), value);
        }

        return result;
    }

    @Override
    public boolean saveConfig(String configKey, String configValue, Integer configType, String description, String groupName) {
        // 检查配置键是否已存在
        SystemConfig existingConfig = getConfigByKey(configKey);
        if (existingConfig != null) {
            return false;
        }

        SystemConfig config = new SystemConfig();
        config.setConfigKey(configKey);
        config.setConfigValue(configValue);
        config.setConfigType(configType);
        config.setDescription(description);
        config.setGroupName(groupName);
        config.setIsEditable(1);
        config.setSortOrder(0);

        return save(config);
    }

    @Override
    public boolean updateConfig(String configKey, String configValue, String description) {
        SystemConfig config = getConfigByKey(configKey);
        if (config == null) {
            return false;
        }

        config.setConfigValue(configValue);
        if (description != null && !description.trim().isEmpty()) {
            config.setDescription(description);
        }

        return updateById(config);
    }

    @Override
    public boolean deleteConfig(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        return remove(wrapper);
    }

    @Override
    public List<SystemConfig> getConfigsByGroup(String groupName) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getGroupName, groupName)
                .orderByAsc(SystemConfig::getSortOrder)
                .orderByAsc(SystemConfig::getId);
        return list(wrapper);
    }

    @Override
    public Map<String, Object> getAllConfigs() {
        List<SystemConfig> configs = list();
        Map<String, Object> result = new HashMap<>();

        for (SystemConfig config : configs) {
            Object value = convertConfigValue(config.getConfigValue(), config.getConfigType());
            result.put(config.getConfigKey(), value);
        }

        return result;
    }

    @Override
    public boolean createConfig(SystemConfig config) {
        config.setId(null);
        return save(config);
    }

    @Override
    public boolean updateConfig(SystemConfig config) {
        if (config.getId() == null) {
            return false;
        }
        return updateById(config);
    }

    @Override
    public boolean deleteConfig(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateConfigStatus(Long id, Integer status) {
        // 由于数据库中没有status字段，此方法暂时不可用
        return false;
    }

    @Override
    public Map<String, Object> getConfigPage(Integer current, Integer size, String configKey, Integer status) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();

        if (configKey != null && !configKey.trim().isEmpty()) {
            wrapper.like(SystemConfig::getConfigKey, configKey);
        }
        // 由于数据库中没有status字段，暂时忽略status参数

        wrapper.orderByAsc(SystemConfig::getGroupName)
               .orderByAsc(SystemConfig::getSortOrder)
               .orderByAsc(SystemConfig::getId);

        Page<SystemConfig> page = new Page<>(current, size);
        Page<SystemConfig> resultPage = page(page, wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("records", resultPage.getRecords());
        result.put("total", resultPage.getTotal());
        result.put("size", resultPage.getSize());
        result.put("current", resultPage.getCurrent());
        result.put("pages", resultPage.getPages());

        return result;
    }

    private Object convertConfigValue(String configValue, Integer configType) {
        if (configValue == null || configValue.trim().isEmpty()) {
            return null;
        }

        try {
            switch (configType) {
                case 1: // 字符串
                    return configValue;
                case 2: // 数字
                    return Long.parseLong(configValue);
                case 3: // 布尔
                    return Boolean.parseBoolean(configValue);
                case 4: // JSON
                    return new com.fasterxml.jackson.databind.ObjectMapper().readValue(configValue, Object.class);
                default:
                    return configValue;
            }
        } catch (Exception e) {
            return configValue; // 转换失败返回原值
        }
    }
}