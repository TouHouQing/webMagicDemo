package com.touhouqing.webmagicdemo.util;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

/**
 * 页面调试工具
 * 用于调试页面解析问题
 */
public class PageDebugger implements PageProcessor {

    public static void main(String[] args) {
        String url = "http://www.ccgp-tianjin.gov.cn/portal/documentView.do?method=view&id=750176329&ver=2";
        
        Spider.create(new PageDebugger())
                .addUrl(url)
                .run();
    }

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        String url = page.getUrl().get();

        System.out.println("=== 调试页面: " + url + " ===");

        // 获取页面所有文本内容
        String pageText = html.xpath("//body//text()").all().toString();
        System.out.println("页面文本长度: " + pageText.length());
        System.out.println("页面文本前500字符: " + pageText.substring(0, Math.min(500, pageText.length())));

        // 测试不同的标题提取方法
        System.out.println("\n=== 标题提取测试 ===");

        // 方法1：strong标签
        List<String> strongTexts = html.$("strong").xpath("//text()").all();
        System.out.println("Strong标签数量: " + strongTexts.size());
        for (int i = 0; i < Math.min(5, strongTexts.size()); i++) {
            System.out.println("Strong[" + i + "]: " + strongTexts.get(i));
        }

        // 方法2：查找包含"公开招标公告"的文本
        String[] lines = pageText.split("\\n");
        System.out.println("页面行数: " + lines.length);
        for (String line : lines) {
            line = line.trim();
            if (line.contains("公开招标公告") && line.length() > 10) {
                System.out.println("找到招标公告标题: " + line);
            }
        }

        // 方法3：title标签
        String titleText = html.$("title").xpath("//text()").get();
        System.out.println("Title标签: " + titleText);

        // 方法4：h1, h2, h3标签
        List<String> headerTexts = html.$("h1, h2, h3").xpath("//text()").all();
        System.out.println("Header标签数量: " + headerTexts.size());
        for (String header : headerTexts) {
            System.out.println("Header: " + header);
        }

        // 方法5：查找所有包含"项目"的文本
        for (String line : lines) {
            line = line.trim();
            if (line.contains("项目") && line.length() > 10 && line.length() < 100) {
                System.out.println("包含项目的行: " + line);
            }
        }

        System.out.println("=== 调试完成 ===");
    }

    @Override
    public us.codecraft.webmagic.Site getSite() {
        return us.codecraft.webmagic.Site.me()
                .setRetryTimes(3)
                .setSleepTime(1000)
                .setTimeOut(10000)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
    }
}
