package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.CardTypeDTO;
import com.basketball.entity.MemberCard;
import com.basketball.entity.MemberCardType;
import com.basketball.entity.User;
import com.basketball.mapper.MemberCardMapper;
import com.basketball.mapper.MemberCardTypeMapper;
import com.basketball.mapper.UserMapper;
import com.basketball.service.IAdminMemberService;
import com.basketball.vo.MemberCardTypeVO;
import com.basketball.vo.MemberCardVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员会员服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Service
public class AdminMemberServiceImpl implements IAdminMemberService {

    @Resource
    private MemberCardTypeMapper cardTypeMapper;

    @Resource
    private MemberCardMapper memberCardMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public PageResult<MemberCardTypeVO> listCardTypes(Integer page, Integer pageSize) {
        Page<MemberCardType> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<MemberCardType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MemberCardType::getSortOrder)
                .orderByDesc(MemberCardType::getCreateTime);

        Page<MemberCardType> result = cardTypeMapper.selectPage(pageParam, wrapper);

        List<MemberCardTypeVO> voList = result.getRecords().stream()
                .map(this::convertToCardTypeVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCardType(CardTypeDTO dto) {
        // 检查编号是否已存在
        LambdaQueryWrapper<MemberCardType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCardType::getCardCode, dto.getCardCode());
        if (cardTypeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("会员卡编号已存在");
        }

        MemberCardType cardType = new MemberCardType();
        BeanUtils.copyProperties(dto, cardType);

        // 处理权益列表
        if (dto.getBenefits() != null && !dto.getBenefits().isEmpty()) {
            try {
                cardType.setBenefits(objectMapper.writeValueAsString(dto.getBenefits()));
            } catch (Exception e) {
                throw new BusinessException("权益列表格式错误");
            }
        }

        cardTypeMapper.insert(cardType);
        return cardType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCardType(Long id, CardTypeDTO dto) {
        MemberCardType cardType = cardTypeMapper.selectById(id);
        if (cardType == null) {
            throw new BusinessException("会员卡类型不存在");
        }

        // 检查编号是否重复
        LambdaQueryWrapper<MemberCardType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCardType::getCardCode, dto.getCardCode())
                .ne(MemberCardType::getId, id);
        if (cardTypeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("会员卡编号已存在");
        }

        BeanUtils.copyProperties(dto, cardType);

        // 处理权益列表
        if (dto.getBenefits() != null && !dto.getBenefits().isEmpty()) {
            try {
                cardType.setBenefits(objectMapper.writeValueAsString(dto.getBenefits()));
            } catch (Exception e) {
                throw new BusinessException("权益列表格式错误");
            }
        }

        cardTypeMapper.updateById(cardType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCardType(Long id) {
        MemberCardType cardType = cardTypeMapper.selectById(id);
        if (cardType == null) {
            throw new BusinessException("会员卡类型不存在");
        }

        // 检查是否有关联的会员卡
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getCardTypeId, id);
        if (memberCardMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该会员卡类型下有会员卡，无法删除");
        }

        cardTypeMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCardTypeStatus(Long id, Integer status) {
        MemberCardType cardType = cardTypeMapper.selectById(id);
        if (cardType == null) {
            throw new BusinessException("会员卡类型不存在");
        }

        cardType.setStatus(status);
        cardTypeMapper.updateById(cardType);
    }

    @Override
    public PageResult<MemberCardVO> getAllCards(String keyword, Integer status, Integer page, Integer pageSize) {
        Page<MemberCard> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(MemberCard::getStatus, status);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(MemberCard::getCardNo, keyword)
                    .or().in(MemberCard::getUserId, getUserIdsByKeyword(keyword)));
        }

        wrapper.orderByDesc(MemberCard::getCreateTime);

        Page<MemberCard> result = memberCardMapper.selectPage(pageParam, wrapper);

        List<MemberCardVO> voList = result.getRecords().stream()
                .map(this::convertToCardVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateCard(Long id) {
        MemberCard card = memberCardMapper.selectById(id);
        if (card == null) {
            throw new BusinessException("会员卡不存在");
        }

        card.setStatus(1);
        memberCardMapper.updateById(card);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeCard(Long id) {
        MemberCard card = memberCardMapper.selectById(id);
        if (card == null) {
            throw new BusinessException("会员卡不存在");
        }

        card.setStatus(2);
        memberCardMapper.updateById(card);
    }

    /**
     * 根据关键词获取用户ID列表
     */
    private List<Long> getUserIdsByKeyword(String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(User::getUsername, keyword)
                .or().like(User::getRealName, keyword)
                .or().like(User::getPhone, keyword);

        return userMapper.selectList(wrapper).stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    /**
     * 转换为会员卡类型VO
     */
    private MemberCardTypeVO convertToCardTypeVO(MemberCardType cardType) {
        MemberCardTypeVO vo = new MemberCardTypeVO();
        BeanUtils.copyProperties(cardType, vo);

        // 处理benefits JSON
        if (cardType.getBenefits() != null) {
            try {
                List<String> benefits = objectMapper.readValue(
                        cardType.getBenefits(),
                        new TypeReference<List<String>>() {}
                );
                vo.setBenefitsList(benefits);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        return vo;
    }

    /**
     * 转换为会员卡VO
     */
    private MemberCardVO convertToCardVO(MemberCard card) {
        MemberCardVO vo = new MemberCardVO();
        BeanUtils.copyProperties(card, vo);

        // 填充卡类型信息
        MemberCardType cardType = cardTypeMapper.selectById(card.getCardTypeId());
        if (cardType != null) {
            vo.setCardName(cardType.getCardName());
            vo.setCardType(cardType.getCardType());
        }

        // 填充用户信息
        User user = userMapper.selectById(card.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
            vo.setPhone(user.getPhone());
        }

        return vo;
    }
}
