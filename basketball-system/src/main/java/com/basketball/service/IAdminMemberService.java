package com.basketball.service;

import com.basketball.common.result.PageResult;
import com.basketball.dto.request.CardTypeDTO;
import com.basketball.vo.MemberCardTypeVO;
import com.basketball.vo.MemberCardVO;

/**
 * 管理员会员服务接口
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
public interface IAdminMemberService {

    /**
     * 获取会员卡类型列表
     *
     * @param page     页码
     * @param pageSize 每页大小
     * @return 会员卡类型列表
     */
    PageResult<MemberCardTypeVO> listCardTypes(Integer page, Integer pageSize);

    /**
     * 创建会员卡类型
     *
     * @param dto 会员卡类型信息
     * @return 会员卡类型ID
     */
    Long createCardType(CardTypeDTO dto);

    /**
     * 更新会员卡类型
     *
     * @param id  会员卡类型ID
     * @param dto 会员卡类型信息
     */
    void updateCardType(Long id, CardTypeDTO dto);

    /**
     * 删除会员卡类型
     *
     * @param id 会员卡类型ID
     */
    void deleteCardType(Long id);

    /**
     * 更新会员卡类型状态
     *
     * @param id     会员卡类型ID
     * @param status 状态
     */
    void updateCardTypeStatus(Long id, Integer status);

    /**
     * 获取所有会员卡列表
     *
     * @param keyword  关键词
     * @param status   状态
     * @param page     页码
     * @param pageSize 每页大小
     * @return 会员卡列表
     */
    PageResult<MemberCardVO> getAllCards(String keyword, Integer status, Integer page, Integer pageSize);

    /**
     * 激活会员卡
     *
     * @param id 会员卡ID
     */
    void activateCard(Long id);

    /**
     * 冻结会员卡
     *
     * @param id 会员卡ID
     */
    void freezeCard(Long id);
}
