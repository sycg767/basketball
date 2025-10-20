package com.basketball.controller;

import com.basketball.entity.Dictionary;
import com.basketball.service.IDictionaryService;
import com.basketball.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dictionary")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DictionaryController {

    @Autowired
    private IDictionaryService dictionaryService;

    @GetMapping("/type/{dictType}")
    public Result<List<Dictionary>> getDictionaryByType(@PathVariable String dictType) {
        List<Dictionary> dictionaries = dictionaryService.getDictionaryByType(dictType);
        return Result.success(dictionaries);
    }

    @GetMapping("/all")
    public Result<List<Dictionary>> getAllDictionary() {
        List<Dictionary> dictionaries = dictionaryService.getAllDictionary();
        return Result.success(dictionaries);
    }

    @GetMapping("/{id}")
    public Result<Dictionary> getDictionaryById(@PathVariable Long id) {
        Dictionary dictionary = dictionaryService.getDictionaryById(id);
        if (dictionary == null) {
            return Result.error("字典项不存在");
        }
        return Result.success(dictionary);
    }

    @GetMapping("/page")
    public Result<Map<String, Object>> getDictionaryPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String label) {
        Map<String, Object> result = dictionaryService.getDictionaryPage(current, size, dictType, status, label);
        return Result.success(result);
    }

    @PostMapping("/create")
    public Result<Dictionary> createDictionary(@RequestBody Dictionary dictionary) {
        boolean success = dictionaryService.createDictionary(dictionary);
        if (success) {
            return Result.success(dictionary);
        } else {
            return Result.error("创建字典项失败");
        }
    }

    @PostMapping("/update")
    public Result<Dictionary> updateDictionary(@RequestBody Dictionary dictionary) {
        if (dictionary.getId() == null) {
            return Result.error("字典ID不能为空");
        }
        boolean success = dictionaryService.updateDictionary(dictionary);
        if (success) {
            return Result.success(dictionary);
        } else {
            return Result.error("更新字典项失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDictionary(@PathVariable Long id) {
        boolean success = dictionaryService.deleteDictionary(id);
        return success ? Result.success() : Result.error("删除字典项失败");
    }

    @PostMapping("/status/{id}")
    public Result<Void> updateDictionaryStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = dictionaryService.updateDictionaryStatus(id, status);
        return success ? Result.success() : Result.error("更新状态失败");
    }

    @GetMapping("/types")
    public Result<Map<String, List<Dictionary>>> getDictionaryTypes() {
        // 获取所有字典类型
        List<Dictionary> allDictionaries = dictionaryService.getAllDictionary();

        // 按类型分组
        Map<String, List<Dictionary>> groupedDictionaries = new java.util.HashMap<>();

        for (Dictionary dict : allDictionaries) {
            groupedDictionaries.computeIfAbsent(dict.getDictType(), k -> new java.util.ArrayList<>()).add(dict);
        }

        return Result.success(groupedDictionaries);
    }
}