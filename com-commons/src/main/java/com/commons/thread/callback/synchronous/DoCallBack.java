package com.commons.thread.callback.synchronous;

public class DoCallBack {
	public Object CallBack(String name,CallBack object){
		//begin
		//System.out.println(object.callBack(name));
		//end
		return object.callBack(name);
	}
}
