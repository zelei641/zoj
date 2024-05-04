package com.yupi.springbootinit.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.Question.JudgeCase;
import com.yupi.springbootinit.model.Question.JudgeConfig;
import com.yupi.springbootinit.judge.codesandbox.model.JudgeInfo;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

/**
 * java的判题策略
 */
public class JavaLaguageJudgeStrategy implements JudgeStrategy{
    @Override
    public JudgeInfo doJudge(JudgeConext judgeConext) {

        //得到判题需要的依赖
        JudgeInfo judgeInfo = judgeConext.getJudgeInfo();
        List<String> inputList = judgeConext.getInputList();
        List<String> outputList = judgeConext.getOutputList();
        Question question = judgeConext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeConext.getJudgeCaseList();


        //得到代码运行的数据
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        //设置返回值
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);

        //判断输出和输入用例的数量是否相等
        if (outputList.size() != inputList.size())
        {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        //判断输出是否正确
        for (int i = 0; i < judgeCaseList.size(); i ++)
        {
            JudgeCase judgeCase1 = judgeCaseList.get(i);
            if (!judgeCase1.getOutput().equals(outputList.get(i)))
            {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        //判断题目限制
        //得到题目要求的限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfigQuestion = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needTimeLimit = judgeConfigQuestion.getTimeLimit();
        Long needMemoryLimit = judgeConfigQuestion.getMemoryLimit();
        //比较
        if (memory > needMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        //java本身需要执行10s
        long JAVA_PROGRAM_TIME_COST = 10000;
        if (time - JAVA_PROGRAM_TIME_COST > needTimeLimit)
        {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());


        return judgeInfoResponse;
    }
}
