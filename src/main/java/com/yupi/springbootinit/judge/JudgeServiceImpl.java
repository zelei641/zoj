package com.yupi.springbootinit.judge;

import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.judge.codesandbox.CodeSandbox;
import com.yupi.springbootinit.judge.codesandbox.CodeSandboxFactory;
import com.yupi.springbootinit.judge.codesandbox.CodeSandboxProxy;
import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.springbootinit.judge.strategy.JudgeConext;
import com.yupi.springbootinit.model.Question.JudgeCase;
import com.yupi.springbootinit.judge.codesandbox.model.JudgeInfo;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.entity.QuestionSubmit;
import com.yupi.springbootinit.model.enums.QuestionSubmitStatusEnum;
import com.yupi.springbootinit.service.QuestionService;
import com.yupi.springbootinit.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService{

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:excample}")
    String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {

        //1)拿到信息
        //拿到提交信息
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);


        if (questionSubmit == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交信息不存在");
        }

        //2)拿到题目
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }

        //3)题目的状态不为等待状态 就直接返回 不进行判题(防止用户一直提交我们的程序一直运行)
        Integer questionSubmitStatus = questionSubmit.getStatus();
        if (!questionSubmitStatus.equals(QuestionSubmitStatusEnum.WATTING.getValue()))
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }

        //4)更改题目提交的状态 (防止用户一直提交我们的程序一直运行)
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) throw new BusinessException(ErrorCode.OPERATION_ERROR,"更新错误");

        //5)调用代码沙箱 获取执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        String code = questionSubmit.getCode(); //得到代码
        String language = questionSubmit.getLanguage(); //得到编程语言
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCasesList = JSONUtil.toList(judgeCase, JudgeCase.class); //得到测试用例

        codeSandbox = new CodeSandboxProxy(codeSandbox);
        List<String> inputList = judgeCasesList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest build = ExecuteCodeRequest.builder()
                .code(code)
                .input(inputList)
                .language(language)
                .build();
        System.out.println("fesadfasdf");
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(build);
        List<String> outputList = executeCodeResponse.getOutput();

        //6)根据执行结果 得到提交信息
        JudgeConext judgeConext = new JudgeConext();
        judgeConext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeConext.setInputList(inputList);
        judgeConext.setOutputList(outputList);
        judgeConext.setQuestion(question);
        judgeConext.setJudgeCaseList(judgeCasesList);
        judgeConext.setQuestionSubmit(questionSubmit);
        //处理结果
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeConext);

        //7)修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"更新错误");
        }


        QuestionSubmit currentQuestionSubmit = questionSubmitService.getById(questionSubmitId);
        return currentQuestionSubmit;
    }
}
