#!/bin/bash

TEST_CLASS="GrpcHangTest"
MAX_RUNS=10000
MAX_BACKUPS=2

echo "==== New Test Run $(date) ====" >> hang-report.log
./gradlew clean compileJava compileTestJava
rm -rf logs/bak-*
for i in $(seq 1 $MAX_RUNS); do
    echo "[$i/$MAX_RUNS] Running test..."
    # 备份上一次日志（从第二次循环开始）
    if [ $i -gt 1 ]; then
       prev_dir="logs/bak-$((i-1))"
       mkdir -p "$prev_dir"
       [ -f "logs/tool-test.log" ] && mv "logs/tool-test.log" "$prev_dir/"
       [ -f "logs/grpc-test.log" ] && mv "logs/grpc-test.log" "$prev_dir/"
    fi
     # 清理旧备份（保留最近5次）
    ls -1d logs/bak-* 2>/dev/null | sort -V | head -n -$MAX_BACKUPS | xargs rm -rf

    # 运行测试
    ./gradlew cleanTest test --tests GrpcHang4Test  --no-rebuild

    # 判断上一次执行是否产生了 timeout 记录
    if tail -n 1 hang-report.log | grep -q "HANG/TIMEOUT"; then
        echo "==> Detected HANG at run $i"
    fi
done

echo "==== Done ====" >> hang-report.log
