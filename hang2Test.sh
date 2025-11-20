#!/bin/bash

TEST_CLASS="GrpcHangTest"
MAX_RUNS=10000

echo "==== New Test Run $(date) ====" >> hang-report.log
./gradlew clean compileJava compileTestJava
for i in $(seq 1 $MAX_RUNS); do
    echo "[$i/$MAX_RUNS] Running test..."
    ./gradlew cleanTest test --tests GrpcHang2Test  --no-rebuild

    # 判断上一次执行是否产生了 timeout 记录
    if tail -n 1 hang-report.log | grep -q "HANG/TIMEOUT"; then
        echo "==> Detected HANG at run $i"
    fi
done

echo "==== Done ====" >> hang-report.log
