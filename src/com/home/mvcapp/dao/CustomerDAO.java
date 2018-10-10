package com.home.mvcapp.dao;

import java.util.List;

import com.home.mvcapp.domain.Customer;

public interface CustomerDAO {
	
	/**
	 * 返回满足查询条件的List
	 * @param cc:封装了查询条件
	 * @return
	 */
	public List<Customer> getForListWithCriteriaCustomer(CriteriaCustomer cc);

	public List<Customer>getAll();
	
	public void save(Customer customer);
	
	public Customer get(Integer id);
	
	public void delete(Integer id);
	
	public void update(Customer customer);
	
	/**
	 * 返回和name相等的记录数.
	 * @param name
	 * @return
	 */
	public long getCountWithName(String name);
}
