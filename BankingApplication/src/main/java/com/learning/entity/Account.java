package com.learning.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.learning.enums.EnabledStatus;
import com.learning.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long accountNumber;
	@NotNull
	private AccountType accountType;

	private double accountBalance;
	@NotBlank
	private String approved;
	@NotNull
	private LocalDate dateOfCreation;
	@NotNull
	private EnabledStatus accountStatus;
	
	@ManyToOne
	@NotNull
	private Customer customer;
	
	@OneToMany
	private List<Transaction> transactions;
}