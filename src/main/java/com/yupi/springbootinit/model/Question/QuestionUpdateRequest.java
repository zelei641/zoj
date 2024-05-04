package com.yupi.springbootinit.model.Question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class QuestionUpdateRequest implements Serializable {

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
     * 答案
     */
    private String answer;

    /**
     * 判题用例（Json数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 题目设置
     */
    private JudgeConfig judgeConfig;


    @TableField(exist = false)
    private static final long serialVersionUID = 141L;
}