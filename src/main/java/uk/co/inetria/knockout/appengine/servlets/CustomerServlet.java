/**
 * The contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2014, dawelbeit.info
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.inetria.knockout.appengine.servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import uk.co.inetria.knockout.appengine.Constants;
import uk.co.inetria.knockout.appengine.entities.Customer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author Omer Dawelbeit
 *
 */
public class CustomerServlet extends HttpServlet {

	private static final long serialVersionUID = 338706822397771715L;
	
	private static final Logger log = Logger.getLogger(CustomerServlet.class.getName());
	
	private Type type = new TypeToken<List<Customer>>(){}.getType();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		List<Customer> customers = Customer.findAll();
		log.info("Found " + customers.size() + " customers.");
		
		Gson gson = new Gson();
		
		resp.setContentType(Constants.CONTENT_TYPE_JSON);
		resp.getWriter().print(gson.toJson(customers));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String customersJson = req.getParameter("customers");
		Gson gson = new Gson();
		Map<String, Object> status = new HashMap<>();
		status.put("success", false);
		
		try {
			if(StringUtils.isNotBlank(customersJson)) {
				log.info("Saving customers");
				List<Customer> customers = gson.fromJson(customersJson, type);
				
				if(customers != null) {
					
					if(customers.isEmpty()) {
						// delete everything
						Customer.removeAll(Customer.findAll());
						
					} else {
						// check for deleted customers
						Set<Long> ids = new HashSet<>();
						for(Customer customer: customers) {
							if(customer.getId() != null) {
								ids.add(customer.getId());
							}
						}
						
						List<Customer> toDelete = new ArrayList<>();
						List<Customer> allCustomers = Customer.findAll();
						for(Customer customer: allCustomers) {
							if(!ids.contains(customer.getId())) {
								toDelete.add(customer);
							}
						}
						
						Customer.removeAll(toDelete);
						Customer.saveAll(customers);
					}
					
					status.put("success", true);
				}
			}
			
		} catch(Exception e) {
			log.log(Level.SEVERE, "Failed to save customers", e);
		}
		
		resp.setContentType(Constants.CONTENT_TYPE_JSON);
		resp.getWriter().print(gson.toJson(status));
	}
	
	

}
