package com.project.java.persistences.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Setter
@Getter
@AllArgsConstructor
@Builder
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "password")
	private String password;
	
	@OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
	@JsonManagedReference
	private Set<SubSystem> subSystems;

	@OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
	@JsonManagedReference
	private Set<User> listUser = new HashSet<>();

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "manager_id")
	private User manager;

	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIgnore
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinTable(name ="user_permission", joinColumns = @JoinColumn(name =" user_id"), inverseJoinColumns = @JoinColumn(name="permission_id"))
	private Set<Permission> permissions = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<User> getListUser() {
		return listUser;
	}

	public void setListUser(Set<User> listUser) {
		this.listUser = listUser;
	}

	public User(String userName, String password) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.listUser = new HashSet<>();
		this.roles = new HashSet<>();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		roles.stream().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleName().toString())));
		return authorities;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public User(Long id, String userName, String password, Set<SubSystem> subSystems, Set<User> listUser, User manager,
			Set<Role> roles) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.subSystems = subSystems;
		this.listUser = listUser;
		this.manager = manager;
		this.roles = roles;
	}
	

}
