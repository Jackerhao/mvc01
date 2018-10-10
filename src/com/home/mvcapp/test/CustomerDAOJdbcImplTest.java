package com.home.mvcapp.test;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.home.mvcapp.dao.CriteriaCustomer;
import com.home.mvcapp.dao.CustomerDAO;
import com.home.mvcapp.dao.impl.CustomerDAOJdbcImpl;
import com.home.mvcapp.domain.Customer;



public class CustomerDAOJdbcImplTest {

	private CustomerDAO customerDAO=new CustomerDAOJdbcImpl();
	
	@Test
	public void testgetForListWithCriteriaCustomer() {
		CriteriaCustomer cc = new CriteriaCustomer("k", null, null);
		List<Customer> customers = customerDAO.getForListWithCriteriaCustomer(cc);
		System.out.println(customers);
	}
	
	@Test
	public void testGetAll() {
     List<Customer> customers=customerDAO.getAll();
     System.out.println(customers);
     
	}

	@Test
	public void testSaveCustomer() {
	Customer customer=new Customer();
	customer.setAddress("ShangHai");
	customer.setName("Jerry");
	customer.setPhone("13766733128");
	
	customerDAO.save(customer);
	
	}

	@Test
	public void testGetInteger() {
	    Customer cust=customerDAO.get(7);
	    System.out.println(cust);
	    
	}

	@Test
	public void testDelete() {
	customerDAO.delete(2);
	}

	@Test
	public void testGetCountWithName() {
	long count=customerDAO.getCountWithName("Jack");
	System.out.println(count);
	}

}
