package com.basketball.service;

import com.basketball.entity.SystemConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ISystemConfigService extends IService<SystemConfig> {

    SystemConfig getConfigByKey(String configKey);

    Map<String, Object> getGroupConfigs(String groupName);

    boolean saveConfig(String configKey, String configValue, Integer configType, String description, String groupName);

    boolean updateConfig(String configKey, String configValue, String description);

    boolean deleteConfig(String configKey);

    List<SystemConfig> getConfigsByGroup(String groupName);

    Map<String, Object> getAllConfigs();

    boolean createConfig(SystemConfig config);

    boolean updateConfig(SystemConfig config);

    boolean deleteConfig(Long id);

    boolean updateConfigStatus(Long id, Integer status);

    Map<String, Object> getConfigPage(Integer current, Integer size, String configKey, Integer status);
}