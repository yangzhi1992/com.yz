package com.commons.api;

import com.commons.api.annotation.ApiClass;
import com.commons.api.annotation.ApiOptions;
import com.commons.api.model.ComminApiDTO;
import com.commons.api.retrofit.Call;
import com.commons.api.retrofit.http.Body;
import com.commons.api.retrofit.http.Field;
import com.commons.api.retrofit.http.FieldMap;
import com.commons.api.retrofit.http.FormUrlEncoded;
import com.commons.api.retrofit.http.GET;
import com.commons.api.retrofit.http.HTTP;
import com.commons.api.retrofit.http.Header;
import com.commons.api.retrofit.http.HeaderMap;
import com.commons.api.retrofit.http.Headers;
import com.commons.api.retrofit.http.ModelParam;
import com.commons.api.retrofit.http.Multipart;
import com.commons.api.retrofit.http.POST;
import com.commons.api.retrofit.http.Part;
import com.commons.api.retrofit.http.PartMap;
import com.commons.api.retrofit.http.Path;
import com.commons.api.retrofit.http.Query;
import com.commons.api.retrofit.http.QueryMap;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * common api 使用
 */
@ApiClass(value = "commonApiTest", baseUrl = "${common.apiHost}", requestInterceptor = "apiRequestInterceptor", responseInterceptor = "apiResponseInterceptor")
public interface CommonApiTest {
    /**
     * get 不带入参
     */
    @GET("/health")
    @ApiOptions(connectTimeout = 5000, readTimeout = 5000, writeTimeout = 5000, retries = 3)
    Call<String> health();

    /**
     * get 带path入参，带query入参入参编码请求，携带header头单个入参
     */
    @GET("get_query/{key}")
    Call<Map<String, Object>> get_query(
            @Path(value = "key") String key,
            @Query(value = "name", encoded = true) String name,
            @Query(value = "value", encoded = true) String value,
            @Header(value = "token") String token
    );

    /**
     * get 带query入参入参编码请求入参是map，携带header头map入参
     */
    @GET("get_queryMap")
    Call<Map<String, Object>> get_queryMap(
            @QueryMap(encoded = true) Map<String, String> param,
            @HeaderMap Map<String, String> headers
    );

    /**
     * post x-www-form-urlencoded 入参以对象写入
     */
    @POST("/post_xWwwFormUrlencoded_object")
    @FormUrlEncoded
    @ApiOptions(connectTimeout = 5000, readTimeout = 5000, writeTimeout = 5000, retries = 3)
    Call<Map<String, Object>> post_xWwwFormUrlencoded_object(@ModelParam(excludeNull = true) ComminApiDTO comminApiDTO);

    /**
     * post x-www-form-urlencoded 入参以map写入
     */
    @POST("/post_xWwwFormUrlencoded_map")
    @FormUrlEncoded
    @ApiOptions(connectTimeout = 5000, readTimeout = 5000, writeTimeout = 5000, retries = 3)
    Call<Map<String, Object>> post_xWwwFormUrlencoded_map(@FieldMap Map<String, String> params);

    /**
     * post x-www-form-urlencoded 入参以单个参数写入
     */
    @POST("/post_xWwwFormUrlencoded_field")
    @FormUrlEncoded
    @ApiOptions(connectTimeout = 5000, readTimeout = 5000, writeTimeout = 5000, retries = 3)
    Call<Map<String, Object>> post_xWwwFormUrlencoded_field(@Field("name") String name);

    /**
     * post json 入参以对象写入
     */
    @POST("post_json_object")
    @Headers({"Content-Type: application/json"})
    @ApiOptions(connectTimeout = 5000, readTimeout = 5000, writeTimeout = 5000, retries = 3)
    Call<Map<String, Object>> post_json_object(@Body ComminApiDTO comminApiDTO);

    /**
     * post json 入参以map写入且携带query入参入参编码
     */
    @POST("post_json_map")
    @Headers({"Content-Type: application/json"})
    @ApiOptions(connectTimeout = 5000, readTimeout = 5000, writeTimeout = 5000, retries = 3)
    Call<Map<String, Object>> post_json_map(@Query(value = "name", encoded = true) String name, @Body Map requestBody);

    /**
     * post form-data 上传文件，携带query入参入参编码，form-data表单参数value,value1
     */
    @POST("/post_upload_part")
    @Multipart
    @ApiOptions(connectTimeout = 5000, readTimeout = 5000, writeTimeout = 5000, retries = 3)
    Call<Map<String, Object>> post_upload_part(
            @Query(value = "name", encoded = true) String name,
            @Part MultipartBody.Part file,
            @Part(value = "value") RequestBody value,
            @Part(value = "value") RequestBody value1
    );

    /**
     * post form-data 上传文件，携带query入参入参编码，form-data表单参数map
     */
    @POST("/post_upload_partMap")
    @Multipart
    @ApiOptions(connectTimeout = 5000, readTimeout = 5000, writeTimeout = 5000, retries = 3)
    Call<Map<String, Object>> post_upload_partMap(
            @Query(value = "name", encoded = true) String name,
            @Part MultipartBody.Part file,
            @PartMap Map<String, RequestBody> params
    );

    /**
     * post path header query body
     */
    @ApiOptions(connectTimeout = 5000, readTimeout = 5000, writeTimeout = 5000, retries = 3)
    @HTTP(path = "/post_bode/{key}", hasBody = true, method = "post")
    Call<Map<String, Object>> updateConverter(
            @Path("key") String key,
            @Header("token") String token,
            @Query("name") String name,
            @Body ComminApiDTO comminApiDTO
    );
}
