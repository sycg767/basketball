@echo off
echo ========================================
echo 测试管理后台数据分析API
echo ========================================
echo.

echo [1/4] 测试会员活跃度分析API...
curl -s "http://localhost:8080/api/admin/analytics/member/active-users?minScore=60" -H "Authorization: Bearer test"
echo.
echo.

echo [2/4] 测试课程热度分析API...
curl -s "http://localhost:8080/api/admin/analytics/course/hot-courses?minScore=60" -H "Authorization: Bearer test"
echo.
echo.

echo [3/4] 测试场地使用分析API...
curl -s "http://localhost:8080/api/admin/analytics/venue/high-usage?minRate=70" -H "Authorization: Bearer test"
echo.
echo.

echo [4/4] 手动触发分析（生成数据）...
echo 触发会员活跃度分析...
curl -X POST "http://localhost:8080/api/admin/analytics/member/analyze?analysisDate=2025-01-22" -H "Authorization: Bearer test"
echo.

echo 触发课程热度分析...
curl -X POST "http://localhost:8080/api/admin/analytics/course/analyze?analysisDate=2025-01-22" -H "Authorization: Bearer test"
echo.

echo 触发场地使用分析...
curl -X POST "http://localhost:8080/api/admin/analytics/venue/analyze?analysisDate=2025-01-22" -H "Authorization: Bearer test"
echo.
echo.

echo ========================================
echo 测试完成！
echo ========================================
echo.
echo 提示：如果返回空数组，说明数据库表为空
echo 解决方案：
echo 1. 执行 init_analytics_data.sql 初始化数据
echo 2. 或等待上面的手动触发API生成数据
echo.
pause
