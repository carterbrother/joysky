package com.joysky.ice.easyhttp.auth.web;

import com.joysky.ice.easyhttp.auth.service.BookService;
import com.joysky.ice.easyhttp.auth.start.client.ApiResult;
import com.joysky.ice.easyhttp.auth.start.client.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 图书服务控制器
 * 提供图书相关的REST API接口
 * @author EasyHttp
 */
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 根据ID获取图书信息
     * 支持路径参数方式：/books/{id}
     */
    @GetMapping("/books/{id}")
    public ApiResult<Book> getBookById(@PathVariable String id){
        try {
            Book book = bookService.getBookById(id);
            return ApiResult.successful(book);
        } catch (Exception e) {
            return ApiResult.failed("获取图书失败: " + e.getMessage());
        }
    }

    /**
     * 根据作者查询图书
     * 支持路径参数方式：/books/author/{author}
     */
    @GetMapping("/books/author/{author}")
    public ApiResult<List<Book>> getBooksByAuthor(@PathVariable String author){
        try {
            List<Book> books = bookService.getBooksByAuthor(author);
            return ApiResult.successful(books);
        } catch (Exception e) {
            return ApiResult.failed("根据作者查询图书失败: " + e.getMessage());
        }
    }

    /**
     * 根据多个参数查询图书列表
     * 支持查询参数方式：/books?author=李四&publisher=人民出版社
     */
    @GetMapping("/books")
    public ApiResult<List<Book>> getBooksByParams(@RequestParam(required = false) Map<String, String> params){
        try {
            List<Book> books = bookService.getBooksByConditions(params);
            return ApiResult.successful(books);
        } catch (Exception e) {
            return ApiResult.failed("查询图书列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建图书
     * POST /books
     */
    @PostMapping("/books")
    public ApiResult<String> createBook(@RequestBody Book book){
        try {
            // 验证必填字段
            if (book.getName() == null || book.getName().trim().isEmpty()) {
                return ApiResult.failed("图书名称不能为空");
            }
            if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
                return ApiResult.failed("图书作者不能为空");
            }
            
            String bookId = bookService.createBook(book);
            return ApiResult.successful("图书创建成功，ID: " + bookId);
        } catch (Exception e) {
            return ApiResult.failed("创建图书失败: " + e.getMessage());
        }
    }

    /**
     * 更新图书
     * PUT /books/{id}
     */
    @PutMapping("/books/{id}")
    public ApiResult<String> updateBook(@PathVariable String id, @RequestBody Book book){
        try {
            // 验证必填字段
            if (book.getName() == null || book.getName().trim().isEmpty()) {
                return ApiResult.failed("图书名称不能为空");
            }
            
            boolean updated = bookService.updateBook(id, book);
            if (updated) {
                return ApiResult.successful("图书ID: " + id + " 更新成功");
            } else {
                return ApiResult.failed("图书ID: " + id + " 不存在");
            }
        } catch (Exception e) {
            return ApiResult.failed("更新图书失败: " + e.getMessage());
        }
    }

    /**
     * 删除图书
     * DELETE /books/{id}
     */
    @DeleteMapping("/books/{id}")
    public ApiResult<String> deleteBook(@PathVariable String id){
        try {
            boolean deleted = bookService.deleteBook(id);
            if (deleted) {
                return ApiResult.successful("图书ID: " + id + " 删除成功");
            } else {
                return ApiResult.failed("图书ID: " + id + " 不存在");
            }
        } catch (Exception e) {
            return ApiResult.failed("删除图书失败: " + e.getMessage());
        }
    }

    /**
     * 表单方式创建图书
     * POST /books/form
     */
    @PostMapping("/books/form")
    public ApiResult<String> createBookByForm(@RequestParam Map<String, String> params){
        try {
            String name = params.get("name");
            String author = params.get("author");
            String publisher = params.get("publisher");
            String publishDate = params.get("publishDate");
            String priceStr = params.get("price");
            
            // 验证必填字段
            if (name == null || name.trim().isEmpty()) {
                return ApiResult.failed("图书名称不能为空");
            }
            if (author == null || author.trim().isEmpty()) {
                return ApiResult.failed("图书作者不能为空");
            }
            
            // 构造Book对象
            Book book = new Book();
            book.setName(name);
            book.setAuthor(author);
            book.setPublisher(publisher != null ? publisher : "未知出版社");
            book.setPublishDate(publishDate != null ? publishDate : "2024-01-01");
            
            // 处理价格
            if (priceStr != null && !priceStr.trim().isEmpty()) {
                try {
                    book.setPrice(new java.math.BigDecimal(priceStr));
                } catch (NumberFormatException e) {
                    return ApiResult.failed("价格格式不正确");
                }
            }
            
            String bookId = bookService.createBook(book);
            return ApiResult.successful("表单方式创建图书成功: " + name + " by " + author + ", ID: " + bookId);
        } catch (Exception e) {
            return ApiResult.failed("表单创建图书失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ApiResult<String> health(){
        try {
            int bookCount = bookService.getBookCount();
            return ApiResult.successful("EasyHttp Auth Service is running on port 8082, 当前图书总数: " + bookCount);
        } catch (Exception e) {
            return ApiResult.successful("EasyHttp Auth Service is running on port 8082");
        }
    }

    /**
     * 获取所有图书
     * GET /books/all
     */
    @GetMapping("/books/all")
    public ApiResult<List<Book>> getAllBooks(){
        try {
            List<Book> books = bookService.getAllBooks();
            return ApiResult.successful(books);
        } catch (Exception e) {
            return ApiResult.failed("获取所有图书失败: " + e.getMessage());
        }
    }

    /**
     * 检查图书是否存在
     * GET /books/exists/{id}
     */
    @GetMapping("/books/exists/{id}")
    public ApiResult<Boolean> bookExists(@PathVariable String id){
        try {
            boolean exists = bookService.bookExists(id);
            return ApiResult.successful(exists);
        } catch (Exception e) {
            return ApiResult.failed("检查图书存在性失败: " + e.getMessage());
        }
    }
    
    /**
     * 不稳定服务模拟接口 - 用于测试重试功能
     * 该接口有70%的概率返回失败，用于演示EasyHttp的重试机制
     * GET /books/{id}/unstable
     */
    @GetMapping("/books/{id}/unstable")
    public ApiResult<Book> getBookFromUnstableService(@PathVariable String id){
        // 模拟不稳定的服务，70%概率失败
        if (Math.random() < 0.7) {
            throw new RuntimeException("模拟不稳定的服务，70%概率失败");
        }

        Book book = bookService.getBookById(id);
        if (book != null) {
            return ApiResult.successful(book);
        } else {
            throw new RuntimeException("图书不存在");
        }
    }
}
