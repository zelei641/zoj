package com.yupi.springbootinit.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.judge.JudgeService;
import com.yupi.springbootinit.mapper.QuestionSubmitMapper;
import com.yupi.springbootinit.model.QuestionSubmit.QuestionSubmitAddRequest;
import com.yupi.springbootinit.model.QuestionSubmit.QuestionSubmitQueryRequest;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.entity.QuestionSubmit;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.enums.QuestionSubmitLanguageEnum;
import com.yupi.springbootinit.model.enums.QuestionSubmitStatusEnum;
import com.yupi.springbootinit.model.vo.QuestionSubmitVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.QuestionService;
import com.yupi.springbootinit.service.QuestionSubmitService;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author 29620
 * @description 针对表【question_submit(题目提交表)】的数据库操作Service实现
 * @createDate 2024-04-08 21:59:26
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    /**
     * 提交题目
     *
     * @param doQuestionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest doQuestionSubmit, User loginUser) {
        Long questionId = doQuestionSubmit.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        //todo 校验编程语言是否合法
        String language = doQuestionSubmit.getLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (ObjectUtil.isEmpty(enumByValue)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不存在");
        }

        //更改题目提交数据
        Question questionUpdate = new Question();
        BeanUtils.copyProperties(question, questionUpdate);
        Integer submitNum = question.getSubmitNum();
        questionUpdate.setSubmitNum(submitNum + 1);


        // 是否已提交题目
        long userId = loginUser.getId();

        // todo 加锁
        // 锁必须要包裹住事务方法
        // 为了防止用户重复提交  只允许用户同时提交一条
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(doQuestionSubmit.getCode());
        questionSubmit.setLanguage(language);


        //todo 设置初始状态
        questionSubmit.setStatus(0);
        questionSubmit.setJudgeInfo("{}");

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入异常");
        }

        //执行判题服务 更新题目信息
        CompletableFuture.runAsync(() -> {
            QuestionSubmit questionSubmit1 = judgeService.doJudge(questionSubmit.getId());
            if (questionSubmit1.getJudgeInfo().length() >= 20)
            {
                System.out.println("questionSubmit1.getJudgeInfo() = " + questionSubmit1.getJudgeInfo());
                String s = new String("{\"message\":\"Accepted\"");
                int n = s.length();
                String questionJudgeInfo = questionSubmit1.getJudgeInfo();
                Boolean f = true;
                for (int i = 0; i < n; i ++)
                {
                    char c = questionJudgeInfo.charAt(i);
                    char c1 = s.charAt(i);
                    if (questionJudgeInfo.charAt(i) != s.charAt(i))
                    {
                        f = false;
                    }
                }
                if (f)
                {
                    Integer accpetNum = question.getAccpetNum();
                    questionUpdate.setAccpetNum(accpetNum + 1);
                }
            }
            boolean b = questionService.updateById(questionUpdate);
            if (!b)
            {
                System.out.println("更新错误");
            }
        });
        return questionSubmit.getId();

    }


//    /**
//     * 封装了事务的方法
//     *
//     * @param userId
//     * @param postId
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int doQuestionSubmitInner(long userId, long postId) {
//
//        QueryWrapper<QuestionSubmit> thumbQueryWrapper = new QueryWrapper<>(questionSubmit);
//        QuestionSubmit oldQuestionSubmit = this.getOne(thumbQueryWrapper);
//        boolean result;
//        // 已点赞
//        if (oldQuestionSubmit != null) {
//            result = this.remove(thumbQueryWrapper);
//            if (result) {
//                // 点赞数 - 1
//                result = questionService.update()
//                        .eq("id", postId)
//                        .gt("thumbNum", 0)
//                        .setSql("thumbNum = thumbNum - 1")
//                        .update();
//                return result ? -1 : 0;
//            } else {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//            }
//        } else {
//            // 未点赞
//            result = this.save(questionSubmit);
//            if (result) {
//                // 点赞数 + 1
//                result = questionService.update()
//                        .eq("id", postId)
//                        .setSql("thumbNum = thumbNum + 1")
//                        .update();
//                return result ? 1 : 0;
//            } else {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//            }
//        }
//    }


    /**
     * 获取查询包装类  用户会根据那些字段查询  根据前端传来的请求对象 得到 mybatis框架支持的 QueryWrapper 查询类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStaus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtil.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEunmByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);


        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 前端请求分页 返回给前端
     *
     * @param questionSubmit
     * @param loginUser
     * @questionSubmit
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {

        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        //long userId = loginUser.getId();
        // 处理脱敏
//        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
//            questionSubmitVO.setCode(null);
//        }
        //得到改题目的作者信息
//        Long userId = questionSubmitVO.getUserId();
//        //查找作者信息
//        User byId = userService.getById(userId);
//        //用户信息返回类
//        UserVO userVO = new UserVO();
//        userVO.setId(byId.getId());
//        userVO.setUserName(byId.getUserName());
//        userVO.setUserAvatar(byId.getUserAvatar());
//        userVO.setUserProfile(byId.getUserProfile());
//        userVO.setUserRole(null);
//        userVO.setCreateTime(byId.getCreateTime());
//
//        questionSubmitVO.setUserVO(userVO);

        return questionSubmitVO;
    }

    /**
     * 前端请求单一 返回给前端
     *
     * @param questionSubmit
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitDetailsVO(QuestionSubmit questionSubmit) {

        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        Long userId = questionSubmitVO.getUserId();
        //查找作者信息
        User byId = userService.getById(userId);
        //用户信息返回类
        UserVO userVO = new UserVO();
        userVO.setId(byId.getId());
        userVO.setUserName(byId.getUserName());
        userVO.setUserAvatar(byId.getUserAvatar());
        userVO.setUserProfile(byId.getUserProfile());
        userVO.setUserRole(null);
        userVO.setCreateTime(byId.getCreateTime());

        questionSubmitVO.setUserVO(userVO);

        return questionSubmitVO;
    }


    /**
     * 前端请求单一
     *
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(long id) {
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        QuestionSubmit one = this.baseMapper.selectOne(queryWrapper);

        questionSubmitVO = getQuestionSubmitDetailsVO(one);

        return questionSubmitVO;
    }


    /**
     * 分页条件查询
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());

        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 1. 关联查询用户信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());

        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }



}




