package com.joysky.ms.ct.login.repository;

import com.joysky.ms.ct.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据手机号查询用户是否存在
     * @param phone 手机号
     * @return true-存在，false-不存在
     */
    boolean existsByPhone(String phone);

    /**
     * 根据邮箱查询用户是否存在
     * @param email 邮箱
     * @return true-存在，false-不存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据用户名查询用户是否存在
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean existsByUsername(String username);

    User findByPhone(String username);

    User findByUsername(String username);

    User findByEmail(String username);
    
    /**
     * 根据用户名、手机号或邮箱查询用户（UNION查询优化）
     * 使用UNION替代OR条件，避免索引失效问题
     * @param identifier 用户标识（用户名、手机号或邮箱）
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier " +
           "UNION " +
           "SELECT u FROM User u WHERE u.phone = :identifier " +
           "UNION " +
           "SELECT u FROM User u WHERE u.email = :identifier")
    User findByUsernameOrPhoneOrEmailUnion(@Param("identifier") String identifier);
    
    /**
     * 智能路由查询方法 - 根据标识符类型选择最优索引
     * 优先使用单一索引查询，性能更佳
     */
    
    /**
     * 根据用户名精确查询
     * @param username 用户名
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUsernameExact(@Param("username") String username);
    
    /**
     * 根据手机号精确查询
     * @param phone 手机号
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE u.phone = :phone")
    User findByPhoneExact(@Param("phone") String phone);
    
    /**
     * 根据邮箱精确查询
     * @param email 邮箱
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmailExact(@Param("email") String email);
    
    /**
     * 保留原有OR查询方法作为备用（已废弃，建议使用智能路由）
     * @deprecated 使用 findByUsernameOrPhoneOrEmailOptimized 替代
     */
    @Deprecated
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.phone = :identifier OR u.email = :identifier")
    User findByUsernameOrPhoneOrEmail(@Param("identifier") String identifier);
}