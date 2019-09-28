package com.atguigu.springdata.test;

import com.atguigu.springdata.Person;
import com.atguigu.springdata.PersonRepository;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SpringDataTest {

    private ApplicationContext ctx = null;
    private PersonRepository personRepository = null;

    {
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        personRepository = ctx.getBean(PersonRepository.class);
    }

    @Test
    public void testKeyWords2(){
//        List<Person> persons = personRepository.getByAddressIdGreaterThan(1);
        List<Person> persons = personRepository.getByAddress_IdGreaterThan(1);
        System.out.println(persons);
    }

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
