package com.commons.thread.callback.synchronous;

public class Test {
	public static void main(String[] args) {
		final String name = "yangzhi";
		DoCallBack dcb = new DoCallBack();
		Object ret = dcb.CallBack(name,new CallBack(){
			@Override
			public Object callBack(String namex) {
				// TODO Auto-generated method stub
				return name + namex;
			}
		});
		
		System.out.println(ret);
	}
}
