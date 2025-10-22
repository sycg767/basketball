package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.CourseCreateDTO;
import com.basketball.dto.request.CoursePriceDTO;
import com.basketball.dto.request.CourseQueryDTO;
import com.basketball.dto.request.CourseUpdateDTO;
import com.basketball.entity.Course;
import com.basketball.entity.MemberCard;
import com.basketball.entity.MemberCardType;
import com.basketball.entity.User;
import com.basketball.mapper.CourseMapper;
import com.basketball.mapper.MemberCardMapper;
import com.basketball.mapper.MemberCardTypeMapper;
import com.basketball.mapper.UserMapper;
import com.basketball.service.ICourseService;
import com.basketball.vo.CoursePriceVO;
import com.basketball.vo.CourseVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MemberCardMapper memberCardMapper;

    @Resource
    private MemberCardTypeMapper memberCardTypeMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCourse(CourseCreateDTO dto) {
        // 验证教练是否存在
        User coach = userMapper.selectById(dto.getCoachId());
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }

        // 创建课程实体
        Course course = new Course();
        BeanUtils.copyProperties(dto, course);

        // 生成课程编号
        course.setCourseCode("COU" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());

        // 设置默认值
        course.setStatus(1); // 默认上架
        course.setViewCount(0);
        course.setEnrollCount(0);
        course.setRating(BigDecimal.valueOf(5.00));
        course.setCreateTime(LocalDateTime.now());

        courseMapper.insert(course);
        log.info("创建课程成功, 课程ID: {}, 课程名称: {}", course.getId(), course.getCourseName());

        return course.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCourse(Long id, CourseUpdateDTO dto) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 如果更新了教练,验证教练是否存在
        if (dto.getCoachId() != null) {
            User coach = userMapper.selectById(dto.getCoachId());
            if (coach == null) {
                throw new BusinessException("教练不存在");
            }
        }

        // 更新课程信息
        BeanUtils.copyProperties(dto, course, "id", "courseCode", "createTime");
        course.setUpdateTime(LocalDateTime.now());

        courseMapper.updateById(course);
        log.info("更新课程成功, 课程ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourse(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        courseMapper.deleteById(id);
        log.info("删除课程成功, 课程ID: {}", id);
    }

    @Override
    public CourseVO getCourseDetail(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        return convertToVO(course);
    }

    @Override
    public PageResult<CourseVO> getCourseList(CourseQueryDTO dto) {
        Page<Course> page = new Page<>(dto.getPage(), dto.getSize());

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(dto.getKeyword()), Course::getCourseName, dto.getKeyword())
                .eq(dto.getCourseType() != null, Course::getCourseType, dto.getCourseType())
                .eq(dto.getCoachId() != null, Course::getCoachId, dto.getCoachId())
                .eq(dto.getDifficultyLevel() != null, Course::getDifficultyLevel, dto.getDifficultyLevel())
                .eq(dto.getStatus() != null, Course::getStatus, dto.getStatus())
                .orderByDesc(Course::getCreateTime);

        IPage<Course> result = courseMapper.selectPage(page, wrapper);

        List<CourseVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        Course course = courseMapper.selectById(id);
        if (course != null) {
            course.setViewCount(course.getViewCount() + 1);
            courseMapper.updateById(course);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCourseStatus(Long id, Integer status) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        course.setStatus(status);
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(course);
        log.info("更新课程状态成功, 课程ID: {}, 状态: {}", id, status);
    }

    @Override
    public CoursePriceVO calculateCoursePrice(Long userId, CoursePriceDTO dto) {
        // 1. 获取课程基础价格
        Course course = courseMapper.selectById(dto.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        BigDecimal basePrice = course.getPrice();

        // 2. 查询用户有效会员卡
        LambdaQueryWrapper<MemberCard> cardQuery = new LambdaQueryWrapper<>();
        cardQuery.eq(MemberCard::getUserId, userId)
                .eq(MemberCard::getStatus, 1)
                .ge(MemberCard::getExpireDate, java.time.LocalDate.now())
                .orderByDesc(MemberCard::getCreateTime)
                .last("LIMIT 1");
        MemberCard card = memberCardMapper.selectOne(cardQuery);

        BigDecimal actualPrice = basePrice;
        BigDecimal discountAmount = BigDecimal.ZERO;
        boolean hasCard = false;
        String cardName = "";
        BigDecimal discount = BigDecimal.ONE;

        // 3. 应用会员卡折扣
        if (card != null) {
            MemberCardType cardType = memberCardTypeMapper.selectById(card.getCardTypeId());
            if (cardType != null && cardType.getDiscount() != null) {
                discount = cardType.getDiscount();
                actualPrice = basePrice.multiply(discount).setScale(2, java.math.RoundingMode.HALF_UP);
                discountAmount = basePrice.subtract(actualPrice);
                hasCard = true;
                cardName = cardType.getCardName();
            }
        }

        // 4. 计算最大积分抵扣(50%)
        BigDecimal maxPointsDeduct = actualPrice.multiply(new BigDecimal("0.5")).setScale(2, java.math.RoundingMode.HALF_UP);

        return CoursePriceVO.builder()
                .basePrice(basePrice)
                .memberPrice(actualPrice)
                .actualPrice(actualPrice)
                .discountAmount(discountAmount)
                .hasCard(hasCard)
                .cardName(cardName)
                .discount(discount)
                .canUsePoints(true)
                .maxPointsDeduct(maxPointsDeduct)
                .build();
    }

    /**
     * 转换为VO
     */
    private CourseVO convertToVO(Course course) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(course, vo);

        // 获取教练名称
        User coach = userMapper.selectById(course.getCoachId());
        if (coach != null) {
            vo.setCoachName(coach.getRealName() != null ? coach.getRealName() : coach.getUsername());
        }

        // 处理课程大纲JSON
        if (StringUtils.isNotBlank(course.getSyllabus())) {
            try {
                List<String> syllabusList = objectMapper.readValue(
                        course.getSyllabus(),
                        new TypeReference<List<String>>() {}
                );
                vo.setSyllabusList(syllabusList);
            } catch (Exception e) {
                log.error("解析课程大纲失败", e);
            }
        }

        return vo;
    }
}
