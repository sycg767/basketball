package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.exception.ErrorCode;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.VenueCreateDTO;
import com.basketball.dto.request.VenuePriceDTO;
import com.basketball.dto.request.VenueQueryDTO;
import com.basketball.dto.request.VenueUpdateDTO;
import com.basketball.entity.Venue;
import com.basketball.entity.VenuePrice;
import com.basketball.mapper.VenueMapper;
import com.basketball.mapper.VenuePriceMapper;
import com.basketball.service.IVenueService;
import com.basketball.vo.VenuePriceVO;
import com.basketball.vo.VenueVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 场地服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Service
public class VenueServiceImpl extends ServiceImpl<VenueMapper, Venue> implements IVenueService {

    @Resource
    private VenueMapper venueMapper;

    @Resource
    private VenuePriceMapper venuePriceMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建场地
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createVenue(VenueCreateDTO createDTO) {
        // 1. 校验场地编码是否已存在
        LambdaQueryWrapper<Venue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Venue::getVenueCode, createDTO.getVenueCode());
        if (venueMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(400, "场地编码已存在");
        }

        // 2. 创建场地
        Venue venue = new Venue();
        BeanUtils.copyProperties(createDTO, venue);
        venue.setStatus(1); // 默认可用
        venue.setCreateTime(LocalDateTime.now());
        venue.setUpdateTime(LocalDateTime.now());

