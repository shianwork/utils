package org.shian;

import org.shian.csdnToMd.CsdnToMd;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;

/**
 * @author shian
 * @version 1.0.0
 * @ClassName org.shian
 * @description TODO
 * @createTime 2024/7/22 16:05
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
        System.out.println("当前提供快捷方法如下:");
        System.out.println("1:csdn网页内容转md文档");
        System.out.println("请输入执行工具,例如输入:1,请输入:");
        Scanner scanner = new Scanner(System.in);
        String utiltype = scanner.nextLine();

        if ("1".equals(utiltype)) {
            CsdnToMd csdnToMd = new CsdnToMd();
            csdnToMd.csdnToMd();
        }else {
            System.out.println("更多工具还在开发中...");

        }
    }
}