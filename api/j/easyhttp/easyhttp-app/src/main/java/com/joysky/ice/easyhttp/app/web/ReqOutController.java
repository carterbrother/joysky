package com.joysky.ice.easyhttp.app.web;

import com.joysky.ice.easyhttp.auth.client.BookHttpService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ReqOutController {

    private final BookHttpService bookHttpService;
    
    public ReqOutController(BookHttpService bookHttpService) {
        this.bookHttpService = bookHttpService;
    }

    @GetMapping("/testGet")
    public Object testGet(@RequestParam String id) {
        return bookHttpService.getBookById(id);
    }


}
