package com.touhouqing.webmagicdemo.component;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.touhouqing.webmagicdemo.entity.DetailedProcurementInfo;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 相关公告页面处理器
 * 专门用于处理相关公告页面，提取详细的采购信息
 * 
 * @author TouHouQing
 * @since 2025-07-22
 */
@Component
public class RelatedAnnouncementPageProcessor implements PageProcessor {

    @Override
    public void process(Page page) {
        // 获取传递的链接ID
        Map<String, Object> extras = page.getRequest().getExtras();
        Long relatedLinkId = (Long) extras.get("relatedLinkId");
        
        Html html = page.getHtml();
        String url = page.getUrl().get();

        System.out.println("开始处理相关公告页面: " + url);

        // 获取页面HTML内容
        String htmlContent = html.get();
        System.out.println("HTML内容长度: " + htmlContent.length());
        
        // 如果HTML内容太短，直接返回
        if (htmlContent.length() < 100) {
            System.out.println("HTML内容太短，跳过处理");
            return;
        }

        // 提取相关公告ID
        String relatedAnnouncementId = extractIdFromUrl(url);

        // 提取标题
        String title = extractTitle(htmlContent);
        System.out.println("提取到标题: " + title);

        // 提取发布日期
        String publishDate = extractPublishDate(htmlContent);
        System.out.println("提取到发布日期: " + publishDate);

        // 提取采购单位
        String procurementUnit = extractProcurementUnit(htmlContent);
        System.out.println("提取到采购单位: " + procurementUnit);

        // 提取表格中的详细采购信息
        extractTableData(htmlContent, relatedAnnouncementId, title, publishDate, procurementUnit, url, relatedLinkId, page);

        System.out.println("相关公告页面处理完成: " + title);
    }

    /**
     * 提取标题
     */
    private String extractTitle(String htmlContent) {
        // 方法1：从meta标签的ArticleTitle中提取
        Pattern metaTitlePattern = Pattern.compile("name=\"ArticleTitle\"\\s+content=\"([^\"]+)\"");
        Matcher metaTitleMatcher = metaTitlePattern.matcher(htmlContent);
        if (metaTitleMatcher.find()) {
            String title = metaTitleMatcher.group(1).trim();
            return cleanHtmlContent(title);
        }

        // 方法2：查找页面中的主标题（通常在h1、h2标签或特定位置）
        Pattern mainTitlePattern = Pattern.compile("([^\\n\\r<>]{10,100}政府采购意向公告)");
        Matcher mainTitleMatcher = mainTitlePattern.matcher(htmlContent);
        if (mainTitleMatcher.find()) {
            String title = mainTitleMatcher.group(1).trim();
            return cleanHtmlContent(title);
        }

        // 方法3：查找包含"意向公告"的标题
        Pattern intentionPattern = Pattern.compile("([^\\n\\r<>]{10,100}意向公告)");
        Matcher intentionMatcher = intentionPattern.matcher(htmlContent);
        if (intentionMatcher.find()) {
            String title = intentionMatcher.group(1).trim();
            return cleanHtmlContent(title);
        }

        // 方法4：从title标签获取
        Pattern titleTagPattern = Pattern.compile("<title[^>]*>([^<]+)</title>");
        Matcher titleTagMatcher = titleTagPattern.matcher(htmlContent);
        if (titleTagMatcher.find()) {
            String title = titleTagMatcher.group(1).trim();
            return cleanHtmlContent(title);
        }

        // 方法5：查找页面中的任何包含"公告"的标题
        Pattern generalTitlePattern = Pattern.compile("([^\\n\\r<>]{10,100}公告)");
        Matcher generalTitleMatcher = generalTitlePattern.matcher(htmlContent);
        if (generalTitleMatcher.find()) {
            String title = generalTitleMatcher.group(1).trim();
            return cleanHtmlContent(title);
        }

        return "";
    }

    /**
     * 提取发布日期
     */
    private String extractPublishDate(String htmlContent) {
        Pattern datePattern = Pattern.compile("发布日期：(\\d{4}年\\d{2}月\\d{2}日)");
        Matcher dateMatcher = datePattern.matcher(htmlContent);
        if (dateMatcher.find()) {
            return dateMatcher.group(1);
        }
        
        // 尝试其他日期格式
        Pattern datePattern2 = Pattern.compile("(\\d{4}年\\d{2}月\\d{2}日)");
        Matcher dateMatcher2 = datePattern2.matcher(htmlContent);
        if (dateMatcher2.find()) {
            return dateMatcher2.group(1);
        }
        
        return "";
    }

