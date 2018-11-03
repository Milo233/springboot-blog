package com.yuan.blog.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User. 
 */
@Entity  // 实体 javax
//@XmlRootElement // mediatype 转为xml
public class User implements UserDetails {
	// extends UserDetail
	private static final long serialVersionUID = 1L;

	@Id // javax 主键
	@GeneratedValue(strategy= GenerationType.IDENTITY) // 自增长策略
	private Long id; // 用户的唯一标识

	// 映射为字段，值不能为空
	@NotEmpty(message = "姓名不能为空")
	@Size(min=2, max=20)
	@Column(nullable = false, length = 20) // 映射为字段，值不能为空
 	private String name;

	@NotEmpty(message = "邮箱不能为空")
	@Size(max=50)@Email(message= "邮箱格式不对" )
	@Column(nullable = false, length = 50, unique = true)
	private String email;

	@NotEmpty(message = "账号不能为空")
	@Size(min=3, max=20)
	@Column(nullable = false, length = 20, unique = true)
	private String username; // 用户账号，用户登录时的唯一标识

	@NotEmpty(message = "密码不能为空")
	@Size(max=100)
	@Column(length = 100)
	private String password; // 登录时密码

	@Column(length = 500)
	private String avatar; // 头像图片地址

	protected User() { // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
	}

	public User(Long id,String name, String username,String email) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.username = username;
	}

	@ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
	private List<Authority> authorities;

	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//  需将 List<Authority> 转成 List<SimpleGrantedAuthority>，否则前端拿不到角色列表名称
		List<SimpleGrantedAuthority> simpleAuthorities = new ArrayList<>();
		for(GrantedAuthority authority : this.authorities){
			simpleAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
		}
		return simpleAuthorities;
	}

	// 加密密码
	public void setEncodePassword(String password){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePwd = encoder.encode(password);
		this.password = encodePwd;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}



	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

/*	@Override
	public String toString() {
		return String.format(
				"User[id=%d, name='%s', age='%d']",
				id, name, age);
	}*/
}
