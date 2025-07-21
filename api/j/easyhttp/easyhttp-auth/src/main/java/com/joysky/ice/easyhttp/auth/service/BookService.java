package com.joysky.ice.easyhttp.auth.service;

import com.joysky.ice.easyhttp.auth.start.client.Book;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 图书服务类
 * 提供图书数据的模拟构造和业务逻辑处理
 * @author EasyHttp
 */
@Service
public class BookService {

    // 模拟图书数据库
    private final Map<String, Book> bookDatabase = new HashMap<>();
    
    // 预定义的图书数据
    private final List<String> bookNames = Arrays.asList(
        "Java编程思想", "Spring Boot实战", "微服务架构设计模式", "深入理解Java虚拟机",
        "Effective Java", "Spring Cloud微服务实战", "Redis设计与实现", "MySQL技术内幕",
        "算法导论", "设计模式", "重构：改善既有代码的设计", "代码整洁之道",
        "分布式系统概念与设计", "高性能MySQL", "Kafka权威指南", "Docker容器技术"
    );
    
    private final List<String> authors = Arrays.asList(
        "张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
        "Bruce Eckel", "Joshua Bloch", "Martin Fowler", "Robert C. Martin",
        "Chris Richardson", "Baron Schwartz", "Neha Narkhede", "Adrian Mouat"
    );
    
    private final List<String> publishers = Arrays.asList(
        "机械工业出版社", "人民邮电出版社", "电子工业出版社", "清华大学出版社",
        "北京大学出版社", "中国电力出版社", "华中科技大学出版社", "科学出版社",
        "O'Reilly Media", "Addison-Wesley", "Manning Publications", "Packt Publishing"
    );

    public BookService() {
        // 初始化一些默认数据
        initializeDefaultBooks();
    }

    /**
     * 初始化默认图书数据
     */
    private void initializeDefaultBooks() {
        for (int i = 1; i <= 100; i++) {
            String id = Objects.toString(i);
            Book book = createRandomBook(id);
            bookDatabase.put(id, book);
        }
    }

    /**
     * 根据ID获取图书
     */
    public Book getBookById(String id) {
        return bookDatabase.getOrDefault(id, bookDatabase.get(id));
    }

    /**
     * 根据作者查询图书列表
     */
    public List<Book> getBooksByAuthor(String author) {
        return bookDatabase.values().stream().filter(book -> book.getAuthor().equalsIgnoreCase(author)).collect(Collectors.toList());
    }

    /**
     * 根据条件查询图书列表
     */
    public List<Book> getBooksByConditions(Map<String, String> conditions) {
        List<Book> result = new ArrayList<>();
        
        if (conditions == null || conditions.isEmpty()) {
            // 返回所有默认图书
            return new ArrayList<>(bookDatabase.values());
        }
        
        String author = conditions.get("author");
        String publisher = conditions.get("publisher");
        String keyword = conditions.get("keyword");
        


        bookDatabase.values().stream().filter(book -> {
            if (author != null && !author.isEmpty() && !book.getAuthor().contains(author)) {
                return false;
            }
            if (publisher != null && !publisher.isEmpty() && !book.getPublisher().contains(publisher)) {
                return false;
            }
            if (keyword != null && !keyword.isEmpty() && !book.getName().contains(keyword)) {
                return false;
            }
            return true;
        }).collect(Collectors.toList()).stream().forEach(result::add);

        return result;
    }

    /**
     * 创建图书
     */
    public String createBook(Book book) {
        String id = generateBookId();
        book.setIsbn(id);
        bookDatabase.put(id, book);
        return id;
    }

    /**
     * 更新图书
     */
    public boolean updateBook(String id, Book book) {
        if (bookDatabase.containsKey(id)) {
            book.setIsbn(id);
            bookDatabase.put(id, book);
            return true;
        }
        return false;
    }

    /**
     * 删除图书
     */
    public boolean deleteBook(String id) {
        return bookDatabase.remove(id) != null;
    }

    /**
     * 获取所有图书
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookDatabase.values());
    }

    /**
     * 创建随机图书数据
     */
    public Book createRandomBook(String id) {
        Book book = new Book();

        book.setId(id);
        book.setIsbn(id);
        book.setName(getRandomElement(bookNames));
        book.setAuthor(getRandomElement(authors));
        book.setPublisher(getRandomElement(publishers));
        book.setPublishDate(generateRandomDate());
        book.setPrice(generateRandomPrice());
        
        return book;
    }



    /**
     * 生成图书ID
     */
    private String generateBookId() {
        return "BK" + System.currentTimeMillis() % 100000;
    }

    /**
     * 生成随机日期
     */
    private String generateRandomDate() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.now();
        
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        return randomDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 生成随机价格
     */
    private BigDecimal generateRandomPrice() {
        double price = 29.99 + (199.99 - 29.99) * new Random().nextDouble();
        return new BigDecimal(String.format("%.2f", price));
    }

    /**
     * 从列表中随机获取元素
     */
    private String getRandomElement(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    /**
     * 检查图书是否存在
     */
    public boolean bookExists(String id) {
        return bookDatabase.containsKey(id);
    }

    /**
     * 获取图书总数
     */
    public int getBookCount() {
        return bookDatabase.size();
    }
}