    /**
     * 提取采购单位
     */
    private String extractProcurementUnit(String htmlContent) {
        Pattern unitPattern = Pattern.compile("发布来源：([^\\n\\r<>]+)");
        Matcher unitMatcher = unitPattern.matcher(htmlContent);
        if (unitMatcher.find()) {
            return cleanHtmlContent(unitMatcher.group(1));
        }
        return "";
    }

    /**
     * 提取表格数据
     */
    private void extractTableData(String htmlContent, String relatedAnnouncementId, String title, 
                                 String publishDate, String procurementUnit, String url, 
                                 Long relatedLinkId, Page page) {
        
        // 查找表格行的模式
        Pattern tableRowPattern = Pattern.compile("<tr[^>]*>.*?</tr>", Pattern.DOTALL);
        Matcher tableRowMatcher = tableRowPattern.matcher(htmlContent);
        
        int rowIndex = 0;
        while (tableRowMatcher.find()) {
            String rowHtml = tableRowMatcher.group();
            
            // 跳过表头行
            if (rowHtml.contains("序号") || rowHtml.contains("采购项目名称") || rowIndex == 0) {
                rowIndex++;
                continue;
            }
            
            // 提取表格单元格数据
            Pattern cellPattern = Pattern.compile("<td[^>]*>(.*?)</td>", Pattern.DOTALL);
            Matcher cellMatcher = cellPattern.matcher(rowHtml);
            
            String[] cells = new String[7]; // 序号、采购项目名称、采购需求概况、预算金额、预计采购时间、执行的政府采购政策、备注
            int cellIndex = 0;
            
            while (cellMatcher.find() && cellIndex < cells.length) {
                cells[cellIndex] = cleanHtmlContent(cellMatcher.group(1));
                cellIndex++;
            }
            
            // 如果提取到了有效数据，创建详细采购信息对象
            if (cellIndex >= 4 && StringUtils.isNotBlank(cells[1])) { // 至少要有项目名称
                DetailedProcurementInfo procurementInfo = new DetailedProcurementInfo();
                procurementInfo.setRelatedAnnouncementId(relatedAnnouncementId + "_" + rowIndex); // 使用行索引区分同一页面的多个项目
                procurementInfo.setTitle(title);
                procurementInfo.setProcurementProjectName(cells[1]); // 采购项目名称
                procurementInfo.setProcurementRequirements(cells[2]); // 采购需求概况
                
                // 解析预算金额
                if (StringUtils.isNotBlank(cells[3])) {
                    try {
                        String budgetStr = cells[3].replaceAll("[^\\d\\.]", "");
                        if (StringUtils.isNotBlank(budgetStr)) {
                            procurementInfo.setBudgetAmount(new BigDecimal(budgetStr));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("预算金额解析失败: " + cells[3]);
                    }
                }
                
                procurementInfo.setEstimatedTime(cells[4]); // 预计采购时间
                procurementInfo.setPolicyExecution(cellIndex > 5 ? cells[5] : ""); // 执行的政府采购政策
                procurementInfo.setRemark(cellIndex > 6 ? cells[6] : ""); // 备注
                procurementInfo.setProcurementUnit(procurementUnit);
                procurementInfo.setPublishDate(publishDate);
                procurementInfo.setDetailUrl(url);
                
                // 将采购信息添加到页面结果中
                page.putField("procurementInfo_" + rowIndex, procurementInfo);
                
                System.out.println("提取到采购项目: " + procurementInfo.getProcurementProjectName() + 
                                 ", 预算: " + procurementInfo.getBudgetAmount() + "万元");
            }
            
            rowIndex++;
        }
        
        // 传递相关链接ID和项目数量
        page.putField("relatedLinkId", relatedLinkId);
        page.putField("projectCount", rowIndex - 1);
    }

    /**
     * 从URL中提取ID
     */
    private String extractIdFromUrl(String url) {
        Pattern idPattern = Pattern.compile("id=(\\d+)");
        Matcher idMatcher = idPattern.matcher(url);
        if (idMatcher.find()) {
            return idMatcher.group(1);
        }
        return "";
    }

    /**
     * 清理HTML标签和实体
     */
    private String cleanHtmlContent(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        return content.replaceAll("<[^>]+>", "")
                     .replaceAll("&nbsp;", " ")
                     .replaceAll("&amp;", "&")
                     .replaceAll("&lt;", "<")
                     .replaceAll("&gt;", ">")
                     .replaceAll("&quot;", "\"")
                     .replaceAll("&apos;", "'")
                     .replaceAll("\\s+", " ")
                     .trim();
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
