package com.basketball.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.Dictionary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IDictionaryService extends IService<Dictionary> {

    List<Dictionary> getDictionaryByType(String dictType);

    List<Dictionary> getAllDictionary();

    Dictionary getDictionaryById(Long id);

    boolean createDictionary(Dictionary dictionary);

    boolean updateDictionary(Dictionary dictionary);

    boolean deleteDictionary(Long id);

    boolean updateDictionaryStatus(Long id, Integer status);

    Map<String, Object> getDictionaryPage(Integer current, Integer size, String dictType, Integer status, String label);
}