package com.yuan.blog.domain;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User. 
 */
@Entity  // 实体 javax
@XmlRootElement // mediatype 转为xml
public class User {

	private static final long serialVersionUID = 1L;

	@Id // javax 主键
	@GeneratedValue(strategy= GenerationType.IDENTITY) // 自增长策略
	private long id; // 用户的唯一标识

	// 映射为字段，值不能为空
	@Column(nullable = false)
 	private String name;

	@Column(nullable = false)
	private Integer age;

	protected User() { // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
	}

	public User(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return String.format(
				"User[id=%d, name='%s', age='%d']",
				id, name, age);
	}
}
