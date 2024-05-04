package com.yupi.springbootinit.model.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yupi.springbootinit.model.Question.JudgeConfig;
import com.yupi.springbootinit.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目返回封装类
 * @TableName question
 */
@Data
public class QuestionVO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 题目标签
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数字
     */
    private Integer accpetNum;

    /**
     * 题目设置（Json数组）
     */
    private JudgeConfig judgeConfig;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 创建题目的人的信息
     */
    private UserVO userInfo;


    @TableField(exist = false) //数据库表没有对应的字段
    private static final long serialVersionUID = 1L;



    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);

        //设置Tags
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            //转成Json字符串
            question.setTags(JSONUtil.toJsonStr(tagList));
        }

        //设置judgeConfig
        JudgeConfig judgeConfig1 = questionVO.getJudgeConfig();
        if (judgeConfig1 != null)
        {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig1));
        }

        return question;
    }

    /**
     * 对象转包装类
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);

        String tags1 = question.getTags();
        if (ObjectUtil.isNotEmpty(tags1))
        {
            //从Json字符串转换回来
            questionVO.setTags(JSONUtil.toList(tags1,String.class));
        }

        String judgeConfig1 = question.getJudgeConfig();
        if (judgeConfig1 != null)
        {
            //从Json字符串转换回来
            questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfig1, JudgeConfig.class));
        }

        return questionVO;
    }

}