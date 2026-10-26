#!/usr/bin/env bash
# scripts/smoke-test.sh —— Week 06 微服务版验证脚本
# 从仓库根目录执行：bash scripts/smoke-test.sh  (或 .\scripts\smoke-test.sh 的 Git Bash)
# Windows 纯 PowerShell 用户可直接对照脚本中的 curl 命令执行。
set -euo pipefail

: "${PRODUCT_PORT:=8081}"
: "${USER_PORT:=8082}"
: "${ORDER_PORT:=8083}"
PRODUCT="http://localhost:${PRODUCT_PORT}"
USER="http://localhost:${USER_PORT}"
ORDER="http://localhost:${ORDER_PORT}"

pass=0; fail=0; total=0
check() {
    total=$((total+1))
    local name="$1"; shift
    if "$@" >/tmp/smoke.${total}.out 2>&1; then
        pass=$((pass+1))
        echo "  ✅ [$name]"
    else
        fail=$((fail+1))
        echo "  ❌ [$name]  -- 见 /tmp/smoke.${total}.out"
    fi
}

echo "== 1) 健康检查 =="
for name in product user order; do
    url_var="\$$(echo ${name^^}_BASE)"
done
check "product-service health" curl -sf "${PRODUCT}/actuator/health"
check "user-service   health"   curl -sf "${USER}/actuator/health"
check "order-service  health"  curl -sf "${ORDER}/actuator/health"

echo
echo "== 2) 演示数据 =="
check "创建演示用户" curl -sf -X POST "${USER}/api/users" \
    -H "Content-Type: application/json" \
    -d '{"userNo":"U1001","name":"测试用户","phone":"13800138000"}'
check "创建演示商品" curl -sf -X POST "${PRODUCT}/api/products" \
    -H "Content-Type: application/json" \
    -d '{"name":"Kitty 丝绒口红","category":"美妆","price":"99.00","stock":100,"onSale":true}'
# 记录新创建的 user id / product id（若之前已存在，id 从响应 JSON 中取）
USER_ID=$(curl -s "${USER}/api/users" | grep -oE '"id":[0-9]+' | tail -1 | sed 's/"id"://')
PRODUCT_ID=$(curl -s "${PRODUCT}/api/products" | grep -oE '"id":[0-9]+' | tail -1 | sed 's/"id"://')
echo "  user.id=${USER_ID}  product.id=${PRODUCT_ID}"

echo
echo "== 3) 跨服务下单成功场景 =="
check "下单(用户+商品存在，库存充足)" curl -sf -X POST "${ORDER}/api/orders" \
    -H "Content-Type: application/json" \
    -d "{\"userId\":${USER_ID},\"productId\":${PRODUCT_ID},\"quantity\":2}"
ORDER_ID=$(curl -s "${ORDER}/api/orders" | grep -oE '"id":[0-9]+' | tail -1 | sed 's/"id"://')
echo "  order.id=${ORDER_ID}"
check "查询刚创建的订单" curl -sf "${ORDER}/api/orders/${ORDER_ID}"

echo
echo "== 4) 失败场景：依赖资源不存在 / 不可用 =="
# 4a 用户不存在（user-service 返回 404）→ order-service 不写入本地数据
check "用户不存在 → order-service 应拒绝（本地无写入）" \
    bash -c "curl -s -o /tmp/smoke.err.json -w '%{http_code}' -X POST ${ORDER}/api/orders \
      -H 'Content-Type: application/json' \
      -d '{\"userId\":99999,\"productId\":${PRODUCT_ID},\"quantity\":1}' | grep -E '400|404|503'"

# 4b 商品不存在 → order-service 不写入本地
check "商品不存在 → order-service 应拒绝（本地无写入）" \
    bash -c "curl -s -o /tmp/smoke.err2.json -w '%{http_code}' -X POST ${ORDER}/api/orders \
      -H 'Content-Type: application/json' \
      -d '{\"userId\":${USER_ID},\"productId\":99999,\"quantity\":1}' | grep -E '400|404|503'"

echo
echo "== 总结：${pass}/${total} 通过，${fail} 失败 =="
[[ ${fail} -eq 0 ]]
