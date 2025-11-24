import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelMainApplication {
	public static void main(String[] args) throws Exception {
		// 创建 CamelContext 实例
		CamelContext camelContext = new DefaultCamelContext();
		camelContext.addRoutes(new MyRoute());
		// 启动 CamelContext
		camelContext.start();

		Thread.currentThread().join();
		// 关闭 CamelContext
		camelContext.stop();
	}

	// 定义 Camel 路由
	static class MyRoute extends RouteBuilder {
		@Override
		public void configure() {
			// 定时器，每 1 秒执行一次，打印 "Hello World!" 到控制台
			from("timer:hello?period=1000")
					.setBody()
					.constant("main Hello World!") // 设置消息的内容为 "Hello World!"
					.to("stream:out"); // 将消息输出到控制台
		}
	}
}
