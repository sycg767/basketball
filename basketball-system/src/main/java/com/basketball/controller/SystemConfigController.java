package com.basketball.controller;

import com.basketball.entity.SystemConfig;
import com.basketball.service.ISystemConfigService;
import com.basketball.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system-config")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SystemConfigController {

    @Autowired
    private ISystemConfigService systemConfigService;

    @GetMapping("/key/{configKey}")
    public Result<SystemConfig> getConfigByKey(@PathVariable String configKey) {
        SystemConfig config = systemConfigService.getConfigByKey(configKey);
        if (config == null) {
            return Result.error("配置项不存在");
        }
        return Result.success(config);
    }

    @GetMapping("/group/{groupName}")
    public Result<Map<String, Object>> getGroupConfigs(@PathVariable String groupName) {
        Map<String, Object> configs = systemConfigService.getGroupConfigs(groupName);
        return Result.success(configs);
    }

    @GetMapping("/all")
    public Result<Map<String, Object>> getAllConfigs() {
        Map<String, Object> configs = systemConfigService.getAllConfigs();
        return Result.success(configs);
    }

    @GetMapping("/page")
    public Result<Map<String, Object>> getConfigPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> result = systemConfigService.getConfigPage(current, size, configKey, status);
        return Result.success(result);
    }

    @PostMapping("/create")
    public Result<SystemConfig> createConfig(@RequestBody SystemConfig config) {
        boolean success = systemConfigService.createConfig(config);
        if (success) {
            return Result.success(config);
        } else {
            return Result.error("创建配置失败");
        }
    }

    @PostMapping("/update")
    public Result<SystemConfig> updateConfig(@RequestBody SystemConfig config) {
        if (config.getId() == null) {
            return Result.error("配置ID不能为空");
        }
        boolean success = systemConfigService.updateConfig(config);
        if (success) {
            return Result.success(config);
        } else {
            return Result.error("更新配置失败");
        }
    }

    @PostMapping("/save")
    public Result<SystemConfig> saveConfig(@RequestBody Map<String, Object> configData) {
        String configKey = (String) configData.get("configKey");
        String configValue = (String) configData.get("configValue");
        Integer configType = (Integer) configData.get("configType");
        String description = (String) configData.get("description");
        String groupName = (String) configData.get("groupName");

        if (configKey == null || configValue == null || configType == null || groupName == null) {
            return Result.error("配置参数不完整");
        }

        boolean success = systemConfigService.saveConfig(configKey, configValue, configType, description, groupName);
        if (success) {
            SystemConfig config = systemConfigService.getConfigByKey(configKey);
            return Result.success(config);
        } else {
            return Result.error("保存配置失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        boolean success = systemConfigService.deleteConfig(id);
        return success ? Result.success() : Result.error("删除配置失败");
    }

    @DeleteMapping("/key/{configKey}")
    public Result<Void> deleteConfigByKey(@PathVariable String configKey) {
        boolean success = systemConfigService.deleteConfig(configKey);
        return success ? Result.success() : Result.error("删除配置失败");
    }

    @PostMapping("/status/{id}")
    public Result<Void> updateConfigStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = systemConfigService.updateConfigStatus(id, status);
        return success ? Result.success() : Result.error("更新状态失败");
    }

    @GetMapping("/groups")
    public Result<List<String>> getGroups() {
        // 获取所有配置组
        List<SystemConfig> allConfigs = systemConfigService.list();
        java.util.Set<String> groups = new java.util.HashSet<>();

        for (SystemConfig config : allConfigs) {
            if (config.getGroupName() != null) {
                groups.add(config.getGroupName());
            }
        }

        return Result.success(new java.util.ArrayList<>(groups));
    }
}