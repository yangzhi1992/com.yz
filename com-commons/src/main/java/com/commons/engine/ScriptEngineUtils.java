package com.commons.engine;

import java.util.Map;
import java.util.TreeMap;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptEngineUtils {
    public static ScriptEngine engine;

    public static void main(String[] args) throws Exception {
        engine();
        getJsValue();
        getObject();
        putValue();
        callJsFunction();
    }

    public static void engine() throws ScriptException {
        //通过脚本名称获取：
        ScriptEngine JavaScriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
        //通过文件扩展名获取：
        ScriptEngine jsEngine = new ScriptEngineManager().getEngineByExtension("js");
        //通过MIME类型来获取：
        ScriptEngine testEngine = new ScriptEngineManager().getEngineByMimeType("text/javascript");

        //计算算术表达式
        Object JavaScriptEngineO = JavaScriptEngine.eval("(1+2)/3");
        System.out.println(JavaScriptEngineO);

        //执行逻辑表达式并且动态传参
        jsEngine.put("a", false);
        jsEngine.put("b", true);
        jsEngine.put("c", false);
        jsEngine.put("d", true);
        Object jsEngineO = jsEngine.eval("(a||b) && (c||d)");
        System.out.println(jsEngineO);
    }

    //该函数测试Java获取JS变量值的能力
    public static void getJsValue() throws Exception {
        engine = new ScriptEngineManager().getEngineByName("JavaScript");
        String str = "  var msg='hello';          "
                + "  var number = 123;         "
                + "  var array=['A','B','C'];  "
                + "  var json={                "
                + "      'name':'pd',          "
                + "      'subjson':{           "
                + "           'subname':'spd'  "
                + "           ,'id':123        "
                + "           }                "
                + "      };                    ";
        //执行语句
        engine.eval(str);
        str = "msg+=' world';number+=5";
        //再次执行
        engine.eval(str);
        //获取js变量msg（String类型）
        System.out.println(engine.get("msg"));
        //获取js变量msg（int类型）
        System.out.println(engine.get("number"));
        //获取js变量array（数组）
        ScriptObjectMirror array = (ScriptObjectMirror) engine.get("array");
        //getSlot（int index）函数用于获取下标为index的值
        System.out.println(array.getSlot(0));
        //获取js变量json（json类型）
        ScriptObjectMirror json = (ScriptObjectMirror) engine.get("json");
        //get（String key）函数用于键key的值
        System.out.println(json.get("name"));
        //获取js变量subjson（嵌套json类型）
        ScriptObjectMirror subjson = (ScriptObjectMirror) json.get("subjson");
        System.out.println(subjson.get("subname"));
    }

    //该函数测试Java与js对象
    public static void getObject() throws Exception {
        String str = "  var obj=new Object();     "
                + "  obj.info='hello world';   "
                + "  obj.getInfo=function(){   "
                + "        return this.info;   "
                + "  };                        ";
        engine.eval(str);
        //获取对象
        ScriptObjectMirror obj = (ScriptObjectMirror) engine.get("obj");
        //输出属性
        System.out.println(obj.get("info"));
        System.out.println(obj.get("getInfo"));
        str = "obj.getInfo()";
        //执行方法
        System.out.println(engine.eval(str));
    }

    //java将变量导入js脚本
    public static void putValue() throws Exception {
        String str = "Math.pow(a,b)";
        Map<String, Object> input = new TreeMap<>();
        input.put("a", 2);
        input.put("b", 8);
        System.out.println(engine.eval(str, new SimpleBindings(input)));
    }

    //调用js函数
    public static void callJsFunction() throws Exception {
        engine.eval("function add (a, b) {return a+b; }");
        Invocable jsInvoke = (Invocable) engine;
        Object res = jsInvoke.invokeFunction("add", new Object[]{10, 5});
        System.out.println(res);
    }

}
