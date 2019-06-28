package com.gdkm.weixin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gdkm.weixin.domain.User;

// Spring Data JPA会自动生成此接口的实例，程序员不需要写实现类
// 实现的原理是动态代理技术
@Repository
// extends JpaRepository可以得到几乎所有数据的CRUD方法
// <User, String> 前者表示管理哪个类的数据（对应哪个表），后者表示主键的数据类型
public interface UserRepository extends JpaRepository<User, String> {

	// 会自动根据方法生成SQL语句
	// select * from wx_user where open_id = ?
	User findByOpenId(String openId);
}
