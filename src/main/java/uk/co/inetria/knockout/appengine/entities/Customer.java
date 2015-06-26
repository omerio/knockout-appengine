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
package uk.co.inetria.knockout.appengine.entities;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.KeyFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

/**
 * Customer entity that is serialized into the following JSON
 * {
 *	    "name": "John Smith",
 *	    "emailAddress": "john.smith@example.com",
 *	    "age": 30,
 *	    "bio": "Lorem ipsum dolor sit amet, posse perpetua cum ut, duo te adhuc integre complectitur. Mea eu senserit molestiae. In vis vidisse dissentias. Harum petentium constituto no his, ei cum eleifend delicatissimi, indoctum aliquando cu quo. Everti placerat senserit per ea, in vide errem vel.",
 *	    "services": [{
 *	        "name": "Website Templates",
 *	        "credit": "10"
 *	    }, {
 *	        "name": "Stock Images",
 *	        "credit": "5"
 *	    }, {
 *	        "name": "Sound Tracks",
 *	        "credit": "1"
 *	    }]
 *	}
 * @author omerio
 *
 */
@Entity
@Data
public class Customer {

	@Id
	private Long id;

	private String name;

	@Index
	private String emailAddress;
	
	private Integer age;
	
	private String bio;
	
	private List<Service> services = new ArrayList<Service>();

	/**
	 * On save Escape any HTML and Javascript
	 */
	@OnSave
	void onSave() {
		
		this.bio = escape(this.bio);
		this.name = escape(this.name);
		this.emailAddress = escape(this.emailAddress);
		
		if(this.services != null) {
			for(Service service: this.services) {
				service.setName(escape(service.getName()));
			}
		}
	}
	
	/**
	 * Escape any HTML and Javascript
	 * @param text
	 * @return
	 */
	public static String escape(String text) {
		if(StringUtils.isNotBlank(text)) {
			text = StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeEcmaScript(text));
		}
		return text;
	}

	//------- CRUD

	public static List<Customer> findAll()	{
		return ofy().load().type(Customer.class).list();
	}

	public static List<Customer> findAll(String emailAddress)	{
		return ofy().load().type(Customer.class).filter("emailAddress", emailAddress).list();
	}

	public static Customer findByKey(String key) {
		return findByKey(KeyFactory.stringToKey(key));
	}

	public static Customer findByKey(com.google.appengine.api.datastore.Key key) {
		return (Customer) ofy().load().value(key).now();
	}

	public static Customer findById(Long id) {
		return ofy().load().type(Customer.class).id(id).now();
	}

	public Key<Customer> save()	{
		return ofy().save().entity(this).now();
	}

	public static Map<Key<Customer>, Customer> saveAll(List<Customer> customers)	{
		return ofy().save().entities(customers).now();
	}

	public void remove()	{
		ofy().delete().entity(this).now();
	}

	public static void removeAll(List<Customer> customers) {
		ofy().delete().entities(customers).now();
	}

}
