package com.joysky.ms.ct.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 数据库连接信息日志记录器
 */
@Slf4j
@Component
public class DatabaseInfoLogger {

    @Autowired
    private DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void logDatabaseConnectionInfo() {
        try {
            log.info("=== 数据库连接信息详情 ===");
            
            // HikariCP连接池信息
            if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
                log.info("连接池类型: HikariCP");
                log.info("连接池名称: {}", hikariDataSource.getPoolName());
                log.info("最大连接数: {}", hikariDataSource.getMaximumPoolSize());
                log.info("最小空闲连接数: {}", hikariDataSource.getMinimumIdle());
                log.info("连接超时时间: {} ms", hikariDataSource.getConnectionTimeout());
                log.info("空闲超时时间: {} ms", hikariDataSource.getIdleTimeout());
                log.info("最大生命周期: {} ms", hikariDataSource.getMaxLifetime());
                log.info("连接测试查询: {}", hikariDataSource.getConnectionTestQuery());
                log.info("当前活跃连接数: {}", hikariDataSource.getHikariPoolMXBean().getActiveConnections());
                log.info("当前空闲连接数: {}", hikariDataSource.getHikariPoolMXBean().getIdleConnections());
                log.info("总连接数: {}", hikariDataSource.getHikariPoolMXBean().getTotalConnections());
            }
            
            // 数据库元数据信息
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                
                log.info("=== 数据库详细信息 ===");
                log.info("数据库产品名称: {}", metaData.getDatabaseProductName());
                log.info("数据库产品版本: {}", metaData.getDatabaseProductVersion());
                log.info("数据库驱动名称: {}", metaData.getDriverName());
                log.info("数据库驱动版本: {}", metaData.getDriverVersion());
                log.info("JDBC URL: {}", metaData.getURL());
                log.info("用户名: {}", metaData.getUserName());
                log.info("是否只读: {}", connection.isReadOnly());
                log.info("自动提交模式: {}", connection.getAutoCommit());
                log.info("事务隔离级别: {}", getTransactionIsolationName(connection.getTransactionIsolation()));
                log.info("连接有效性: {}", connection.isValid(5));
                
                // 数据库特性支持
                log.info("=== 数据库特性支持 ===");
                log.info("支持事务: {}", metaData.supportsTransactions());
                log.info("支持批量更新: {}", metaData.supportsBatchUpdates());
                log.info("支持保存点: {}", metaData.supportsSavepoints());
                log.info("支持命名参数: {}", metaData.supportsNamedParameters());
                log.info("最大连接数: {}", metaData.getMaxConnections());
                log.info("最大表名长度: {}", metaData.getMaxTableNameLength());
                
            }
            
            log.info("=== 数据库连接信息记录完成 ===");
            
        } catch (SQLException e) {
            log.error("获取数据库连接信息时发生错误: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("记录数据库信息时发生未知错误: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取事务隔离级别名称
     */
    private String getTransactionIsolationName(int level) {
        switch (level) {
            case Connection.TRANSACTION_NONE:
                return "TRANSACTION_NONE";
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                return "TRANSACTION_READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED:
                return "TRANSACTION_READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ:
                return "TRANSACTION_REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE:
                return "TRANSACTION_SERIALIZABLE";
            default:
                return "UNKNOWN(" + level + ")";
        }
    }
}