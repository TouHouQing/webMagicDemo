package com.touhouqing.webmagicdemo.component;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.touhouqing.webmagicdemo.entity.AnnouncementInfo;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 详情页处理器
 * 专门用于处理详情页内容，提取招标公告信息
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Component
public class DetailPageProcessor implements PageProcessor {

    @Override
    public void process(Page page) {
        // 获取传递的链接ID
        Map<String, Object> extras = page.getRequest().getExtras();
        Long linkId = (Long) extras.get("linkId");
        
        Html html = page.getHtml();
        String url = page.getUrl().get();

        System.out.println("开始处理详情页: " + url);

        // 获取页面HTML内容
        String htmlContent = html.get();
        System.out.println("HTML内容长度: " + htmlContent.length());

        // 如果HTML内容太短，直接返回
        if (htmlContent.length() < 100) {
            System.out.println("HTML内容太短，跳过处理");
            return;
        }

        // 直接从HTML内容中提取信息，而不依赖WebMagic的文本提取
        String pageText = htmlContent;

        // 提取标题 - 使用正则表达式从HTML内容中直接提取
        String title = "";

        // 方法1：查找包含"公开招标公告"的strong标签内容
        Pattern strongTitlePattern = Pattern.compile("<strong[^>]*>([^<]*公开招标公告[^<]*)</strong>");
        Matcher strongTitleMatcher = strongTitlePattern.matcher(htmlContent);
        if (strongTitleMatcher.find()) {
            title = strongTitleMatcher.group(1).trim();
            System.out.println("从Strong标签提取到标题: " + title);
        }

        // 方法2：从meta标签的ArticleTitle中提取
        if (StringUtils.isBlank(title)) {
            Pattern metaTitlePattern = Pattern.compile("name=\"ArticleTitle\"\\s+content=\"([^\"]+)\"");
            Matcher metaTitleMatcher = metaTitlePattern.matcher(htmlContent);
            if (metaTitleMatcher.find()) {
                String metaTitle = metaTitleMatcher.group(1).trim();
                // 清理标题，只保留实际的标题内容
                if (metaTitle.contains("公开招标公告")) {
                    title = metaTitle;
                    System.out.println("从Meta标签提取到标题: " + title);
                }
            }
        }

        // 方法3：查找页面中的标题模式
        if (StringUtils.isBlank(title)) {
            Pattern titlePattern = Pattern.compile("([^\\n\\r<>]{10,100}公开招标公告)");
            Matcher titleMatcher = titlePattern.matcher(htmlContent);
            if (titleMatcher.find()) {
                title = titleMatcher.group(1).trim();
                System.out.println("从页面内容提取到标题: " + title);
            }
        }

        // 方法4：从title标签获取
        if (StringUtils.isBlank(title)) {
            Pattern titleTagPattern = Pattern.compile("<title[^>]*>([^<]+)</title>");
            Matcher titleTagMatcher = titleTagPattern.matcher(htmlContent);
            if (titleTagMatcher.find()) {
                title = titleTagMatcher.group(1).trim();
                System.out.println("从Title标签提取到标题: " + title);
            }
        }

        // 清理标题，移除可能的HTML标签残留
        if (StringUtils.isNotBlank(title)) {
            title = title.replaceAll("<[^>]+>", "").trim();
            title = title.replaceAll("\\s+", " ").trim();
        }

        System.out.println("提取到标题: " + title);

        // 提取发布日期
        String publishDate = "";
        Pattern datePattern = Pattern.compile("发布日期：(\\d{4}年\\d{2}月\\d{2}日)");
        Matcher dateMatcher = datePattern.matcher(htmlContent);
        if (dateMatcher.find()) {
            publishDate = dateMatcher.group(1);
            System.out.println("找到发布日期: " + publishDate);
        } else {
            // 尝试其他日期格式
            Pattern datePattern2 = Pattern.compile("(\\d{4}年\\d{2}月\\d{2}日)");
            Matcher dateMatcher2 = datePattern2.matcher(htmlContent);
            if (dateMatcher2.find()) {
                publishDate = dateMatcher2.group(1);
                System.out.println("找到发布日期(格式2): " + publishDate);
            }
        }

        // 提取发布来源
        String publishSource = "";
        Pattern sourcePattern = Pattern.compile("发布来源：([^\\s\\n\\r<>]+)");
        Matcher sourceMatcher = sourcePattern.matcher(htmlContent);
        if (sourceMatcher.find()) {
            publishSource = sourceMatcher.group(1);
            System.out.println("找到发布来源: " + publishSource);
        }

        // 提取项目编号
        String projectNumber = "";
        Pattern numberPattern = Pattern.compile("项目编号：([^\\s\\n\\r\\)<>]+)");
        Matcher numberMatcher = numberPattern.matcher(htmlContent);
        if (numberMatcher.find()) {
            projectNumber = numberMatcher.group(1);
            System.out.println("找到项目编号: " + projectNumber);
        }

        // 提取项目名称
        String projectName = "";
        Pattern namePattern = Pattern.compile("项目名称：([^\\n\\r<>]+)");
        Matcher nameMatcher = namePattern.matcher(htmlContent);
        if (nameMatcher.find()) {
            projectName = nameMatcher.group(1).trim();
            System.out.println("找到项目名称: " + projectName);
        }

        // 提取预算金额
        BigDecimal budgetAmount = null;
        Pattern budgetPattern = Pattern.compile("预算金额：([\\d\\.]+)万元");
        Matcher budgetMatcher = budgetPattern.matcher(htmlContent);
        if (budgetMatcher.find()) {
            try {
                budgetAmount = new BigDecimal(budgetMatcher.group(1));
                System.out.println("找到预算金额: " + budgetAmount);
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        } else {
            // 尝试其他格式
            Pattern budgetPattern2 = Pattern.compile("预算.*?([\\d\\.]+)万元");
            Matcher budgetMatcher2 = budgetPattern2.matcher(htmlContent);
            if (budgetMatcher2.find()) {
                try {
                    budgetAmount = new BigDecimal(budgetMatcher2.group(1));
                    System.out.println("找到预算金额(格式2): " + budgetAmount);
                } catch (NumberFormatException e) {
                    // 忽略解析错误
                }
            }
        }

        // 提取最高限价
        BigDecimal maxPrice = null;
        Pattern maxPricePattern = Pattern.compile("最高限价：([\\d\\.]+)万元");
        Matcher maxPriceMatcher = maxPricePattern.matcher(htmlContent);
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
        Pattern contractPattern = Pattern.compile("合同履行期限：([^\\n\\r。<>]+)");
        Matcher contractMatcher = contractPattern.matcher(htmlContent);
        if (contractMatcher.find()) {
            contractPeriod = contractMatcher.group(1).trim();
            System.out.println("找到合同履行期限: " + contractPeriod);
        }

        // 提取采购需求
        String procurementRequirements = "";
        Pattern requirementPattern = Pattern.compile("采购需求：([^\\n\\r<>]+)");
        Matcher requirementMatcher = requirementPattern.matcher(htmlContent);
        if (requirementMatcher.find()) {
            procurementRequirements = requirementMatcher.group(1).trim();
            System.out.println("找到采购需求: " + procurementRequirements);
        }

        // 提取联系方式
        String contactInfo = "";
        Pattern contactPattern = Pattern.compile("联系方式：([^\\n\\r<>]+)");
        Matcher contactMatcher = contactPattern.matcher(htmlContent);
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

        // 交给pipeline来处理，同时传递linkId用于标记已处理
        page.putField("announcementInfo", announcementInfo);
        page.putField("linkId", linkId);

        System.out.println("详情页处理完成: " + title);
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(3)
                .setSleepTime(1000)
                .setTimeOut(10000)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
    }
}
