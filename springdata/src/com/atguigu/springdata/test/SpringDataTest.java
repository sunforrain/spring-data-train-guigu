package com.atguigu.springdata.test;

import com.atguigu.springdata.Person;
import com.atguigu.springdata.PersonRepository;
import com.atguigu.springdata.PersonService;
import com.atguigu.springdata.commonrepositorymethod.AddressRepository;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
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

    @Test
    public void testCommonCustomRepositoryMethod(){
        ApplicationContext ctx2 = new ClassPathXmlApplicationContext("classpath:com/atguigu/springdata/commonrepositorymethod/applicationContext2.xml");
        AddressRepository addressRepository = ctx2.getBean(AddressRepository.class);
        addressRepository.method();
    }

    // 为一个repository添加自定义方法的示例
    @Test
    public void testCustomRepositoryMethod(){
        personRepository.test();
    }

    /**
     *  目标: 实现带查询条件的分页.  id > 5 的条件
     *
     *  JpaSpecificationExecutor 可以用来构建动态的查询方法
     *  调用 JpaSpecificationExecutor 的 Page<T> findAll(Specification<T> spec, Pageable pageable);
     * 	 Specification: 封装了 JPA Criteria 查询的查询条件
     * 	 Pageable: 封装了请求分页的信息: 例如 pageNo, pageSize, Sort
     */
    @Test
    public void testJpaSpecificationExecutor () {
        int pageNo = 3 - 1;
        int pageSize = 5;
        PageRequest pageable = new PageRequest(pageNo, pageSize);

        //通常使用 Specification 的匿名内部类
        Specification<Person> specification = new Specification<Person>() {
            /**
             * @param *root: 代表查询的实体类.
             * @param criteriaQuery: 可以从中可到 Root 对象, 即告知 JPA Criteria 查询要查询哪一个实体类. 还可以
             * 来添加查询条件, 还可以结合 EntityManager 对象得到最终查询的 TypedQuery 对象.
             * @param *criteriaBuilder: CriteriaBuilder 对象. 用于创建 Criteria 相关对象的工厂. 当然可以从中获取到 Predicate 对象
             * @return: *Predicate 类型, 代表一个查询条件.
             */
            @Override
            public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                // 使用path实现一个属性导航的目的,告诉criteriaBuilder比较的属性是id
                Path path = root.get("id");
                // 使用criteria的builder,这里是查询id大于5的
                Predicate predicate = criteriaBuilder.gt(path, 5);
                return predicate;
            }
        };

        Page<Person> page = personRepository.findAll(specification, pageable);

        System.out.println("总记录数: " + page.getTotalElements());
        System.out.println("当前第几页: " + (page.getNumber() + 1));
        System.out.println("总页数: " + page.getTotalPages());
        System.out.println("当前页面的 List: " + page.getContent());
        System.out.println("当前页面的记录数: " + page.getNumberOfElements());
    }

    // 使用继承了JpaRepository接口的接口执行merge操作
    @Test
    public void testJpaRepository () {
        Person person = new Person();
        person.setBirth(new Date());
        person.setEmail("xy@atguigu.com");
        person.setLastName("xyz");
        // 不设置id,直接执行insert
        // 设置了id则会去先select对应id的数据后,执行update
        // 这时就相当于merge方法
        person.setId(27);

        Person person2 = personRepository.saveAndFlush(person);

        // 设置了id就能发现两个对象不是一个对象
        System.out.println(person == person2);

    }

    // 测试使用继承了PagingAndSortingRepository接口的接口执行分页查询操作
    @Test
    public void testPagingAndSortingRepository () {
        //pageNo 从 0 开始.这里要查第三页就给减一
        int pageNo = 3 - 1;
        int pageSize = 5;
        //Pageable 接口通常使用的其 PageRequest 实现类. 其中封装了需要分页的信息
        //排序相关的. Sort 封装了
        // 排序的信息
        //Order 是具体针对于某一个属性进行升序还是降序.
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        Page<Person> page =personRepository.findAll(pageRequest);

        System.out.println("总记录数: " + page.getTotalElements());
        // 这里不用()包一下会变成x1的页码
        System.out.println("当前第几页: " + (page.getNumber() + 1));
        System.out.println("总页数: " + page.getTotalPages());
        System.out.println("当前页面的 List: " + page.getContent());
        System.out.println("当前页面的记录数: " + page.getNumberOfElements());
    }

    //  测试使用继承了CrudRepository接口的接口执行批量查询操作
    @Test
    public void testFindAll () {
        List<Integer> ids = new ArrayList<>();
        for (int i = 6; i< 20; i++) {
            ids.add(i);
        }
        List<Person> personList = personService.findPersonsByIds(ids);
        System.out.println(personList.size());
    }

    // 测试使用继承了CrudRepository接口的接口执行批量保存操作
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
