this.disruptor = new Disruptor<>(
    new MessageEventFactory(), 
    BUFFER_SIZE, DaemonThreadFactory.INSTANCE, 
    ProducerType.MULTI, 
    new BlockingWaitStrategy()
);
环行队列初始化会预分配内存，若BUFFER_SIZE过大，可能增加 GC 压力；
