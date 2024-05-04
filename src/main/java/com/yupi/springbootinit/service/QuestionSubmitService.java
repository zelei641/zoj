package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.QuestionSubmit.QuestionSubmitAddRequest;
import com.yupi.springbootinit.model.QuestionSubmit.QuestionSubmitQueryRequest;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.entity.QuestionSubmit;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.QuestionSubmitVO;
import com.yupi.springbootinit.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 29620
 * @description 针对表【question_submit(题目提交表)】的数据库操作Service
 * @createDate 2024-04-08 21:59:26
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param doQuestionSubmit //题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest doQuestionSubmit, User loginUser);


    /**
     * 获取查询条件
     *
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);


    /**
     * 获取返回的问题封装类
     *
     * @param questionSubmit
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);


    /**
     * 前端请求单一 返回给前端
     *
     * @param questionSubmit
     * @return
     */
    QuestionSubmitVO getQuestionSubmitDetailsVO(QuestionSubmit questionSubmit);

    /**
     * 返回题目提交封装类
     *
     * @param questionSubmitPage
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);


    /**
     * 前端请求单一
     *
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(long userid);
}
