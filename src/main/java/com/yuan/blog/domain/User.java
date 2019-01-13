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
import java.util.Objects;

/**
 * User. 
 */
@Entity  // 实体 javax
//@XmlRootElement // mediatype 转为xml
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Id // javax 主键
	@GeneratedValue(strategy= GenerationType.IDENTITY) // 自增长策略
	private Long id; // 用户的唯一标识

	// 映射为字段，值不能为空
	@NotEmpty(message = "姓名不能为空")
	@Size(max=20,min=2,message="姓名长度2-30位")
	@Column(nullable = false, length = 20) // 映射为字段，值不能为空
 	private String name;

	@NotEmpty(message = "邮箱不能为空")
	@Size(max=50,message="邮箱长度最多50位")@Email(message= "邮箱格式不对" )
	@Column(nullable = false, length = 50, unique = true)
	private String email;

	@NotEmpty(message = "账号不能为空")
	@Size(max=20,min=3,message="账号长度2-30位")
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

	public User(String name, String username,String email,String password) {
		this.name = name;
		this.email = email;
		this.username = username;
		this.password = password;
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

	// 获取用户的 authority名称
	public String getFirstAuthority(){
		try {
			return this.authorities.get(0).getAuthority();
		} catch (Exception e){
			// todo add log here
		}
		return "";
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


	/**
	 *  重写user的equals 和 hashcode方法 用于判断是否是同一个用户
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(username, user.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
}
