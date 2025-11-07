package com.commons.javase.scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

// Scanner 是 Java 中的一个非常强大和灵活的工具类，用于读取各种输入数据（文件、字符串、输入流等）。
// 它提供了一组方法，能够轻松解析文本，提取各种数据类型的内容，如字符串、整数、浮点数等。
// 适合用于简单的文件读取、标准输入读取、文本解析以及按正则分隔符处理输入的场景。
public class ScannerTool {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		// 读取整数
		System.out.print("请输入一个整数: ");
		int number = scanner.nextInt();
		System.out.println("你输入的整数是: " + number);

		// 读取一整行文本
		System.out.print("请输入一段话: ");
		scanner.nextLine(); // 清除上一行的换行符
		String input = scanner.nextLine();
		System.out.println("你输入的话是: " + input);
		scanner.close();

		Scanner scanner2 = new Scanner("assss,asss").useDelimiter(",");
		while (scanner.hasNext()) {
			String value = scanner2.next();
			System.out.println(value);
		}
		scanner2.close();
	}

	/**
	 * 数据源：System.in
	 */
	public void scannerBySystemIn() {
		Scanner scanner = new Scanner(System.in);
	}

	/**
	 * 数据源：文件
	 */
	public void scannerByFile(String path) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(path));
		Scanner scanner2 = new Scanner(new File(path), "UTF-8");
	}

	/**
	 * 数据源：字符串
	 */
	public void scannerByString(String content) {
		Scanner scanner = new Scanner(content);
	}

	/**
	 * 从字符串或流中读取时可以指定分隔符：使用逗号和任意多个空格作为分隔符
	 */
	public void scannerUtil() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("example.txt")).useDelimiter(",\\s*");
	}
}
