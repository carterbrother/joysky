package com.joysky.ice.easyhttp.auth.client.res;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Book {

    private String name;
    private String author;
    private String isbn;
    private String publisher;
    private String publishDate;
    private BigDecimal price;

}
