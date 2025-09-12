package com.commons.javatree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 多叉树类
 */
public class MultipleTree {
	public static void main(String[] args) {
		// 读取层次数据结果集列表
		List dataList = VirtualDataGenerator.getVirtualResult();
		List<Node> list = convertObjectToTree(dataList);
		for (Node o : list) {
			System.out.println(o);
		}
	}

	public static List convertObjectToTree(List dataList) {
		// 节点列表（散列表，用于临时存储节点对象）
		HashMap nodeList = new HashMap();
		// 根节点
		Node root = null;
		// 根据结果集构造节点列表（存入散列表）
		for (Iterator it = dataList.iterator(); it.hasNext();) {
			Map dataRecord = (Map) it.next();
			Node node = new Node();
			node.id = (String) dataRecord.get("id");
			node.text = (String) dataRecord.get("text");
			node.parentId = (String) dataRecord.get("parentId");
			nodeList.put(node.id, node);
		}
		// 构造无序的多叉树
		Set entrySet = nodeList.entrySet();
		List<Node> list = new ArrayList();
		for (Iterator it = entrySet.iterator(); it.hasNext();) {
			Node node = (Node) ((Map.Entry) it.next()).getValue();
			if (node.parentId == null || node.parentId.equals("")) {
				root = node;
				root.sortChildren();
				list.add(root);
			} else {
				((Node) nodeList.get(node.parentId)).addChild(node);
			}
		}
		return list;
	}
}