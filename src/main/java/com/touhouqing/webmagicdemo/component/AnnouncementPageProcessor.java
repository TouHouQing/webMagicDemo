package com.touhouqing.webmagicdemo.component;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.touhouqing.webmagicdemo.entity.AnnouncementInfo;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AnnouncementPageProcessor implements PageProcessor {

    public void process(Page page) {
        // 上下文 可以传递一些数据
        Map<String, Object> extras = page.getRequest().getExtras();
        String level = extras.get("level").toString();

        switch (level){
            case "list":
                parseList(page);
                break;
            case "detail":
                parseDetail(page);
                break;
        }
    }

    /**
     * 解析详情页
     *
     * @param page
     */
    private void parseDetail(Page page) {
        Html html = page.getHtml();
        String url = page.getUrl().get();

        // 获取页面所有文本内容
        String pageText = html.xpath("//body//text()").all().toString();

        // 提取标题 - 从页面中的粗体标题获取
        String title = "";
        List<String> titleCandidates = html.$("strong").xpath("//text()").all();
        if (!titleCandidates.isEmpty()) {
            title = titleCandidates.get(0);
        }
        if (StringUtils.isBlank(title)) {
            // 尝试从页面标题获取
            title = html.$("title").xpath("//text()").get();
        }

        System.out.println("页面文本内容（前500字符）: " + pageText.substring(0, Math.min(500, pageText.length())));

        // 提取发布日期
        String publishDate = "";
        Pattern datePattern = Pattern.compile("发布日期：(\\d{4}年\\d{2}月\\d{2}日)");
        Matcher dateMatcher = datePattern.matcher(pageText);
        if (dateMatcher.find()) {
            publishDate = dateMatcher.group(1);
            System.out.println("找到发布日期: " + publishDate);
        }

        // 提取发布来源
        String publishSource = "";
        Pattern sourcePattern = Pattern.compile("发布来源：([^\\s\\n\\r]+)");
        Matcher sourceMatcher = sourcePattern.matcher(pageText);
        if (sourceMatcher.find()) {
            publishSource = sourceMatcher.group(1);
            System.out.println("找到发布来源: " + publishSource);
        }

        // 提取项目编号
        String projectNumber = "";
        Pattern numberPattern = Pattern.compile("项目编号：([^\\s\\n\\r\\)]+)");
        Matcher numberMatcher = numberPattern.matcher(pageText);
        if (numberMatcher.find()) {
            projectNumber = numberMatcher.group(1);
            System.out.println("找到项目编号: " + projectNumber);
        }

        // 提取项目名称
        String projectName = "";
        Pattern namePattern = Pattern.compile("项目名称：([^\\n\\r]+)");
        Matcher nameMatcher = namePattern.matcher(pageText);
        if (nameMatcher.find()) {
            projectName = nameMatcher.group(1).trim();
            System.out.println("找到项目名称: " + projectName);
        }

        // 提取预算金额
        BigDecimal budgetAmount = null;
        Pattern budgetPattern = Pattern.compile("预算金额：([\\d\\.]+)万元");
        Matcher budgetMatcher = budgetPattern.matcher(pageText);
        if (budgetMatcher.find()) {
            try {
                budgetAmount = new BigDecimal(budgetMatcher.group(1));
                System.out.println("找到预算金额: " + budgetAmount);
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }

        // 提取最高限价
        BigDecimal maxPrice = null;
        Pattern maxPricePattern = Pattern.compile("最高限价：([\\d\\.]+)万元");
        Matcher maxPriceMatcher = maxPricePattern.matcher(pageText);
        if (maxPriceMatcher.find()) {
            try {
                maxPrice = new BigDecimal(maxPriceMatcher.group(1));
                System.out.println("找到最高限价: " + maxPrice);
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }

        // 提取合同履行期限
        String contractPeriod = "";
        Pattern contractPattern = Pattern.compile("合同履行期限：([^\\n\\r。]+)");
        Matcher contractMatcher = contractPattern.matcher(pageText);
        if (contractMatcher.find()) {
            contractPeriod = contractMatcher.group(1).trim();
            System.out.println("找到合同履行期限: " + contractPeriod);
        }

        // 提取采购需求
        String procurementRequirements = "";
        Pattern requirementPattern = Pattern.compile("采购需求：([^\\n\\r]+)");
        Matcher requirementMatcher = requirementPattern.matcher(pageText);
        if (requirementMatcher.find()) {
            procurementRequirements = requirementMatcher.group(1).trim();
            System.out.println("找到采购需求: " + procurementRequirements);
        }

        // 提取联系方式
        String contactInfo = "";
        Pattern contactPattern = Pattern.compile("联系方式：([^\\n\\r]+)");
        Matcher contactMatcher = contactPattern.matcher(pageText);
        if (contactMatcher.find()) {
            contactInfo = contactMatcher.group(1).trim();
            System.out.println("找到联系方式: " + contactInfo);
        }

        // 创建招标公告对象
        AnnouncementInfo announcementInfo = new AnnouncementInfo();
        announcementInfo.setTitle(title);
        announcementInfo.setProjectNumber(projectNumber);
        announcementInfo.setProjectName(projectName);
        announcementInfo.setPurchaseUnit(publishSource);
        announcementInfo.setBudgetAmount(budgetAmount);
        announcementInfo.setMaxPrice(maxPrice);
        announcementInfo.setPublishDate(publishDate);
        announcementInfo.setPublishSource(publishSource);
        announcementInfo.setDetailUrl(url);
        announcementInfo.setContractPeriod(contractPeriod);
        announcementInfo.setProcurementRequirements(procurementRequirements);
        announcementInfo.setContactInfo(contactInfo);

        // 交给pipeline来处理
        page.putField("announcementInfo", announcementInfo);
    }

    /**
     * 解析列表页
     *
     * @param page
     */
    private void parseList(Page page) {
        Html html = page.getHtml();

        // 获取所有的招标公告链接 - 使用更精确的选择器
        List<String> urlList = html.$("li a[href*='/viewer.do?id=']").links().all();

        System.out.println("找到 " + urlList.size() + " 个详情页链接");

        for (String url : urlList) {
            // 从viewer.do链接中提取ID，然后构造documentView.do链接
            if (url.contains("/viewer.do?id=")) {
                // 提取ID
                String id = url.substring(url.indexOf("id=") + 3);
                if (id.contains("&")) {
                    id = id.substring(0, id.indexOf("&"));
                }

                // 构造新的详情页URL
                String detailUrl = "http://www.ccgp-tianjin.gov.cn/portal/documentView.do?method=view&id=" + id + "&ver=2";

                System.out.println("准备爬取详情页: " + detailUrl);

                Request request = new Request(detailUrl);
                Map<String, Object> map = new HashMap<>();
                map.put("level", "detail"); // 标识位
                request.setExtras(map);
                setGetHeaders(request);
                page.addTargetRequest(request);
            }
        }
    }

    public Site getSite() {
        return Site.me().setRetryTimes(3).setSleepTime(1000);
    }

    /**
     * 添加请求伪装
     *
     * @param request
     */
    public void setGetHeaders(Request request){
        request.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        request.addHeader("Accept-Encoding","gzip, deflate");
        request.addHeader("Accept-Language","zh-CN,zh;q=0.9");
        request.addHeader("Cache-Control","max-age=0");
        request.addHeader("Connection","keep-alive");
        request.addHeader("Host","www.ccgp-tianjin.gov.cn");
        request.addHeader("Upgrade-Insecure-Requests","1");
        request.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
    }
}
