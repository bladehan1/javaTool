#!/bin/bash

TEST_CLASS="GrpcHangTest"
MAX_RUNS=10000

echo "==== New Test Run $(date) ====" >> fix-report.log
./gradlew clean compileJava compileTestJava
for i in $(seq 1 $MAX_RUNS); do
    echo "[$i/$MAX_RUNS] Running test..."
    ./gradlew cleanTest test --tests GrpcFixTest  --no-rebuild

    # 判断上一次执行是否产生了 timeout 记录
    if tail -n 1 fix-report.log | grep -q "HANG/TIMEOUT"; then
        echo "==> Detected HANG at run $i"
    fi
done

echo "==== Done ====" >> fix-report.log
