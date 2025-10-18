1、介绍单例模式几种类型
    饥饿模式 SingletonHungry 线程安全的--浪费资源
    懒加载模式-线程不安全 SingletonLazy 懒加载--多线程状态下线程不安全的--不建议使用
    懒加载模式-线程安全 SingletonLazySynchronized 懒加载模式--线程安全--性能不高
    双重检索模式 SingletonDoubleCheck  懒加载模式--线程安全--性能比方法加synchronized高
    静态内部类模式 SingletonStaticInnerClass 懒加载模式--线程安全--性能高--推荐使用
    枚举模式 SingletonEnum 
2、单例模式的应用