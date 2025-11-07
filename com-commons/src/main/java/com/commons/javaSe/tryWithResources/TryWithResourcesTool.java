package com.commons.javaSe.tryWithResources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

//try-with-resources 是 Java 7 引入的一种语法糖，用于简化资源管理的代码写法，并确保资源会在使用后被正确地关闭而不需要手动调用关闭方法。
//这种机制极大地减少了手动关闭资源可能导致的资源泄露和代码冗余问题。
//try-with-resources 主要用于处理实现了 java.lang.AutoCloseable 或 java.io.Closeable 接口的资源对象，例如文件流、数据库连接、网络连接等场景。
public class TryWithResourcesTool {
	//这种写法的问题是，相对来说代码比较冗长，尤其是在资源处理链条较多的时候。
	//并且如果开发者忘记在 finally 块中使用正确的关闭方法，就可能导致资源未被释放，进而引发 资源泄漏。
	public void read(String path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
			System.out.println(line);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	//使用 try-with-resources 语句，就可以大幅简化代码。直接在 try 小括号中定义资源，然后 Java 会自动关闭这些资源，以确保资源不会泄露。
	//自动关闭资源 在这段代码中，当 try 块执行完成后，BufferedReader 会被自动关闭，而无需显式调用其 close() 方法。
	//try 块中的资源会在作用域内被安全释放。 实现条件：只有实现了 AutoCloseable 接口（或者扩展了 Closeable 接口）的类，才能用在 try-with-resources 中。
	//例如： 所有输入/输出的核心类，如 FileReader, BufferedReader, InputStream, BufferedWriter, FileWriter。 JDBC 的数据库连接，如 Connection, PreparedStatement, ResultSet。
	public void read2(String path) {
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line = reader.readLine();
			System.out.println(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//多个资源管理
	//可以在一个 try 中声明多个资源，用分号（;）分隔。所有资源都会按照声明的顺序依次被关闭，后声明的资源会先关闭。
	public void read3(String readPath, String writePath) {
		try (
				BufferedReader reader = new BufferedReader(new FileReader(readPath));
				BufferedWriter writer = new BufferedWriter(new FileWriter(writePath))
		) {
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void read4(String readPath, String readPath2) {
		try (BufferedReader reader = new BufferedReader(new FileReader(readPath))) {
			System.out.println(reader.readLine());

			try (BufferedReader reader2 = new BufferedReader(new FileReader(readPath2))) {
				System.out.println(reader2.readLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class MyResource implements AutoCloseable {
		@Override
		public void close() {
			System.out.println("资源已关闭！");
		}

		public void doSomething() {
			System.out.println("使用这个资源...");
		}
	}

	public static void main(String[] args) {
		try (MyResource resource = new MyResource()) {
			resource.doSomething();
		}
	}
}
