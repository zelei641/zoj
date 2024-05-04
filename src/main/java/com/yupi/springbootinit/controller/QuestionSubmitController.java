package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.QuestionSubmit.QuestionSubmitAddRequest;
import com.yupi.springbootinit.model.QuestionSubmit.QuestionSubmitQueryRequest;
import com.yupi.springbootinit.model.entity.QuestionSubmit;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.QuestionSubmitVO;
import com.yupi.springbootinit.service.QuestionSubmitService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/post_thumb")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService QuestionSubmitService;

    @Resource
    private UserService userService;


    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录的id
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才提交
        final User loginUser = userService.getLoginUser(request);
        long questionId = questionSubmitAddRequest.getQuestionId();
        long questionSubmitId = QuestionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        // todo 执行判题服务

        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表(除了管理员外, 普通用户只能看到非答案,提交代码等公开信息)
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitVOByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                           HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = QuestionSubmitService.page(new Page<>(current, size),
                QuestionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(QuestionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));

    }

    /**
     * 单一查询
     *
     * @return
     */
    @GetMapping("/list/page/vo/details")
    public BaseResponse<QuestionSubmitVO> listQuestionSubmitVO(String id) {
        if (id == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        long idl = Long.parseLong(id);

        // 返回脱敏信息
        return ResultUtils.success(QuestionSubmitService.getQuestionSubmitVO(idl));

    }


}
