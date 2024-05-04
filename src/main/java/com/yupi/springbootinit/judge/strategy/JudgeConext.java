package com.yupi.springbootinit.judge.strategy;

import com.yupi.springbootinit.model.Question.JudgeCase;
import com.yupi.springbootinit.judge.codesandbox.model.JudgeInfo;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;


/**
 * 上下文 (用于定义在策略中传递的参数)
 */
@Data
public class JudgeConext
{
    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private Question question;

    private QuestionSubmit questionSubmit;

    private List<JudgeCase> judgeCaseList;


}
