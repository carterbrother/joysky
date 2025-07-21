#!/bin/bash

# 接口测试脚本 - 测试重试功能接口（GET方法）
# 用法: ./retryTest.sh [base_url] [book_id] [retry_times]
# 示例: ./retryTest.sh http://localhost:8083 300 3

# 参数检查
if [ $# -lt 2 ]; then
    echo "Usage: $0 [base_url] [book_id] [retry_times=3]"
    echo "Example: $0 http://localhost:8083 300 3"
    exit 1
fi

BASE_URL=$1
BOOK_ID=$2
MAX_RETRIES=${3:-3} # 默认重试3次
API_PATH="/api/books/$BOOK_ID/unstable"

# 统计变量
total_attempts=0
success_count=0
failure_count=0

echo "开始测试重试功能接口: $BASE_URL$API_PATH"
echo "最大重试次数: $MAX_RETRIES"
echo "----------------------------------------"

for ((i=1; i<=$MAX_RETRIES; i++)); do
    echo "尝试 #$i:"

    # 发送GET请求
    response=$(curl -s -X GET "$BASE_URL$API_PATH" -H "Content-Type: application/json")

    # 检查curl命令是否成功执行
    if [ $? -ne 0 ]; then
        echo "  HTTP请求失败"
        failure_count=$((failure_count + 1))
        continue
    fi

    # 解析响应
    status=$(echo "$response" | jq -r '.code' 2>/dev/null)
    message=$(echo "$response" | jq -r '.message' 2>/dev/null)

    if [ "$status" == "200" ]; then
        echo "  成功: 获取到书籍信息"
        book_title=$(echo "$response" | jq -r '.data.title' 2>/dev/null)
        echo "  书籍标题: $book_title"
        success_count=$((success_count + 1))
    else
        echo "  失败: $message"
        failure_count=$((failure_count + 1))
    fi

    echo "----------------------------------------"
    sleep 1 # 每次请求间隔1秒
done

# 输出统计结果
echo "测试完成"
echo "总尝试次数: $MAX_RETRIES"
echo "成功次数: $success_count"
echo "失败次数: $failure_count"
echo "成功率: $((success_count * 100 / MAX_RETRIES))%"

if [ $success_count -eq 0 ]; then
    exit 1 # 如果全部失败，返回非0状态码
else
    exit 0
fi
