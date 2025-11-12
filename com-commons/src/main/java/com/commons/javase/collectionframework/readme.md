Java集合大致可分成List、Set、Queue、Map四种接口体系。
    List代表了有序可重复集合，保留插入顺序，并允许通过索引（index）来访问元素
    Set代表无序不可重复集合，只能根据元素本身来访问
    Queue是队列集合
    Map代表的是存储key-value对的集合，可根据元素的key来访问value

1、List
    ArrayList 
        基于动态数组数据结构
        支持动态扩容
        允许快速随机访问元素时间复杂度为 O(1)
        插入和删除操作较慢
        适用于：频繁随机访问需要较高读取效率的场景
    LinkedList 
        基于双向链表实现的 List
        允许高效地进行插入和删除操作
        元素存储在双向链表中（不像 ArrayList 那样使用连续的数组空间）
        随机访问时效率较低（时间复杂度为 O(n)因为需要从头或尾开始遍历）
        插入和删除操作效率较高
        适用于：需要频繁插入、删除元素的场景
    Vector
        类似于 ArrayList 的实现，操作方法都使用了 synchronized，线程安全的
        随机访问效率高（和 ArrayList 相似）
        插入和删除操作较慢
        适用于：少量数据，且对线程安全有严格要求的场景（不建议在现代开发中使用它，推荐使用 CopyOnWriteArrayList 或其他并发集合）。
    Stack Stack 是 Vector 的一个子类，实现了 LIFO（后进先出）规则的栈数据结构
        扩展自 Vector，是同步的
        主要用于模拟栈结构操作：push()、pop()、peek()
    CopyOnWriteArrayList
        CopyOnWriteArrayList 是线程安全的动态数组，所有的修改操作（如 add、set）会创建一个新的数组。同时，它锁定操作时只对写操作加锁，允许多个线程同时进行读操作，从而提高并发效率
        线程安全：适用于高并发场景
        读取性能高，但写入性能低（因每次写入都会复制一个副本）
        读操作多写操作少的场景，如读多写少的缓存
    List 的常用操作
        boolean add(E e)	在 List 末尾添加元素
        void add(int index, E e)	在指定索引处插入元素
        boolean addAll(Collection<? extends E>)	将指定集合的所有元素添加到当前集合
        E get(int index)	获取指定索引的元素
        E set(int index, E element)	替换指定索引的元素，返回原来的值
        boolean remove(Object o)	删除列表中第一次出现的指定元素
        E remove(int index)	删除并返回指定索引处的元素
        boolean contains(Object o)	判断集合中是否存在指定的元素
        int indexOf(Object o)	返回元素的首个索引，不存在则返回 -1
        int lastIndexOf(Object o)	返回元素的最后一个索引，不存在则返回 -1
        int size()	获取集合的大小
        void clear()	清空集合
        boolean isEmpty()	判断集合是否为空
        Object[] toArray()	将集合转换为数组