#!/bin/bash

# 两阶段爬虫演示脚本
# 使用说明：确保应用已启动在 http://localhost:8080

echo "=== 两阶段爬虫演示脚本 ==="
echo ""

# 基础URL
BASE_URL="http://localhost:8080/announcement"

# 函数：检查应用是否启动
check_app() {
    echo "检查应用是否启动..."
    if curl -s "$BASE_URL/count" > /dev/null; then
        echo "✓ 应用已启动"
        return 0
    else
        echo "✗ 应用未启动，请先启动应用"
        return 1
    fi
}

# 函数：显示当前状态
show_status() {
    echo ""
    echo "=== 当前状态 ==="
    echo "招标公告数量："
    curl -s "$BASE_URL/count"
    echo ""
    echo "链接状态："
    curl -s "$BASE_URL/link-status"
    echo ""
}

# 函数：第一阶段 - 收集链接
collect_links() {
    echo ""
    echo "=== 第一阶段：收集链接 ==="
    echo "开始从列表页收集详情页链接..."
    
    response=$(curl -s "$BASE_URL/collect-links")
    echo "响应：$response"
    
    echo "等待5秒让收集任务完成..."
    sleep 5
    
    echo "收集完成后的状态："
    curl -s "$BASE_URL/link-status"
    echo ""
}

# 函数：第二阶段 - 抓取详情
crawl_details() {
    echo ""
    echo "=== 第二阶段：抓取详情 ==="
    echo "开始抓取详情页信息（批次大小：5）..."
    
    response=$(curl -s "$BASE_URL/crawl-details?batchSize=5")
    echo "响应：$response"
    
    echo "等待10秒让抓取任务完成..."
    sleep 10
    
    echo "抓取完成后的状态："
    show_status
}

# 主流程
main() {
    # 检查应用状态
    if ! check_app; then
        exit 1
    fi
    
    # 显示初始状态
    echo ""
    echo "=== 初始状态 ==="
    show_status
    
    # 询问用户是否继续
    echo ""
    read -p "是否开始两阶段爬虫演示？(y/n): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "演示取消"
        exit 0
    fi
    
    # 第一阶段：收集链接
    collect_links
    
    # 询问是否继续第二阶段
    echo ""
    read -p "是否继续第二阶段（抓取详情）？(y/n): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "演示结束"
        exit 0
    fi
    
    # 第二阶段：抓取详情
    crawl_details
    
    echo ""
    echo "=== 演示完成 ==="
    echo "您可以访问以下URL查看结果："
    echo "- 招标公告列表：$BASE_URL/list"
    echo "- 链接列表：$BASE_URL/links"
    echo "- 状态统计：$BASE_URL/link-status"
}

# 运行主流程
main
