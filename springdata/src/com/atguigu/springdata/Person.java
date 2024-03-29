package com.atguigu.springdata;

import java.util.Date;

import javax.persistence.*;

@Table(name="JPA_PERSONS")
@Entity
public class Person {

    private Integer id;
    private String lastName;

    private String email;
    private Date birth;

    private Address address;

    private Integer addressId;

    @GeneratedValue
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    // 这个地方如果不另外指定个列名,会和Address的列名冲突导致错误
    @Column(name="ADD_ID")
    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    @JoinColumn(name="ADDRESS_ID")
    @ManyToOne
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Person [id=" + id + ", lastName=" + lastName + ", email="
                + email + ", brith=" + birth + "]";
    }
}
