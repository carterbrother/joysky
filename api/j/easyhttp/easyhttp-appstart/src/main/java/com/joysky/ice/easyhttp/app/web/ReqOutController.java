package com.joysky.ice.easyhttp.app.web;

import com.joysky.ice.easyhttp.app.client.BookApiClient;
import com.joysky.ice.easyhttp.app.model.ApiResult;
import com.joysky.ice.easyhttp.app.model.Book;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * EasyHttp 使用示例控制器
 * 演示各种HTTP调用方式
 * @author EasyHttp
 */
@RestController
@RequestMapping("/api")
public class ReqOutController {

    private final BookApiClient bookApiClient;
    
    public ReqOutController(BookApiClient bookApiClient) {
        this.bookApiClient = bookApiClient;
    }

    /**
     * 原有的测试接口
     */
    @GetMapping("/testGet")
    public ApiResult<Book> testGet(@RequestParam String id) {
        try {
            return bookApiClient.getBookById(id);
        } catch (Exception e) {
            return ApiResult.error("调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取图书信息
     */
    @GetMapping("/books/{id}")
    public ApiResult<Book> getBook(@PathVariable String id) {
        try {
            return bookApiClient.getBookById(id);
        } catch (Exception e) {
            return ApiResult.error("获取图书信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据作者查询图书
     */
    @GetMapping("/books/author/{author}")
    public ApiResult<List<Book>> getBookByAuthor(@PathVariable String author) {
        try {
            return bookApiClient.getBookByAuthor(author);
        } catch (Exception e) {
            return ApiResult.error("根据作者查询图书失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据参数查询图书列表
     */
    @GetMapping("/books")
    public ApiResult<List<Book>> getBooks(@RequestParam(required = false) String author,
                                         @RequestParam(required = false) String publisher) {
        try {
            Map<String, Object> params = new HashMap<>();
            if (author != null) {
                params.put("author", author);
            }
            if (publisher != null) {
                params.put("publisher", publisher);
            }
            return bookApiClient.getBooksByParams(params);
        } catch (Exception e) {
            return ApiResult.error("查询图书列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新图书
     */
    @PostMapping("/books")
    public ApiResult<String> createBook(@RequestBody Book book) {
        try {
            return bookApiClient.createBook(book);
        } catch (Exception e) {
            return ApiResult.error("创建图书失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新图书信息
     */
    @PutMapping("/books/{id}")
    public ApiResult<String> updateBook(@PathVariable String id, @RequestBody Book book) {
        try {
            return bookApiClient.updateBook(id, book);
        } catch (Exception e) {
            return ApiResult.error("更新图书失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除图书
     */
    @DeleteMapping("/books/{id}")
    public ApiResult<String> deleteBook(@PathVariable String id) {
        try {
            return bookApiClient.deleteBook(id);
        } catch (Exception e) {
            return ApiResult.error("删除图书失败: " + e.getMessage());
        }
    }
    
    /**
     * 异步获取图书信息
     */
    @GetMapping("/books/{id}/async")
    public CompletableFuture<ApiResult<Book>> getBookAsync(@PathVariable String id) {
        return bookApiClient.getBookByIdAsync(id)
                .exceptionally(throwable -> ApiResult.error("异步获取图书失败: " + throwable.getMessage()));
    }
    
    /**
     * 异步获取所有图书
     */
    @GetMapping("/books/async")
    public CompletableFuture<ApiResult<List<Book>>> getAllBooksAsync() {
        return bookApiClient.getAllBooksAsync()
                .exceptionally(throwable -> ApiResult.error("异步获取图书列表失败: " + throwable.getMessage()));
    }
    
    /**
     * 创建示例图书数据
     */
    @PostMapping("/books/example")
    public ApiResult<String> createExampleBook() {
        try {
            Book book = new Book();
            book.setId("example-001");
            book.setName("EasyHttp 使用指南");
            book.setAuthor("EasyHttp Team");
            book.setIsbn("978-0000000000");
            book.setPublisher("技术出版社");
            book.setPublishDate("2024-01-01");
            book.setPrice(new BigDecimal("99.00"));
            
            return bookApiClient.createBook(book);
        } catch (Exception e) {
            return ApiResult.error("创建示例图书失败: " + e.getMessage());
        }
    }
    
    /**
     * 带自定义请求头的API调用示例
     */
    @PostMapping("/books/with-headers")
    public ApiResult<String> createBookWithHeaders(@RequestBody Book book) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Request-ID", "req-" + System.currentTimeMillis());
            headers.put("X-Client-Version", "1.0.0");
            
            return bookApiClient.createBookWithHeaders(book, headers);
        } catch (Exception e) {
            return ApiResult.error("带请求头创建图书失败: " + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ApiResult<String> health() {
        return ApiResult.success("EasyHttp服务运行正常");
    }
    
    /**
     * 重试功能测试：获取图书信息（带重试机制）
     * 演示EasyHttp的自动重试功能
     */
    @GetMapping("/books/{id}/retry")
    public ApiResult<Book> getBookWithRetry(@PathVariable String id) {
        try {
            return bookApiClient.getBookByIdWithRetry(id);
        } catch (Exception e) {
            return ApiResult.error("重试后仍然失败: " + e.getMessage());
        }
    }
    
    /**
     * 重试功能测试：模拟不稳定服务调用
     * 用于测试重试机制在不稳定网络环境下的表现
     */
    @GetMapping("/books/{id}/unstable")
    public ApiResult<Book> getBookFromUnstableService(@PathVariable String id) {
        try {
            return bookApiClient.getBookFromUnstableService(id);
        } catch (Exception e) {
            return ApiResult.error("不稳定服务调用失败: ");
        }
    }
}
