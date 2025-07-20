package com.joysky.ice.easyhttp.auth.web;

import com.joysky.ice.easyhttp.auth.start.client.ApiResult;
import com.joysky.ice.easyhttp.auth.start.client.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class BookController {

    @GetMapping("/books")
    public ApiResult<Book> getBookById(@RequestParam("id") String id){
        Book book = new Book();
        book.setIsbn(id);
        book.setAuthor("carter");
        book.setName("cartereasyhttp");
        book.setPrice(new BigDecimal(10000));
        book.setPublishDate("2021-01-01");
        book.setPublisher("cartereasyhttp");

        return ApiResult.successful(book);
    }


}
