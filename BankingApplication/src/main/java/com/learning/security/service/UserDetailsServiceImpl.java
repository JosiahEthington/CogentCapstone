package com.learning.security.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.learning.entity.Customer;
import com.learning.entity.Staff;
import com.learning.entity.User;
import com.learning.exception.NoDataFoundException;
import com.learning.repo.CustomerRepo;
import com.learning.repo.StaffRepo;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	StaffRepo staffRepo;
	@Autowired
	CustomerRepo customerRepo;
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Staff> staff = staffRepo.findByUsername(username);
		Optional<Customer> customer = customerRepo.findByUsername(username);
		User user = !staff.isEmpty() ? staff.get() : !customer.isEmpty() ? customer.get() :null; 
		if(user == null) {
			throw new NoDataFoundException("user not found");
		}
		//System.out.println(user.getRoles());
		return UserDetailsImpl.build(user);
	}

}
