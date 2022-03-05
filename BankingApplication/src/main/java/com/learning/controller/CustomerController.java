package com.learning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.payload.request.ApproveAccountRequest;
import com.learning.payload.request.CreateAccountRequest;
import com.learning.payload.response.AccountCreationResponse;
import com.learning.service.CustomerService;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;
	@PostMapping("/:customerID/account")
	public ResponseEntity<?> createAccount(@PathVariable("customerID") Long customerId, 
			@RequestBody CreateAccountRequest request){
		return ResponseEntity.ok(customerService.addAccount(customerId, request));
	}
	//@PreAuthorize("hasRole('STAFF')")
	@PutMapping("/:customerID/account/:accountNo")
	public ResponseEntity<?> approveAccount(
			@PathVariable("customerID") Long customerId, 
			@PathVariable("accountNo") long accountNo, 
			@RequestBody ApproveAccountRequest request){		
		return ResponseEntity.ok(customerService.approveAccount(customerId, accountNo, request));
	}
	
	@GetMapping("/:customerID/account")
	public ResponseEntity<?> getAccounts(@PathVariable("customerID") Long customerId){	
		return ResponseEntity.ok(customerService.getCustomerAccounts(customerId));
	}
	
	@GetMapping("/:customerID")
	public ResponseEntity<?> getCustomer(@PathVariable("customerID") Long customerId){
		return ResponseEntity.ok(customerService.getCustomer(customerId));
	}
	
	@GetMapping("/:customerID/account/:accountID")
	public ResponseEntity<?> getCustomerAccount(
			@PathVariable("customerID") Long customerId, 
			@PathVariable("accountID") Long accountId){
		return ResponseEntity.ok(customerService.getCustomerAccount(customerId, accountId));
	}
}
