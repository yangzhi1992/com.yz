package com.commons.javaSe.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class FileTool {

	// 关键点说明
	// BufferedReader.readLine():
	// 		按行读取时，会去掉每一行的换行符，返回一个字符串，直到文件结束返回 null。
	// 关闭资源:
	// 		使用 try-with-resources 自动管理资源，保证在程序运行结束后会安全地关闭文件流，避免资源泄露。
	public List<String> readFileByBufferedReader(String filePath) {
		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			// 使用 readLine() 按行读取文件，读取到最后返回 null
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	// 关键点说明
	// hasNextLine() 方法:
	//  	判断文件中是否有下一行可读内容。
	// 应用场景:
	//  	适用于小文件，或文件内容按行解析的场景。
	// 自动关闭资源:
	//  	通过 try-with-resources 自动关闭 Scanner。
	public List<String> readFileByScanner(String filePath) {
		List<String> lines = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File(filePath))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return lines;
	}

	// scanner通过close关闭
	public List<String> readFileByScanner2(String filePath) throws FileNotFoundException {
		List<String> lines = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			lines.add(line);
		}
		scanner.close();
		return lines;
	}

	// 关键点说明
	// Files.lines():
	// 		自动处理文件的读取，返回一个流（Stream<String>），支持流式操作。
	// 性能:
	// 		Files.lines()非常适合用于处理大文件（在懒加载的模式下逐行读取，而不是一次性将文件全部加载到内存中）。
	// 自动关闭流:
	// 		通过 try-with-resources，Stream 在使用完之后会自动关闭，与文件释放关联资源。
	public List<String> readFileByNio(String filePath) {
		List<String> lines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			stream.forEach(lines::add);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	// 用于读取文件并一次性将所有行读取到内存中
	public List<String> readFileByNioAll(String filePath) throws IOException {
		return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
	}
}
