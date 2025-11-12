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
        当对 CopyOnWriteArrayList 执行修改（比如，add、remove、set 等写操作）时，它不会直接修改原有的数组，而是先将原始数组复制一份，然后在复制的数组上修改，在完成修改后，再将原始数组引用指向新数组。这种实现方式保证了并发下读写的线程安全。
        写操作多读操作少的场景，如读多写少的缓存
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
2、map 
    Map 是 Java 提供的一个用于存储键值对的接口；
    键是唯一的，不能重复。如果插入重复的键，后插入的值会覆盖之前的值；
    键与值之间有一一对应关系，允许通过键查找值；
    不保证内部元素的存储顺序；

    put(K key, V value)	将指定的键值对插入 Map 中
    putAll(Map<? extends K,? extends V>)	将另一个 Map 的键值对全添加进当前 Map
    V get(Object key)	根据键获取对应的值，不存在则返回 null
    boolean containsKey(Object key)	判断集合中是否包含指定的键
    boolean containsValue(Object value)	判断集合中是否包含指定的值
    remove(Object key)	删除指定键对应的键值对
    clear()	清空当前集合的所有键值对
    int size()	返回当前 Map 中键值对的个数
    boolean isEmpty()	判断 Map 是否为空
    Set<K> keySet()	返回所有键组成的 Set 集合
    Collection<V> values()	返回所有值组成的集合
    Set<Map.Entry<K, V>> entrySet()	返回所有键值对的集合

    普通场景	HashMap 遍历不保证插入的顺序，线程不安全
    保证插入顺序	LinkedHashMap 遍历保证插入的顺序，现场不安全
    需要按键排序	TreeMap 遍历不保证插入的顺序，按照key的自然排序，线程不安全
    多线程高并发场景	ConcurrentHashMap 线程安全的
    缓存场景	WeakHashMap

3、set
    是一种不允许包含重复元素的集合;
    它继承自 Collection 接口，是实现无序、唯一元素集合的抽象数据类型;
    当试图向集合中插入重复元素时，保存新元素会覆盖旧元素，最后仅保留一个;
    元素存储和访问无特定顺序，部分实现类（如 LinkedHashSet）除外，会按照插入顺序保存;
    唯一性是基于元素的 equals 和 hashCode 方法，如果两个对象的 equals 方法返回 true，那么 Set 认为这两个元素是重复的;

    HashSet 
        是 Set 的常用实现类，基于 哈希表 (HashMap + Hashing) 实现;
    LinkedHashSet 
        继承自 HashSet，但在内部使用了 LinkedHashMap 来存储元素，因此它可以保持元素的插入顺序;
    TreeSet 
        是基于 红黑树 实现的有序集合,元素存储是按照键的自然顺序（Comparable 接口定义）或通过指定的 Comparator 顺序进行的;
        注意： 使用 TreeSet 时，存储的元素必须是可比较的（实现了 Comparable 接口），否则需要提供一个 Comparator;
    EnumSet
        EnumSet 是 Set 的一个特殊实现，专门用于操作 枚举类型的集合;
        效率非常高，内部用一个 位向量 来表示;
        不能存储非枚举类型的值;
    CopyOnWriteArraySet
        CopyOnWriteArraySet 是线程安全的无序集合，它基于 CopyOnWriteArrayList，使用 写时复制 的机制
    ConcurrentSkipListSet
        ConcurrentSkipListSet 是一个基于跳表的数据结构，线程安全，支持高并发操作，同时还保持了元素的顺序;
        它类似于 TreeSet，支持快速的有序存取，但性能优于同样线程安全的 TreeSet;
        适用于： 高并发环境中，操作有序集合;

    场景需求	推荐 Set
    无序快速查找	HashSet
    保留插入顺序	LinkedHashSet
    需要排序	TreeSet
    高并发环境	ConcurrentSkipListSet 或 CopyOnWriteArraySet
    存储枚举类型	EnumSet