package com.joysky.ice.easyhttp.auth.start.client;

import com.github.vizaizai.annotation.*;
import com.github.vizaizai.boot.annotation.EasyHttpClient;
import com.github.vizaizai.entity.body.RequestBodyType;
import com.github.vizaizai.entity.form.BodyContent;
import com.github.vizaizai.entity.form.FormData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@EasyHttpClient(value = "auth")
public interface BookHttpService {

    @Get("/books")
    ApiResult<Book> getBookById(@Param("id") String id);


    @Get("/books?author={author}")
    ApiResult<Book> getBookByAuthor(@Var("author") String author);

    @Get("/books")
    ApiResult<List<Book>> listBooksByAuthor1(@Param("author") String author);

    // 设置了-parameters
    @Get("/books")
    ApiResult<List<Book>> listBooksByAuthor2(@Param String author);

    @Get("/books")
    ApiResult<List<Book>> listBooksByAuthor3(@Param Map<String, String> params);

    @Get("/books")
    ApiResult<List<Book>> listBooksByAuthor4(Map<String, String> params);

    @Get("/books")
    ApiResult<List<Book>> listBooksByIds(@Param("ids") List<String> ids);

    @Post("/addBookUseForm")
    String test5(@Param Book book); // 请求体x-www-form-urlencoded

    @Post(value="/addBookUseForm", bodyType = RequestBodyType.NONE) // 指定无请求体，参数拼接到url上面
    String test5_1(@Param Book book); // 拼接url，如 /addBookUseForm?name=easyhttp&author=lcw

    @Post("/books")
    void addBook(@Body Book book);

    @Post("/addBookUseJSON")
    String test6(@Body Book book1);

    @Post("/addBookUseJSON")
    String test6_1(@Body Map<String,Object> book);

    @Post("/addBookUseJSON")
    String test6_2(@Body String content);


    @Post("/addBookUseFormData")
    String test8(@Body FormData formData);

    @Post("/upload/e-book/{id}")
    String test9(@Var String id, @Body BodyContent bodyContent);


    @Headers({"clent: Easy-http"})
    @Post("/books")
    ApiResult<Void> addBook(@Body Book book, @Headers Map<String, String> headers);

    @Get("/books")
    CompletableFuture<ApiResult<List<Book>>> foo();


}    