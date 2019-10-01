package com.atguigu.springdata;

import com.mchange.util.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public void updatePersonEmail (String email, Integer id){
        personRepository.updatePersonEmail(id, email);
    }

    public void savePersons (List<Person> persons) {
        personRepository.save(persons);
    }

    // 注意对于findAll返回的是一个Iterable接口类型的,而不是util里面那个类
    public List<Person> findPersonsByIds (List<Integer> ids) {
        Iterable<Person> it =  personRepository.findAll(ids);
        Iterator<Person> iterator = it.iterator();
        List<Person> persons = new ArrayList<>();
        while (iterator.hasNext()){
            // 注意如果每次在循环里面每次都获取一次遍历器,会导致死循环,因为每次都是从第一个开始遍历!!!
//            persons.add(it.iterator().next());
            persons.add(iterator.next());
        }
        return persons;
    }
}
