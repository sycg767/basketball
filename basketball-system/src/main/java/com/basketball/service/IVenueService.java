package com.basketball.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.VenueCreateDTO;
import com.basketball.dto.request.VenuePriceDTO;
import com.basketball.dto.request.VenueQueryDTO;
import com.basketball.dto.request.VenueUpdateDTO;
import com.basketball.entity.Venue;
import com.basketball.vo.VenuePriceVO;
import com.basketball.vo.VenueVO;

import java.util.List;

/**
 * 场地服务接口
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
public interface IVenueService extends IService<Venue> {

    /**
     * 创建场地
     */
    void createVenue(VenueCreateDTO createDTO);

    /**
     * 更新场地
     */
    void updateVenue(Long id, VenueUpdateDTO updateDTO);

    /**
     * 删除场地
     */
    void deleteVenue(Long id);

    /**
     * 获取场地详情
     */
    VenueVO getVenueById(Long id);

    /**
     * 场地列表（分页）
     */
    PageResult<VenueVO> listVenues(VenueQueryDTO queryDTO);

    /**
     * 查询可用场地
     */
    List<VenueVO> listAvailableVenues();

    /**
     * 设置场地价格
     */
    void setVenuePrice(VenuePriceDTO priceDTO);

    /**
     * 获取场地价格列表
     */
    List<VenuePriceVO> getVenuePrices(Long venueId);

    /**
     * 计算预订价格
     */
    java.math.BigDecimal calculatePrice(Long venueId, String startTime, String endTime, Integer memberLevel);
}