        venueMapper.insert(venue);
    }

    /**
     * 更新场地
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVenue(Long id, VenueUpdateDTO updateDTO) {
        // 1. 查询场地是否存在
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            throw new BusinessException(404, "场地不存在");
        }

        // 2. 更新场地信息
        if (StringUtils.isNotBlank(updateDTO.getVenueName())) {
            venue.setVenueName(updateDTO.getVenueName());
        }
        if (updateDTO.getVenueType() != null) {
            venue.setVenueType(updateDTO.getVenueType());
        }
        if (updateDTO.getArea() != null) {
            venue.setArea(updateDTO.getArea());
        }
        if (updateDTO.getCapacity() != null) {
            venue.setCapacity(updateDTO.getCapacity());
        }
        if (StringUtils.isNotBlank(updateDTO.getFloorType())) {
            venue.setFloorType(updateDTO.getFloorType());
        }
        if (StringUtils.isNotBlank(updateDTO.getFacilities())) {
            venue.setFacilities(updateDTO.getFacilities());
        }
        if (updateDTO.getStatus() != null) {
            venue.setStatus(updateDTO.getStatus());
        }
        if (StringUtils.isNotBlank(updateDTO.getImageUrl())) {
            venue.setImageUrl(updateDTO.getImageUrl());
        }
        if (StringUtils.isNotBlank(updateDTO.getImages())) {
            venue.setImages(updateDTO.getImages());
        }
        if (StringUtils.isNotBlank(updateDTO.getDescription())) {
            venue.setDescription(updateDTO.getDescription());
        }
        if (StringUtils.isNotBlank(updateDTO.getLocation())) {
            venue.setLocation(updateDTO.getLocation());
        }
        if (updateDTO.getSortOrder() != null) {
            venue.setSortOrder(updateDTO.getSortOrder());
        }

        venue.setUpdateTime(LocalDateTime.now());
        venueMapper.updateById(venue);
    }

    /**
     * 删除场地
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVenue(Long id) {
        // 1. 查询场地是否存在
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            throw new BusinessException(404, "场地不存在");
        }

        // 2. TODO: 检查是否有未完成的预订

        // 3. 删除场地
        venueMapper.deleteById(id);

        // 4. 删除关联的价格配置
        LambdaQueryWrapper<VenuePrice> priceWrapper = new LambdaQueryWrapper<>();
        priceWrapper.eq(VenuePrice::getVenueId, id);
        venuePriceMapper.delete(priceWrapper);
    }

    /**
     * 获取场地详情
     */
    @Override
    public VenueVO getVenueById(Long id) {
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            throw new BusinessException(404, "场地不存在");
        }
        return convertToVO(venue);
    }

    /**
     * 场地列表（分页）
     */
    @Override
    public PageResult<VenueVO> listVenues(VenueQueryDTO queryDTO) {
        // 1. 构建分页对象
        Page<Venue> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Venue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(StringUtils.isNotBlank(queryDTO.getKeyword()), wrapper ->
                        wrapper.like(Venue::getVenueName, queryDTO.getKeyword())
                                .or()
                                .like(Venue::getVenueCode, queryDTO.getKeyword()))
                .eq(StringUtils.isNotBlank(queryDTO.getVenueType()), Venue::getVenueType, queryDTO.getVenueType())
                .eq(queryDTO.getStatus() != null, Venue::getStatus, queryDTO.getStatus())
                .orderByAsc(Venue::getSortOrder)
                .orderByDesc(Venue::getCreateTime);

        // 3. 执行查询
        Page<Venue> venuePage = venueMapper.selectPage(page, queryWrapper);

        // 4. 转换为VO
        List<VenueVO> voList = venuePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 5. 返回分页结果
        return PageResult.<VenueVO>builder()
                .records(voList)
                .total(venuePage.getTotal())
                .pages(venuePage.getPages())
                .current(venuePage.getCurrent())
                .size(venuePage.getSize())
                .build();
    }

    /**
     * 查询可用场地
     */
    @Override
    public List<VenueVO> listAvailableVenues() {
        LambdaQueryWrapper<Venue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Venue::getStatus, 1)
                .orderByAsc(Venue::getSortOrder);

        List<Venue> venues = venueMapper.selectList(queryWrapper);
        return venues.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 设置场地价格
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setVenuePrice(VenuePriceDTO priceDTO) {
        // 1. 校验场地是否存在
        Venue venue = venueMapper.selectById(priceDTO.getVenueId());
        if (venue == null) {
            throw new BusinessException(404, "场地不存在");
        }

        // 2. 创建价格配置
        VenuePrice venuePrice = new VenuePrice();
        BeanUtils.copyProperties(priceDTO, venuePrice);
        venuePrice.setStatus(1); // 默认启用
        venuePrice.setCreateTime(LocalDateTime.now());
        venuePrice.setUpdateTime(LocalDateTime.now());

        venuePriceMapper.insert(venuePrice);
    }

    /**
     * 获取场地价格列表
     */
    @Override
    public List<VenuePriceVO> getVenuePrices(Long venueId) {
        LambdaQueryWrapper<VenuePrice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VenuePrice::getVenueId, venueId)
                .eq(VenuePrice::getStatus, 1)
                .orderByAsc(VenuePrice::getStartTime);

        List<VenuePrice> prices = venuePriceMapper.selectList(queryWrapper);
        return prices.stream()
                .map(this::convertToPriceVO)
                .collect(Collectors.toList());
    }

    /**
     * 计算预订价格
     */
    @Override
    public BigDecimal calculatePrice(Long venueId, String startTime, String endTime, Integer memberLevel) {
        // 1. 获取场地价格配置
        List<VenuePriceVO> prices = getVenuePrices(venueId);
        if (prices.isEmpty()) {
            throw new BusinessException(400, "场地未配置价格");
        }

        // 2. 解析时间
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        // 3. 计算时长（小时）
        long hours = java.time.Duration.between(start, end).toHours();
        if (hours <= 0) {
            throw new BusinessException(400, "结束时间必须大于开始时间");
        }

        // 4. 根据时段计算价格（简化版本，取第一个价格配置）
        VenuePriceVO priceVO = prices.get(0);
        BigDecimal hourlyPrice = (memberLevel != null && memberLevel > 0)
                ? priceVO.getMemberPrice()
                : priceVO.getPrice();

        // 5. 计算总价
        return hourlyPrice.multiply(BigDecimal.valueOf(hours));
    }

    /**
     * 转换为VO对象
     */
    private VenueVO convertToVO(Venue venue) {
        VenueVO vo = new VenueVO();
        BeanUtils.copyProperties(venue, vo);

        // 处理图片列表 - images字段存储的是JSON数组格式
        if (StringUtils.isNotBlank(venue.getImages())) {
            try {
                // 尝试解析JSON数组
                List<String> imageList = objectMapper.readValue(
                    venue.getImages(),
                    new TypeReference<List<String>>() {}
                );
                vo.setImageList(imageList);
            } catch (Exception e) {
                // 如果JSON解析失败,尝试按逗号分隔(向后兼容)
                vo.setImageList(Arrays.asList(venue.getImages().split(",")));
            }
        } else if (StringUtils.isNotBlank(venue.getImageUrl())) {
            // 如果没有images但有imageUrl,使用imageUrl作为图片列表
            vo.setImageList(Arrays.asList(venue.getImageUrl()));
        } else {
            // 如果都没有,设置为空列表
            vo.setImageList(new ArrayList<>());
        }

        // 获取基础价格(工作日价格作为基础价格)
        LambdaQueryWrapper<VenuePrice> priceWrapper = new LambdaQueryWrapper<>();
        priceWrapper.eq(VenuePrice::getVenueId, venue.getId())
                .eq(VenuePrice::getTimeType, 1) // 工作日
                .eq(VenuePrice::getStatus, 1)
                .orderByAsc(VenuePrice::getStartTime)
                .last("LIMIT 1");
        VenuePrice venuePrice = venuePriceMapper.selectOne(priceWrapper);
        if (venuePrice != null) {
            vo.setBasePrice(venuePrice.getPrice());
        } else {
            // 如果没有配置价格,设置默认值
            vo.setBasePrice(BigDecimal.ZERO);
        }

        return vo;
    }

    /**
     * 转换为价格VO
     */
    private VenuePriceVO convertToPriceVO(VenuePrice price) {
        VenuePriceVO vo = new VenuePriceVO();
        BeanUtils.copyProperties(price, vo);
        return vo;
    }
}
