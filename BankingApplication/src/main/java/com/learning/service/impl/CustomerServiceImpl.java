package com.learning.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.learning.entity.Account;
import com.learning.entity.Customer;
import com.learning.entity.Transaction;
import com.learning.enums.EnabledStatus;
import com.learning.exception.AccountCreationException;
import com.learning.exception.NoDataFoundException;
import com.learning.payload.request.AddBeneficiaryRequest;
import com.learning.payload.request.ApproveAccountRequest;
import com.learning.payload.request.AuthenticateRequest;
import com.learning.payload.request.CreateAccountRequest;
import com.learning.payload.request.RegisterRequest;
import com.learning.payload.request.TransferRequest;
import com.learning.payload.request.UpdatePasswordRequest;
import com.learning.payload.response.AccountCreationResponse;
import com.learning.payload.response.AccountDetailsResponse;
import com.learning.payload.response.AccountSummaryResponse;
import com.learning.payload.response.ApproveAccountResponse;
import com.learning.payload.response.BeneficiarySummary;
import com.learning.payload.response.GetCustomerResponse;
import com.learning.payload.response.RegisterUserResponse;
import com.learning.payload.response.UpdateCustomerResponse;
import com.learning.repo.CustomerRepo;
import com.learning.service.CustomerService;

public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepo customerRepo;
	@Override
	public RegisterUserResponse registerCustomer(RegisterRequest request) {
		//create a customer from the request
		Customer customer = new Customer();
		customer.setFullname(request.getFullname());
		customer.setUsername(request.getUsername());
		customer.setPassword(request.getPassword()); //should encrypt password here.
		//customer creation date is now.
		customer.setCreatedDate(LocalDate.now());
		//save customer to DB
		Customer temp = customerRepo.save(customer);
		//Create response from the DB returned customer.
		RegisterUserResponse response = new RegisterUserResponse();
		response.setFullname(temp.getFullname());
		response.setId(temp.getId());
		response.setPassword(temp.getPassword());//should be encrypted password.
		response.setUsername(temp.getUsername());
		return response;
	}

	@Override
	public String authenticate(AuthenticateRequest request) {
		Customer customer = customerRepo.findByUsername(request.getUsername()).orElseThrow(()-> new NoDataFoundException("user not found"));
		return "JWT TOKEN HERE";
	}

	@Override
	public AccountCreationResponse addAccount(long customerID, CreateAccountRequest request) {
		Account newAccount = new Account();
		//Retrieve customer based on URL
		Customer currentCustomer = customerRepo.findById(customerID).orElseThrow(()->new NoDataFoundException("Customer Not Found"));
		//Store list of customer's current accounts, so we can tell which one is new, later.
		Set<Account> oldAccounts = currentCustomer.getAccounts();
		//Set all attributes of new account, based on defaults, and on request.
		newAccount.setAccountBalance(request.getAccountBalance());
		newAccount.setAccountType(request.getAccountType());
		newAccount.setApproved(request.getApproved());
		newAccount.setAccountStatus(EnabledStatus.ENABLED);
		newAccount.setCustomer(currentCustomer);
		newAccount.setDateOfCreation(LocalDate.now());
		newAccount.setTransactions(new ArrayList<Transaction>());
		//Add created account to customer.
		currentCustomer.getAccounts().add(newAccount);
		//Save customer to update list of accounts.  The returned Customer should have the
		//newly created Account, with its generated Account Number.
		Customer updatedCustomer = customerRepo.save(currentCustomer);
		Set<Account> newAccounts = updatedCustomer.getAccounts();
		//Compare the sets to find the newly created Account.
		newAccounts.removeAll(oldAccounts);
		//newAccounts should have exactly 1 account in it. If it doesn't, something is wrong.
		if(newAccounts.isEmpty() || newAccounts.size() >1) {
			throw new AccountCreationException("Account Creation Failed");
		}
		Account createdAccount = ((Account[])newAccounts.toArray())[0];
		//Populate the response with the details of the new Account.
		AccountCreationResponse result = new AccountCreationResponse();
		result.setAccountType(createdAccount.getAccountType());
		result.setAccountBalance(createdAccount.getAccountBalance());
		result.setApproved(createdAccount.getApproved());
		result.setAccountNumber(createdAccount.getAccountNumber());
		result.setDateOfCreation(createdAccount.getDateOfCreation());
		result.setCustomerId(createdAccount.getCustomer().getId());

		return result;
	}

	@Override
	public ApproveAccountResponse approveAccount(long customerID, long accountNo, ApproveAccountRequest request) {
		// find the current customer
		Customer currentCustomer = customerRepo.findById(customerID).orElseThrow(()->new NoDataFoundException("Customer Not Found"));
		// get all the accounts
		Set<Account> accounts= currentCustomer.getAccounts();
		ApproveAccountResponse accountResponse = new ApproveAccountResponse();
		//for each loop to iterate the accounts
		for (Account account: accounts) {
			if(account.getAccountNumber() == accountNo) {
				account.setApproved(request.getApproved());
	
				// want the response to the user
				accountResponse.setAccountNumber(accountNo);
				accountResponse.setApproval(request.getApproved());
				
				customerRepo.save(currentCustomer);
				return accountResponse;
			} 
		}
		
		// if we couldn't find the account number, we will throw the exception 
		throw new NoDataFoundException("Please check Account Number");
	}

	@Override
	public GetCustomerResponse getCustomer(long customerID) {
		Customer customer = customerRepo.findById(customerID)
				.orElseThrow(()-> new NoDataFoundException(
						"Sorry, Customer with ID:" + customerID + " Not Found"));
		GetCustomerResponse response = new GetCustomerResponse();
		response.setAadhar(customer.getAadhar());
		response.setFullName(customer.getFullname());
		response.setPan(customer.getPan());
		response.setPhone(customer.getPhone());
		response.setUsername(customer.getUsername());
		return response;
	}

	@Override
	public UpdateCustomerResponse updateCustomer(long customerID, Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountDetailsResponse getCustomerAccount(long customerID, long accountID) {
		Customer customer = customerRepo.findById(customerID)
				.orElseThrow(()-> new NoDataFoundException(
						"Sorry, Customer with ID:" + customerID + " Not Found"));
		Set<Account> accounts = customer.getAccounts();
		for(Account x: accounts) {
			if(x.getAccountNumber()== accountID) {
				AccountDetailsResponse response = new AccountDetailsResponse();
				response.setAccountNumber(x.getAccountNumber());
				response.setAccountType(x.getAccountType());
				response.setBalance(x.getAccountBalance());
				response.setStatus(x.getAccountStatus());
				response.setTransactions(x.getTransactions());
				return response;
			}
		}
		throw new NoDataFoundException("Account with ID: " + accountID + " not found");
	}

	@Override
	public String addBeneficiary(long customerID, AddBeneficiaryRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BeneficiarySummary> getBeneficiaries(long customerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteBeneficiary(long customerID, long beneficiaryID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transferFunds(TransferRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestion(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String validateAnswer(String username, String answer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updatePassword(String username, UpdatePasswordRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccountSummaryResponse> getCustomerAccounts(long customerID) {
		//Get the customer
		Customer currentCustomer = customerRepo.findById(customerID)
				.orElseThrow(()-> new NoDataFoundException("Customer not found"));
		//Get the accounts
		Set<Account> accounts = currentCustomer.getAccounts();
		//create a list for the summaries.
		List<AccountSummaryResponse> results = new ArrayList<>();
		//generate the summaries from the accounts.
		for(Account x : accounts) {
			AccountSummaryResponse summary = new AccountSummaryResponse();
			summary.setAccountNumber(x.getAccountNumber());
			summary.setAccountType(x.getAccountType());
			summary.setBalance(x.getAccountBalance());
			summary.setStatus(x.getAccountStatus());
			results.add(summary);
		}
		return results;
	}

}
