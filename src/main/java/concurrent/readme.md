# 并发类
- `java.util.concurrent`包 (`ExecutorService`, `ThreadPoolExecutor`, `Future`, `CompletableFuture`, `ConcurrentHashMap`, `ReentrantLock`, `ReadWriteLock`)
- **线程安全：** 原子类 (`AtomicInteger`等), `synchronized` (理解锁升级), `volatile`.
- **并发模型：** 对比Go的Goroutine+Channel和Java的线程池+并发集合+Future/CompletableFuture。理解`CompletableFuture`的异步编排能力。
- **JavaTron中的实践：** 节点如何管理交易处理线程？如何同步访问共享状态（账户、区块数据）？研究源码中的并发设计。