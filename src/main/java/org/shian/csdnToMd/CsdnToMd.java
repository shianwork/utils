package org.shian.csdnToMd;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;

import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shian
 * @version 1.0.0
 * @ClassName org.shian.csdnToMd
 * @description 提供csdn网页文章带格式复制为md文档 可进行语雀等笔记复制
 * @createTime 2024/7/22 16:06
 */
@Component
public class CsdnToMd {
    @Value( "${csdnToMd.outPath}")
    private  String outPath;


    public  void csdnToMd()  {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入目标csdn网址:");
        String url = scanner.nextLine();
        try {
            // 获取网页内容
            System.out.println("正在从网址获取内容...");
            Document document = Jsoup.connect(url).get();
            Element content = document.selectFirst(".article_content");
            String userHome= null;
            if (content != null) {
                if (StringUtil.isBlank(outPath)){
                    userHome = System.getProperty("user.home")+File.separator+"Desktop";
                }else {
                    userHome=outPath;
                }
                // 获取用户的主目录
                System.out.println("输出路径为:"+userHome);
                // 获取特定元素
                Element contentElement = document.selectFirst("#articleContentId");
                String filename =null;
                if (contentElement != null) {
                    filename = contentElement.text();
                } else {
                    filename = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                }
                String txtPath = userHome+ File.separator+filename + ".txt";
                String htmlPath = userHome+ File.separator+filename + ".html";
                String mdPath = userHome+ File.separator+filename + ".md";

                // 保存到TXT文件
                System.out.println("正在保存内容到TXT文件...");
                saveToFile(content.toString(), txtPath);

                // 重命名文件为HTML
                System.out.println("正在转换TXT文件到HTML...");
                Files.move(Paths.get(txtPath), Paths.get(htmlPath));

                // 复制HTML内容到MD文件
                System.out.println("正在复制HTML内容到MD文件...");
                htmlToMd(htmlPath,mdPath);

                // 删除HTML文件
                System.out.println("正在删除HTML文件...");
                Files.delete(Paths.get(htmlPath));

                // 输出MD文件路径
                System.out.println("任务完成。MD文件路径: " + Paths.get(mdPath).toAbsolutePath());
            } else {
                System.out.println("无法找到article_content元素。");
            }
        } catch (IOException e) {
            System.out.println("发生错误: " + e.getMessage());
        }
    }


    private static void saveToFile(String content, String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(content);
        writer.close();
    }
    /**
     * @description 复制html内容解析为md文档
     * @param htmlFilePath
     * @param markdownFilePath
     * @return void
     * @author shian
     * @date 2024/7/22 16:38
     */
    private static void htmlToMd(String htmlFilePath, String markdownFilePath) throws IOException {
        try {
            // 读取HTML文件内容
            String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)));

            // 创建转换器并转换HTML为Markdown
            FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
            String markdownContent = converter.convert(htmlContent);

            // 使用正则表达式移除自动生成的锚点标记
            String cleanedMarkdownContent = removeAnchorTags(markdownContent);
            // 将清理后的Markdown内容写入文件
            Files.write(Paths.get(markdownFilePath), cleanedMarkdownContent.getBytes());
            System.out.println("转换完成");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("发生错误：" + e.getMessage());
        }
    }

    /**
     * @description 移除工具生成的锚点  ### 2、Linux 安装RocketMq 控制台
     * 变成 了### {#2Linux_RocketMq__56}2、Linux 安装RocketMq 控制台
     * @param markdown
     * @return java.lang.String
     * @author shian
     * @date 2024/7/22 16:39
     */
    private static String removeAnchorTags(String markdown) {
        // 正则表达式用于匹配形如{#...}的锚点标记
        Pattern anchorPattern = Pattern.compile("\\{#.*?\\}");
        Matcher matcher = anchorPattern.matcher(markdown);
        return matcher.replaceAll("");
    }
}
