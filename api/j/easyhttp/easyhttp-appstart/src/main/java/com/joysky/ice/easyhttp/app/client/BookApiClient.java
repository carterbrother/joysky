package com.joysky.ice.easyhttp.app.client;

import com.github.vizaizai.annotation.*;
import com.github.vizaizai.boot.annotation.EasyHttpClient;
import com.joysky.ice.easyhttp.app.model.ApiResult;
import com.joysky.ice.easyhttp.app.model.Book;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 图书API客户端接口
 * 使用EasyHttp声明式HTTP客户端
 * @author EasyHttp
 */
@EasyHttpClient(value = "auth")
public interface BookApiClient {
    
    /**
     * 根据ID获取图书信息
     * @param id 图书ID
     * @return 图书信息
     */
    @Get("/books/{id}")
    ApiResult<Book> getBookById(@Var("id") String id);
    
    /**
     * 根据作者查询图书
     * @param author 作者名称
     * @return 图书信息
     */
    @Get("/books?author={author}")
    ApiResult<List<Book>> getBookByAuthor(@Var("author") String author);
    
    /**
     * 根据作者查询图书列表
     * @param author 作者名称
     * @return 图书列表
     */
    @Get("/books")
    ApiResult<List<Book>> getBooksByAuthor(@Param("author") String author);
    
    /**
     * 根据参数查询图书列表
     * @param params 查询参数
     * @return 图书列表
     */
    @Get("/books")
    ApiResult<List<Book>> getBooksByParams(@Param Map<String, Object> params);
    
    /**
     * 根据多个ID查询图书列表
     * @param ids 图书ID列表
     * @return 图书列表
     */
    @Get("/books")
    ApiResult<List<Book>> getBooksByIds(@Param("ids") List<String> ids);
    
    /**
     * 创建新图书
     * @param book 图书信息
     * @return 创建结果
     */
    @Post("/books")
    ApiResult<String> createBook(@Body Book book);
    
    /**
     * 更新图书信息
     * @param id 图书ID
     * @param book 图书信息
     * @return 更新结果
     */
    @Put("/books/{id}")
    ApiResult<String> updateBook(@Var("id") String id, @Body Book book);
    
    /**
     * 删除图书
     * @param id 图书ID
     * @return 删除结果
     */
    @Delete("/books/{id}")
    ApiResult<String> deleteBook(@Var("id") String id);
    
    /**
     * 异步获取图书
     * @param id 图书ID
     * @return 异步图书信息
     */
    @Get("/books/{id}")
    CompletableFuture<ApiResult<Book>> getBookByIdAsync(@Var("id") String id);
    
    /**
     * 异步获取图书列表
     * @return 异步结果
     */
    @Get("/books")
    CompletableFuture<ApiResult<List<Book>>> getAllBooksAsync();
    
    /**
     * 带请求头的API调用
     * @param book 图书信息
     * @param headers 请求头
     * @return 创建结果
     */
    @Headers({"Content-Type: application/json", "Client: EasyHttp"})
    @Post("/books")
    ApiResult<String> createBookWithHeaders(@Body Book book, @Headers Map<String, String> headers);
    
    /**
     * 表单方式创建图书
     * @param book 图书信息
     * @return 创建结果
     */
    @Post("/books/form")
    ApiResult<String> createBookByForm(@Param Book book);
    
    /**
     * 重试示例：获取图书信息（带重试机制）
     * 当请求失败时会自动重试，最多重试3次，每次间隔1秒
     * @param id 图书ID
     * @return 图书信息
     */
    @Get(value = "/books/{id}", retries = 3, interval = 1000)
    ApiResult<Book> getBookByIdWithRetry(@Var("id") String id);
    
    /**
     * 重试示例：模拟不稳定的接口调用
     * 调用一个可能失败的接口，展示重试机制的效果
     * 最多重试5次，每次间隔2秒
     * @param id 图书ID
     * @return 图书信息
     */
    @Get(value = "/books/{id}/unstable", retries = 5, interval = 50)
    ApiResult<Book> getBookFromUnstableService(@Var("id") String id);
}