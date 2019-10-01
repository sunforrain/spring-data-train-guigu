package com.atguigu.springdata.test;

import com.atguigu.springdata.Person;
import com.atguigu.springdata.PersonRepository;
import com.atguigu.springdata.PersonService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SpringDataTest {

    private ApplicationContext ctx = null;
    private PersonRepository personRepository = null;
    private PersonService personService = null;

    {
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        personRepository = ctx.getBean(PersonRepository.class);
        personService = ctx.getBean(PersonService.class);
    }

    // 测试使用继承了CrudRepository接口的方法执行批量保存操作
    @Test
    public void testCRUDRepository () {
        List<Person> persons = new ArrayList<>();

        // 这里使用字符本身也是数字的原理,循环创造a~z的person
        for(int i='a'; i <= 'z'; i++) {
            Person person = new Person();
            person.setAddressId(i+1);
            person.setBirth(new Date());
            person.setEmail((char)i + "" + (char)i + "atguigu.com");
            person.setLastName((char)i + "" + (char)i);

            persons.add(person);
        }

        personService.savePersons(persons);
    }

    // 测试自定义JPQL进行修改操作
    @Test
    public void testModifying () {
        //		personRepsotory.updatePersonEmail(1, "mmmm@atguigu.com");
        personService.updatePersonEmail("eeee@atguigu.com", 5);
    }

    // 本地sql查询
    @Test
    public void testNativeQuery(){
        long count = personRepository.getTotalCount();
        System.out.println(count);
    }

    // LIKE 的使用
    @Test
    public void testQueryAnnotationLikeParam(){
        // JPQL没写%的情况
//		List<Person> persons = personRepository.testQueryAnnotationLikeParam("%a%", "%bb%");
//		System.out.println(persons.size());

		// JPQL写了%的情况
//		List<Person> persons = personRepository.testQueryAnnotationLikeParam("A", "bb");
//		System.out.println(persons.size());

        // %%也适用于命名参数
        List<Person> persons = personRepository.testQueryAnnotationLikeParam2("bb", "A");
        System.out.println(persons.size());
    }

    //使用 @Query 注解可以自定义 JPQL (使用命名参数)
    @Test
    public void testQueryAnnotationParams2(){
        List<Person> persons = personRepository.testQueryAnnotationParams2("aa@guigu.com", "aa");
        System.out.println(persons);
    }

    //使用 @Query 注解可以自定义 JPQL (使用占位符)
    @Test
    public void testQueryAnnotationParams1(){
        List<Person> persons = personRepository.testQueryAnnotationParams1("aa", "aa@guigu.com");
        System.out.println(persons);
    }

    // 使用 @Query 注解可以自定义 JPQL (无参)
    @Test
    public void testQueryAnnotation(){
        Person person = personRepository.getMaxIdPerson();
        System.out.println(person);
    }

    // 测试关于级联属性查询的注意点: _
    @Test
    public void testKeyWords2(){
//        List<Person> persons = personRepository.getByAddressIdGreaterThan(1);
        List<Person> persons = personRepository.getByAddress_IdGreaterThan(1);
        System.out.println(persons);
    }

    // 定义查询方法进行查询
    @Test
    public void testKeyWords () {
        List<Person> persons = personRepository.getByLastNameStartingWithAndIdLessThan("X", 10);
        System.out.println(persons);

        persons = personRepository.getByLastNameEndingWithAndIdLessThan("X", 10);
        System.out.println(persons);

        persons = personRepository.getByEmailInOrBirthLessThan(Arrays.asList("AA@atguigu.com", "FF@atguigu.com",
                "SS@atguigu.com"), new Date());
        System.out.println(persons.size());
    }

    // 一个springdata的helloworld
    @Test
    public void testHelloWorldSpringData () {
        Person person = personRepository.getByLastName("aa");
        System.out.println(person);
    }

    @Test
    public void testDataSource() throws SQLException {
        DataSource dataSource = ctx.getBean(DataSource.class);
        System.out.println(dataSource.getConnection());
    }
}
