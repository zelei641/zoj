package com.yupi.springbootinit.judge.codesandbox;

import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.springbootinit.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.springbootinit.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
class CodeSandboxTest
{
  
    @Value("${codesandbox.type:excample}")
    String type;

    @Test
    void executeCodeByProxy()
    {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String code = "public class Main {\n" +
                "    public Main() {\n" +
                "    }\n" +
                "\n" +
                "    public static void main(String[] var0) {\n" +
                "        Integer var1 = Integer.valueOf(var0[0]);\n" +
                "        Integer var2 = Integer.valueOf(var0[1]);\n" +
                "        System.out.println(\"结果\" + (var1 + var2));\n" +
                "    }\n" +
                "}\n";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");


        ExecuteCodeRequest build = ExecuteCodeRequest.builder()
                .code(code)
                .input(inputList)
                .language(language)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(build);
        Assertions.assertNotNull(executeCodeResponse);
    }

    @Test
    void executeCodeByValue()
    {

        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        String code = "int main {}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");


        ExecuteCodeRequest build = ExecuteCodeRequest.builder()
                .code(code)
                .input(inputList)
                .language(language)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(build);
        Assertions.assertNotNull(executeCodeResponse);


    }


    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext())
        {
            String type = scanner.next();

            CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
            String code = "int main {}";
            String language = QuestionSubmitLanguageEnum.JAVA.getValue();
            List<String> inputList = Arrays.asList("1 2", "3 4");


            ExecuteCodeRequest build = ExecuteCodeRequest.builder()
                    .code(code)
                    .input(inputList)
                    .language(language)
                    .build();
            ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(build);
            Assertions.assertNotNull(executeCodeResponse);
        }
    }
}