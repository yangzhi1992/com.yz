1、引用jar包
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>2.0.42</version>
</dependency>
2、特效说明
常用 SerializerFeatures
QuoteFieldNames	输出字段名时使用双引号（默认启用）
UseSingleQuotes	使用单引号而不是双引号
WriteMapNullValue	输出值为null的字段
WriteEnumUsingToString	枚举值使用toString()方法输出
WriteEnumUsingName	枚举值使用name()方法输出（默认）
UseISO8601DateFormat	日期使用ISO8601格式("yyyy-MM-dd'T'HH:mm:ss")
WriteNullListAsEmpty	将Collection类型为null的字段输出为[]
WriteNullStringAsEmpty	将String类型为null的字段输出为""
WriteNullNumberAsZero	将Number类型为null的字段输出为0
WriteNullBooleanAsFalse	将Boolean类型为null的字段输出为false
SkipTransientField	跳过transient修饰的字段
SortField	按字段名称排序后输出
WriteTabAsSpecial	把\t做转义输出
PrettyFormat	格式化JSON输出
WriteClassName	序列化时写入类型信息
DisableCircularReferenceDetect	禁用循环引用检测
WriteSlashAsSpecial	对斜杠'/'进行转义
BrowserCompatible	将中文会序列化为\uXXXX格式
WriteDateUseDateFormat	使用全局日期格式
NotWriteRootClassName	不写入根类名
DisableCheckSpecialChar	不检查特殊字符
BeanToArray	将对象转为数组格式
WriteNonStringKeyAsString	将非字符串类型的key转为字符串
NotWriteDefaultValue	不输出默认值
BrowserSecure	浏览器安全模式
IgnoreNonFieldGetter	忽略非字段的getter方法
WriteNonStringValueAsString	将非字符串值转为字符串
IgnoreErrorGetter	忽略getter方法抛出的异常
WriteBigDecimalAsPlain	将BigDecimal输出为普通数字

常用 Features
特性	说明
AutoCloseSource	自动关闭输入流
AllowComment	允许注释(//, /* */)
AllowUnQuotedFieldNames	允许非引号字段名
AllowSingleQuotes	允许单引号
InternFieldNames	字段名使用String.intern()
AllowISO8601DateFormat	允许ISO8601日期格式
AllowArbitraryCommas	允许任意多余的逗号
UseBigDecimal	使用BigDecimal代替double
IgnoreNotMatch	忽略不匹配的字段
SortFeidFastMatch	快速匹配字段
DisableASM	禁用ASM
DisableCircularReferenceDetect	禁用循环引用检测
InitStringFieldAsEmpty	初始化String字段为空串
SupportArrayToBean	支持数组转对象
OrderedField	保持字段顺序
DisableSpecialKeyDetect	禁用特殊key检测
UseObjectArray	使用对象数组
SupportNonPublicField	支持非public字段
SupportAutoType	支持自动类型识别(有安全风险)
NonStringKeyAsString	非字符串key转为字符串
CustomMapDeserializer	自定义Map反序列化
ErrorOnEnumNotMatch	枚举不匹配时报错
TrimStringFieldValue	去除字符串字段值的空格
SupportClassForName	支持Class.forName
SupportAutoTypeAccept	支持自动类型接受
DisableFieldSmartMatch	禁用字段智能匹配
SupportSmartMatch	支持智能匹配
IgnoreSetTypeProperties	忽略setType属性
DisableCheckSpecialChar	不检查特殊字符