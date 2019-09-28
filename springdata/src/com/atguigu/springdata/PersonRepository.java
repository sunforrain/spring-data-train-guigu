package com.atguigu.springdata;


import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.List;

/**
 * 1. Repository 是一个空接口. 即是一个标记接口
 * 2. 若我们定义的接口继承了 Repository, 则该接口会被 IOC 容器识别为一个 Repository Bean.
 *  纳入到 IOC 容器中. 进而可以在该接口中定义满足一定规范的方法.
 *
 *  3. 实际上, 也可以通过 @RepositoryDefinition 注解来替代继承 Repository 接口
 */

/**
 * 在 Repository 子接口中声明方法
 * 1. 不是随便声明的. 而需要符合一定的规范
 * 2. 查询方法以 find | read | get 开头
 * 3. 涉及条件查询时，条件的属性用条件关键字连接
 * 4. 要注意的是：条件属性以首字母大写。
 * 5. 支持属性的级联查询. 若当前类有符合条件的属性, 则优先使用, 而不使用级联属性.
 * 若需要使用级联属性, 则属性之间使用 _ 进行连接.
 */
//@RepositoryDefinition(domainClass = Person.class,idClass = Integer.class)
public interface PersonRepository extends Repository<Person, Integer>{

    // 根据lastName 来获取对应的 Person
    Person getByLastName(String lastName);

    // WHERE lastName LIKE ?% AND id < ?
    List<Person> getByLastNameStartingWithAndIdLessThan(String lastName, Integer id);

    // WHERE lastName LIKE %? AND id < ?
    List<Person> getByLastNameEndingWithAndIdLessThan(String lastName, Integer id);

    // WHERE email IN (?,?,?) OR birth < ?
    List<Person> getByEmailInOrBirthLessThan(List<String> emails, Date date);

    // WHERE a.id > ?
    // getByAddressIdGreaterThan会优先使用本类的属性,这会导致级联属性的失效
    // getByAddress_IdGreaterThan会优先使用级联属性,这会导致级联属性的失效
    List<Person> getByAddress_IdGreaterThan(Integer id);
}