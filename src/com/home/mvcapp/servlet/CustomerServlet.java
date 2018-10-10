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
		//1.获取ServletPath:/edit.do或addCustomer.do
	    String servletPath =req.getServletPath();
	    //去掉.do得到类似于得到
	    String methodName = servletPath.substring(1);
	    methodName=methodName.substring(0, methodName.length()-3);
	    //System.out.println(methodName);
	    try {
	    	//3.利用反射获取methodName对应的方法
			Method method=getClass().getDeclaredMethod(methodName, HttpServletRequest.class,
					HttpServletResponse.class);
			//4.利用反射调用对应的方法
			method.invoke(this, req,resp);
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void edit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String forwardPath ="/error.jsp";
		//1.获取请求参数id
		String idStr = request.getParameter("id");
		int id = -1;
		//2.调用CustomerDAO的customerDAO.get(id) 获取和id对应的Customer 对象customer
		try {
			Customer customer = customerDAO.get(Integer.parseInt(idStr));
			if(customer != null) {
				forwardPath="/updateCustomer.jsp";
				//3.将customer放入request中
				request.setAttribute("customer", customer);
			}
		} catch (NumberFormatException e) {
			//e.printStackTrace();
		}
		//4.响应updatecustomer.jsp页面:转发.
		request.getRequestDispatcher(forwardPath).forward(request, response);
	}
	
	private void update(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		//System.out.println("update");
		//1.获取表单参数:id, name, address, phone,oldName
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		String oldName = request.getParameter("oldName");
		
		//2.检验name是否已经被占用:
		//2.1比较name和oldName是否相同,若相同说明name可用.
		//2.1若不相同,则调用CustomerDAO的getCountWithName(String name) 获取name在数据库
		//是否存在
		if(!oldName.equalsIgnoreCase(name)) {
			long count = customerDAO.getCountWithName(name);
			
			if(count >0) {
				//2.2.1在updatecustomer.jsp页面显示一个错误消息:用户名name已经被占用,请重新选择!
				//在request中放入一个属性message:用户名name已经被占用,请重新选择!,
				//在页面上通过request.getAttribute("message")的方式显示.
				request.setAttribute("message", "用户名"+name+"已经被占用,请重新选择!");
				//2.2.2 addCustomer.jsp的表单可以回显.
				//address,phone 显示提交表单的新的值,而name显示oldName,而不是新提交的name
				
				//2.2.3结束方法: return 
				request.getRequestDispatcher("/updateCustomer.jsp").forward(request, response);
			    return;
			}
		}
		//3.若验证通过后 把表单参数封装为一个Customer对象customer
		Customer customer = new Customer(name,address,phone);
		customer.setId(Integer.parseInt(id));
		
		//4.调用CustomerDAO的update (Customer customer)执行保存操作
		customerDAO.update(customer);
		
		//5.重定向到query.do
		response.sendRedirect("query.do");
	}
	
	private void delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		//System.out.println("delete");
		String idStr = request.getParameter("id");
		int id=0;
		
		//try ... catch的作用:防止idStr不能转为int类型
		
		try {
		id=Integer.parseInt(idStr);
		customerDAO.delete(id);
		} catch (Exception e) {}
		response.sendRedirect("query.do");
	}

	private void query(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		//获取模糊查询的请求参数.
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		
		//把请求参数封装为一个CriteriaCustomer对象
		CriteriaCustomer cc = new CriteriaCustomer(name, address, phone);
		
		//System.out.println("query");
		//1.调用CustomerDAO的getAll()得到Customer的集合
		List<Customer> customers = customerDAO.getForListWithCriteriaCustomer(cc);
		
		//2.把Customer的集合放入request中
		request.setAttribute("customers",customers);
		
		//3.转发页面到index.jsp(不能使用重定向)
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	private void addCustomer(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		//System.out.println("add");
		
		//1.获取表单参数:name,address,phone
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		
		//2.检验name是否已经被占用:
		long count = customerDAO.getCountWithName(name);
		//2.1调用CustomerDAO的getCountWithName(String name)获取name在数据库中是否存在
		
		//2.2若返回值大于0,则响应addcustomer.jsp页面:
		//通过转发的方式来响应addCustomer.jsp
		if(count>0) {
		//2.2.1要求再addCustomer.jsp页面显示一个错误消息:用户名name已经被占用,请重新选择!
		//在request中放入一个属性message:用户名name已经被占用,请重新选择!,
		//在页面上通过request.getAttribute("message")的方式来显示.
		request.setAttribute("message", "用户名"+name+"已经被占用,请重新选择!");
		//2.2.2addCustomer.jsp的表单可以回显.
		request.getRequestDispatcher("/addCustomer.jsp").forward(request, response);
		return;
		}
		
		//3.若验证通过后 把表单参数封装为一个Customer对象customer
		Customer customer = new Customer(name,address,phone);
		//4.调用CustomerDAO的save(Customer customer)执行保存操作
		customerDAO.save(customer);
		//5.重定向到success.jsp页面.使用重定向可以避免出现表单的重复提交问题.
		
		//System.out.println(request.getParameter("name"));
		//request.getRequestDispatcher("/addCustomer.jsp").forward(request, response);
		response.sendRedirect("success.jsp");
	}

}
