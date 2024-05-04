package com.yupi.springbootinit.judge.codesandbox;

import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox
{
    /**
     * 执行代码
     * @param executedCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executedCodeRequest);
}
