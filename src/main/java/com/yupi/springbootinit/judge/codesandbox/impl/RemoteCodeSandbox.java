package com.yupi.springbootinit.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.judge.codesandbox.CodeSandbox;
import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandbox implements CodeSandbox
{

    // 定义健全请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executedCodeRequest) {
        System.out.println("远程代码沙箱");

        String url = "http://localhost:8081/executeCode";
        String json = JSONUtil.toJsonStr(executedCodeRequest);
        String resopnseStr = HttpUtil.createPost(url).header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET).body(json).execute().body();
        if (StringUtils.isBlank(resopnseStr))
        {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"executeCode remoteSanbox error message = " + resopnseStr);
        }

        return JSONUtil.toBean(resopnseStr,ExecuteCodeResponse.class);
    }
}
