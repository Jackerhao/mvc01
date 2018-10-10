package com.home.mvcapp.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.home.mvcapp.dao.CriteriaCustomer;
import com.home.mvcapp.dao.CustomerDAO;
import com.home.mvcapp.dao.factory.CustomerDAOFactory;
import com.home.mvcapp.dao.impl.CustomerDAOJdbcImpl;
import com.home.mvcapp.dao.impl.CustomerDAOXMLImpl;
import com.home.mvcapp.domain.Customer;

@WebServlet(name="CustomerServlet", urlPatterns="*.do")
public class CustomerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private CustomerDAO customerDAO = CustomerDAOFactory.getInstance().getCustomerDao();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	doPost(request,response);
	}

	/*protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String method=request.getParameter("method");
		
		switch(method) {
		case "add":    add(request,response);break;
		case "query":  query(request,response);break;
		case "delete": delete(request,response);break;
		case "update": update(request,response);break;
		
		}
	}*/
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		//1.��ȡServletPath:/edit.do��addCustomer.do
	    String servletPath =req.getServletPath();
	    //ȥ��.do�õ������ڵõ�
	    String methodName = servletPath.substring(1);
	    methodName=methodName.substring(0, methodName.length()-3);
	    //System.out.println(methodName);
	    try {
	    	//3.���÷����ȡmethodName��Ӧ�ķ���
			Method method=getClass().getDeclaredMethod(methodName, HttpServletRequest.class,
					HttpServletResponse.class);
			//4.���÷�����ö�Ӧ�ķ���
			method.invoke(this, req,resp);
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void edit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String forwardPath ="/error.jsp";
		//1.��ȡ�������id
		String idStr = request.getParameter("id");
		int id = -1;
		//2.����CustomerDAO��customerDAO.get(id) ��ȡ��id��Ӧ��Customer ����customer
		try {
			Customer customer = customerDAO.get(Integer.parseInt(idStr));
			if(customer != null) {
				forwardPath="/updateCustomer.jsp";
				//3.��customer����request��
				request.setAttribute("customer", customer);
			}
		} catch (NumberFormatException e) {
			//e.printStackTrace();
		}
		//4.��Ӧupdatecustomer.jspҳ��:ת��.
		request.getRequestDispatcher(forwardPath).forward(request, response);
	}
	
	private void update(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		//System.out.println("update");
		//1.��ȡ������:id, name, address, phone,oldName
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		String oldName = request.getParameter("oldName");
		
		//2.����name�Ƿ��Ѿ���ռ��:
		//2.1�Ƚ�name��oldName�Ƿ���ͬ,����ͬ˵��name����.
		//2.1������ͬ,�����CustomerDAO��getCountWithName(String name) ��ȡname�����ݿ�
		//�Ƿ����
		if(!oldName.equalsIgnoreCase(name)) {
			long count = customerDAO.getCountWithName(name);
			
			if(count >0) {
				//2.2.1��updatecustomer.jspҳ����ʾһ��������Ϣ:�û���name�Ѿ���ռ��,������ѡ��!
				//��request�з���һ������message:�û���name�Ѿ���ռ��,������ѡ��!,
				//��ҳ����ͨ��request.getAttribute("message")�ķ�ʽ��ʾ.
				request.setAttribute("message", "�û���"+name+"�Ѿ���ռ��,������ѡ��!");
				//2.2.2 addCustomer.jsp�ı����Ի���.
				//address,phone ��ʾ�ύ�����µ�ֵ,��name��ʾoldName,���������ύ��name
				
				//2.2.3��������: return 
				request.getRequestDispatcher("/updateCustomer.jsp").forward(request, response);
			    return;
			}
		}
		//3.����֤ͨ���� �ѱ�������װΪһ��Customer����customer
		Customer customer = new Customer(name,address,phone);
		customer.setId(Integer.parseInt(id));
		
		//4.����CustomerDAO��update (Customer customer)ִ�б������
		customerDAO.update(customer);
		
		//5.�ض���query.do
		response.sendRedirect("query.do");
	}
	
	private void delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		//System.out.println("delete");
		String idStr = request.getParameter("id");
		int id=0;
		
		//try ... catch������:��ֹidStr����תΪint����
		
		try {
		id=Integer.parseInt(idStr);
		customerDAO.delete(id);
		} catch (Exception e) {}
		response.sendRedirect("query.do");
	}

	private void query(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		//��ȡģ����ѯ���������.
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		
		//�����������װΪһ��CriteriaCustomer����
		CriteriaCustomer cc = new CriteriaCustomer(name, address, phone);
		
		//System.out.println("query");
		//1.����CustomerDAO��getAll()�õ�Customer�ļ���
		List<Customer> customers = customerDAO.getForListWithCriteriaCustomer(cc);
		
		//2.��Customer�ļ��Ϸ���request��
		request.setAttribute("customers",customers);
		
		//3.ת��ҳ�浽index.jsp(����ʹ���ض���)
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	private void addCustomer(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		//System.out.println("add");
		
		//1.��ȡ������:name,address,phone
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		
		//2.����name�Ƿ��Ѿ���ռ��:
		long count = customerDAO.getCountWithName(name);
		//2.1����CustomerDAO��getCountWithName(String name)��ȡname�����ݿ����Ƿ����
		
		//2.2������ֵ����0,����Ӧaddcustomer.jspҳ��:
		//ͨ��ת���ķ�ʽ����ӦaddCustomer.jsp
		if(count>0) {
		//2.2.1Ҫ����addCustomer.jspҳ����ʾһ��������Ϣ:�û���name�Ѿ���ռ��,������ѡ��!
		//��request�з���һ������message:�û���name�Ѿ���ռ��,������ѡ��!,
		//��ҳ����ͨ��request.getAttribute("message")�ķ�ʽ����ʾ.
		request.setAttribute("message", "�û���"+name+"�Ѿ���ռ��,������ѡ��!");
		//2.2.2addCustomer.jsp�ı����Ի���.
		request.getRequestDispatcher("/addCustomer.jsp").forward(request, response);
		return;
		}
		
		//3.����֤ͨ���� �ѱ�������װΪһ��Customer����customer
		Customer customer = new Customer(name,address,phone);
		//4.����CustomerDAO��save(Customer customer)ִ�б������
		customerDAO.save(customer);
		//5.�ض���success.jspҳ��.ʹ���ض�����Ա�����ֱ����ظ��ύ����.
		
		//System.out.println(request.getParameter("name"));
		//request.getRequestDispatcher("/addCustomer.jsp").forward(request, response);
		response.sendRedirect("success.jsp");
	}

}
