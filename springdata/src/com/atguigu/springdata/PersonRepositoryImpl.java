package com.atguigu.springdata;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 为某一个 Repository 上添加自定义方法
 * 为自定义接口的方法提供的实现类
 * 类名需在要声明的 Repository 后添加 Impl, 并实现方法
 */
public class PersonRepositoryImpl implements PersonDao {

    @PersistenceContext
    private EntityManager entityManager;
    // 模拟一下,这样就直接打通了springData和jpa了
    @Override
    public void test() {
        Person person = entityManager.find(Person.class, 11);
        System.out.println("--->" + person);
    }
}
