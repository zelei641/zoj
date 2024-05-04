package com.yupi.springbootinit.judge.codesandbox.impl;

import com.yupi.springbootinit.judge.codesandbox.CodeSandbox;
import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.springbootinit.judge.codesandbox.model.JudgeInfo;
import com.yupi.springbootinit.model.enums.JudgeInfoMessageEnum;
import com.yupi.springbootinit.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙箱(仅为了跑通业务流程)
 */
public class ExampleCodeSandbox implements CodeSandbox
{

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executedCodeRequest) {

        List<String> input = executedCodeRequest.getInput();
        String code = executedCodeRequest.getCode();
        String language = executedCodeRequest.getLanguage();


        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();

        executeCodeResponse.setOutput(input);
        executeCodeResponse.setMessage("测试用例执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        executeCodeResponse.setJudgeInfo(judgeInfo);


        return executeCodeResponse;
    }
}
