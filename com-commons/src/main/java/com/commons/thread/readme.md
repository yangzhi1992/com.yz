1. Java 线程分类：用户线程 和 守护线程
   用户线程（User Thread）： 默认线程是用户线程。 JVM 会一直运行，直到所有用户线程都执行完毕。
   守护线程（Daemon Thread）： 当所有的 用户线程 运行结束后，JVM 会立即退出，即使守护线程还在运行。 JVM 的垃圾回收线程是一个典型的守护线程。
2. 主线程与异步线程的关系
   情况 1：默认线程（非守护线程，用户线程） ->AsyncThreadExample
   如果你在 main 方法中创建的是普通线程（用户线程），并且 main 方法本身执行结束了，那 JVM 不会立即退出，而是 等待线程执行完毕 后再退出。
   情况 2：将线程设置为守护线程（Daemon Thread） ->DaemonThreadExample
   如果你在创建线程时，将其设置为 守护线程，当 main 方法执行完毕后，JVM 会立即退出，停止所有守护线程。
3. 如何确认线程是否完成
   Thread.join()
   CountDownLatch

