package com.yupi.springbootinit.model.enums;

import cn.hutool.core.util.ObjectUtil;
import com.yupi.springbootinit.service.QuestionSubmitService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子审核状态枚举
 *
 * @author yupi
 */
public enum QuestionSubmitStatusEnum {

    WATTING("待判题", 0),
    RUNNING("判题中", 1),
    SUCCESS("成功", 2),
    FAILED("失败", 3);

    private final String text;

    private final Integer value;

    QuestionSubmitStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据值得到枚举类
     * @param value
     * @return
     */
    public static QuestionSubmitStatusEnum getEunmByValue(Integer value)
    {
        if (ObjectUtil.isEmpty(value))  {
            return null;
        }
        for (QuestionSubmitStatusEnum qem : QuestionSubmitStatusEnum.values()) {
            if (qem.value.equals(value))
            {
                return qem;
            }
        }
        return null;
    }


    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
