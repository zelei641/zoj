package com.yupi.springbootinit.judge;

import com.yupi.springbootinit.judge.strategy.DefaultJudgeStrategy;
import com.yupi.springbootinit.judge.strategy.JavaLaguageJudgeStrategy;
import com.yupi.springbootinit.judge.strategy.JudgeConext;
import com.yupi.springbootinit.judge.strategy.JudgeStrategy;
import com.yupi.springbootinit.judge.codesandbox.model.JudgeInfo;
import com.yupi.springbootinit.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;


/**
 * 简化对判题功能的调用
 */
@Service
public class JudgeManager
{
    JudgeInfo doJudge(JudgeConext judgeConext)
    {
        QuestionSubmit questionSubmit = judgeConext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();

        //if (language.equals("java"))
       // {
            judgeStrategy = new JavaLaguageJudgeStrategy();
        //}


        return judgeStrategy.doJudge(judgeConext);
    }
}
