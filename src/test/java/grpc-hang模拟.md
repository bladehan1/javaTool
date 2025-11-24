1. GrpcHang2Test和GrpcHang3Test 区别：GrpcHang3Test中没有Junit超时
2. 测试方式 
   3. ```
      chmod +x hang2Test.sh; 
      nohup ./hang2Test.sh &
      //观察 hang-report.log 日志有"HANG/TIMEOUT" 说明是hang主触发了超时
      //或者更新脚本为测试 GrpcHang3Test; 
      //GrpcHang2Test->GrpcHang3Test
      ```