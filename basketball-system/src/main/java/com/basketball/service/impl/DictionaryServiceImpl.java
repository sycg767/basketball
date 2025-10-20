package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.Dictionary;
import com.basketball.mapper.DictionaryMapper;
import com.basketball.service.IDictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements IDictionaryService {

    @Override
    public List<Dictionary> getDictionaryByType(String dictType) {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dictionary::getDictType, dictType)
                .orderByAsc(Dictionary::getDictSort)
                .orderByAsc(Dictionary::getId);
        return list(wrapper);
    }

    @Override
    public List<Dictionary> getAllDictionary() {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Dictionary::getDictType)
                .orderByAsc(Dictionary::getDictSort)
                .orderByAsc(Dictionary::getId);
        return list(wrapper);
    }

    @Override
    public Dictionary getDictionaryById(Long id) {
        return getById(id);
    }

    @Override
    public boolean createDictionary(Dictionary dictionary) {
        dictionary.setId(null);
        dictionary.setStatus(1);
        return save(dictionary);
    }

    @Override
    public boolean updateDictionary(Dictionary dictionary) {
        return updateById(dictionary);
    }

    @Override
    public boolean deleteDictionary(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateDictionaryStatus(Long id, Integer status) {
        Dictionary dictionary = new Dictionary();
        dictionary.setId(id);
        dictionary.setStatus(status);
        return updateById(dictionary);
    }

    @Override
    public Map<String, Object> getDictionaryPage(Integer current, Integer size, String dictType, Integer status, String label) {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();

        if (dictType != null && !dictType.trim().isEmpty()) {
            wrapper.eq(Dictionary::getDictType, dictType);
        }
        if (status != null) {
            wrapper.eq(Dictionary::getStatus, status);
        }
        if (label != null && !label.trim().isEmpty()) {
            wrapper.like(Dictionary::getDictLabel, label);
        }

        wrapper.orderByAsc(Dictionary::getDictType)
               .orderByAsc(Dictionary::getDictSort)
               .orderByAsc(Dictionary::getId);

        Page<Dictionary> page = new Page<>(current, size);
        Page<Dictionary> resultPage = page(page, wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("records", resultPage.getRecords());
        result.put("total", resultPage.getTotal());
        result.put("size", resultPage.getSize());
        result.put("current", resultPage.getCurrent());
        result.put("pages", resultPage.getPages());

        return result;
    }
}