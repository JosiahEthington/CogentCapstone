package com.learning.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.learning.enums.EnabledStatus;
import com.learning.enums.RoleName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Entity

public class Staff extends User {
	private EnabledStatus status = EnabledStatus.ENABLED;

	public Staff() {
		super();
		this.setRoles(new HashSet<Role>());
	}

	public Staff(long id, String username, String fullname, String password, Set<Role> roles) {
		super(id, username, fullname, password, roles);
		// TODO Auto-generated constructor stub
	}
	
	
}
