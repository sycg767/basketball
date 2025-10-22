package com.basketball.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.BookingCancelDTO;
import com.basketball.dto.request.BookingCreateDTO;
import com.basketball.dto.request.BookingPayDTO;
import com.basketball.dto.response.PaymentResultVO;
import com.basketball.entity.Booking;
import com.basketball.vo.BookingVO;

/**
 * 预订服务接口
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
public interface IBookingService extends IService<Booking> {

    /**
     * 创建预订
     */
    Long createBooking(Long userId, BookingCreateDTO createDTO);

    /**
     * 获取用户预订列表（分页）
     */
    PageResult<BookingVO> getUserBookingList(Long userId, Integer page, Integer pageSize, Integer status);

    /**
     * 获取预订列表（分页）- 管理员
     */
    PageResult<BookingVO> getBookingList(Integer page, Integer pageSize, String orderNo, String username, Integer status);

    /**
     * 获取预订详情
     */
    BookingVO getBookingDetail(Long id);

    /**
     * 取消预订
     */
    void cancelBooking(Long id, BookingCancelDTO cancelDTO);

    /**
     * 支付预订
     * @return 支付结果（在线支付时返回二维码或跳转URL，其他支付方式返回null）
     */
    PaymentResultVO payBooking(Long id, BookingPayDTO payDTO);

    /**
     * 更新预订状态 - 管理员
     */
    void updateBookingStatus(Long id, Integer status);

    /**
     * 检查场地时间段是否可用
     */
    boolean checkVenueAvailable(Long venueId, java.time.LocalDate bookingDate, java.time.LocalTime startTime, java.time.LocalTime endTime);

    /**
     * 计算预订价格
     */
    java.util.Map<String, Object> calculateBookingPrice(Long userId, Long venueId, java.time.LocalDate bookingDate, java.time.LocalTime startTime, java.time.LocalTime endTime);
}
