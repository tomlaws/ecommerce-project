package ast20201.project.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletContext;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ast20201.project.model.User;
import ast20201.project.model.UserWithProfile;
import ast20201.project.model.Category;
import ast20201.project.model.FieldErrorResponse;
import ast20201.project.model.Order;
import ast20201.project.model.PageData;
import ast20201.project.model.PageableList;
import ast20201.project.model.Product;
import ast20201.project.model.ProductFilter;
import ast20201.project.model.SiteConfig;
import ast20201.project.service.CategoryService;
import ast20201.project.service.OrderService;
import ast20201.project.service.ProductService;
import ast20201.project.service.ProfileService;
import ast20201.project.service.SettingService;
import ast20201.project.service.UserService;
import ast20201.project.validation.ValidationGroup;

@RequestMapping(value = "/admin/api")
@Controller
public class AdminController {
	@Autowired
	ServletContext servletContext;
	@Autowired
	UserService userService;
	@Autowired
	CategoryService categoryService;
	@Autowired
	ProductService productService;
	@Autowired
	SettingService settingService;
	@Autowired
	OrderService orderService;
	@Autowired
	ProfileService profileService;

	final int maxItemsPerPage = 10;

	@RequestMapping(value = "/users/all-users", method = RequestMethod.GET)
	public ResponseEntity<?> allUsers(@RequestParam(value = "page", defaultValue = "1") int page, ModelMap model) {
		int totalUserCount = userService.getTotalUserCount();
		List<User> users = userService.getUsersByPage(page, maxItemsPerPage);

		return ResponseEntity.ok(new PageData(users, page, totalUserCount));
	}

	@RequestMapping(value = "/users/all-users/count", method = RequestMethod.GET)
	public ResponseEntity<?> count() {
		int totalUserCount = userService.getTotalUserCount();
		return ResponseEntity.ok(totalUserCount);
	}

	@RequestMapping(value = "/users/all-users/search", method = RequestMethod.GET)
	public ResponseEntity<?> usersListSearch(@RequestParam(value = "username", defaultValue = "") String username,
			@RequestParam(value = "page", defaultValue = "1") int page, ModelMap model) {
		List<User> users = userService.searchUsersByUsername(username);
		return ResponseEntity.ok(new PageableList(users, page));
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@RequestParam(value = "id") long id, ModelMap model) {
		UserWithProfile user = profileService.getProflie(id);
		return ResponseEntity.ok(user);
	}

	@RequestMapping(value = "/users", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(
			@Validated({ ValidationGroup.EditUser.class }) @RequestBody UserWithProfile user) {

		FieldErrorResponse errors = new FieldErrorResponse();

		User u1 = userService.getUserByUsernameOrEmail(user.getUsername());
		if (u1 != null && u1.getId() != user.getId()) {
			errors.addErrors("username", "Username has already been taken");
		}
		User u2 = userService.getUserByUsernameOrEmail(user.getEmail());
		if (u2 != null && u2.getId() != u2.getId()) {
			errors.addErrors("email", "Email address has already been taken");
		}

		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}

		User dbuser = userService.getUser(user.getId());
		dbuser.setUsername(user.getUsername());
		if (null != user.getPassword() && user.getPassword().length() > 0) {
			String plainPassword = user.getPassword();
			String hashedPassword = DigestUtils.md5DigestAsHex(plainPassword.getBytes());
			dbuser.setPassword(hashedPassword);
		}
		dbuser.setEmail(user.getEmail());
		dbuser.setRole(user.getRole());
		dbuser.setCredits(user.getCredits());
		userService.updateUser(dbuser);

		profileService.updateProfile(user.getId(), user);
		UserWithProfile dbuserWithProfile = profileService.getProflie(user.getId());
		return ResponseEntity.ok(dbuserWithProfile);
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public ResponseEntity<?> addUser(@Validated({ ValidationGroup.AddUser.class }) @RequestBody UserWithProfile user) {
		FieldErrorResponse errors = new FieldErrorResponse();
		if (userService.checkUsernameDuplicated(user.getUsername())) {
			errors.addErrors("username", "Username has already been taken");
		}
		if (userService.checkEmailDuplicated(user.getEmail())) {
			errors.addErrors("email", "Email address has already been taken");
		}
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		String plainPassword = user.getPassword();
		String hashedPassword = DigestUtils.md5DigestAsHex(plainPassword.getBytes());
		user.setPassword(hashedPassword);
		userService.addUser(user);
		User dbuser = userService.getUserByUsernameOrEmail(user.getUsername());
		profileService.updateProfile(dbuser.getId(), user);
		return ResponseEntity.ok(dbuser);
	}

	@RequestMapping(value = "/users", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@RequestParam(value = "id") long id) {
		FieldErrorResponse errors = new FieldErrorResponse();
		if (null == userService.getUser(id)) {
			errors.addErrors("id", "User not found");
		}
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		userService.deleteUser(id);
		return ResponseEntity.ok("{}");
	}

	@RequestMapping(value = "/products/categories", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCategories() {
		List<Category> categories = categoryService.getCategories();
		return ResponseEntity.ok(categories);
	}

	@RequestMapping(value = "/products/categories", method = RequestMethod.PUT)
	public ResponseEntity<?> updateCategoryList(@RequestBody List<Category> categoryList) {
		categoryService.updateCategories(categoryList);
		List<Category> categories = categoryService.getCategories();
		return ResponseEntity.ok(categories);
	}

	@RequestMapping(value = "/products/products", method = RequestMethod.GET)
	public ResponseEntity<?> getProducts(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "category", required = false) Long category,
			@RequestParam(value = "priceLowerBound", required = false) BigDecimal priceLowerBound,
			@RequestParam(value = "priceUpperBound", required = false) BigDecimal priceUpperBound,
			@RequestParam(value = "sortBy", required = false) String sortBy) {
		if (null == page)
			page = 1;
		ProductFilter filter = new ProductFilter(name, category, priceLowerBound, priceUpperBound);
		PageData<Product> products = productService.getProducts(page, filter, sortBy);
		return ResponseEntity.ok(products);
	}

	@RequestMapping(value = "/products/products/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getProduct(@PathVariable long id) {
		Product product = productService.getProduct(id);
		return ResponseEntity.ok(product);
	}

	@RequestMapping(value = "/products/products/{id}", method = RequestMethod.PUT, consumes = { "multipart/form-data" })
	public ResponseEntity<?> updateProduct(@PathVariable("id") long id,
			@RequestPart(value = "productImage", required = false) MultipartFile file,
			@RequestPart("product") @Valid Product product) {
		productService.updateProduct(id, product.getName(), product.getPrice(), product.getQuantity(),
				product.getDescription(), product.getCategories());

		if (null != file) {
			try {
				byte[] bytes = file.getBytes();
				Blob blob = new SerialBlob(bytes);
				productService.updateProductImage(id, blob);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SerialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Product p = productService.getProduct(id);
		return ResponseEntity.ok(p);
	}

	@RequestMapping(value = "/products/products/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProduct(@PathVariable("id") long id) {
		productService.deleteProduct(id);
		return ResponseEntity.ok("{}");
	}

	/*
	 * https://stackoverflow.com/questions/21329426/spring-mvc-multipart-request-
	 * with-json
	 * https://stackoverflow.com/questions/2786426/how-can-i-get-real-path-for-file-
	 * in-my-webcontent-folder
	 */
	@RequestMapping(value = "/products/products", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public ResponseEntity<?> addProduct(@RequestPart(value = "productImage", required = false) MultipartFile file,
			@RequestPart("product") @Valid Product product) {
		// Add product
		long pk = productService.addProduct(product.getName(), product.getPrice(), product.getQuantity(),
				product.getDescription(), product.getCategories());
		if (null != file) {
			try {
				byte[] bytes = file.getBytes();
				Blob blob = new SerialBlob(bytes);
				productService.updateProductImage(pk, blob);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SerialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok("{}");
	}

	@RequestMapping(value = "/settings/site-config", method = RequestMethod.GET)
	public ResponseEntity<?> getSiteConfig() {
		SiteConfig configs = settingService.getSiteConfig();
		return ResponseEntity.ok(configs);
	}

	@RequestMapping(value = "/settings/site-config", method = RequestMethod.PUT)
	public ResponseEntity<?> updateSiteConfigs(@RequestBody SiteConfig config) {
		settingService.updateSiteConfig(config);
		return ResponseEntity.ok("{}");
	}

	@RequestMapping(value = "/orders", method = RequestMethod.GET)
	public ResponseEntity<?> getOrders(@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "page", required = false) Integer page) {
		if (page == null)
			page = 1;
		if (query == null)
			query = "";
		PageData<Order> orders = orderService.getOrders(query, page);
		return ResponseEntity.ok(orders);
	}

	@RequestMapping(value = "/orders/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOrder(@PathVariable("id") long id) {
		Order order = orderService.getOrder(id);
		return ResponseEntity.ok(order);
	}

	@RequestMapping(value = "/orders/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateOrder(@PathVariable("id") long id, @RequestBody Order order) {
		Order dborder = orderService.updateOrder(id, order);
		return ResponseEntity.ok(dborder);
	}

	@RequestMapping(value = "/figures/{range}", method = RequestMethod.GET)
	public ResponseEntity<?> getFigures(@PathVariable("range") String range) throws JsonProcessingException {
		long now = System.currentTimeMillis();
		Date start = new Date(now);
		Date end = new Date(now);
		Calendar cal = Calendar.getInstance();
		switch (range) {
		case "today":
			cal.setTime(end);
			cal.add(Calendar.DATE, 1);
			end = new Date(cal.getTimeInMillis());
			break;
		case "yesterday":
			cal.setTime(start);
			cal.add(Calendar.DATE, -1);
			start = new Date(cal.getTimeInMillis());
			break;
		case "month":
			cal.setTime(start);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			start = new Date(cal.getTimeInMillis());
			cal.add(Calendar.MONTH, 1);
			end = new Date(cal.getTimeInMillis());
			break;
		case "alltime":
			cal.setTime(start);
			cal.add(Calendar.YEAR, -999);
			start = new Date(cal.getTimeInMillis());
			cal.setTime(end);
			cal.add(Calendar.YEAR, 999);
			end = new Date(cal.getTimeInMillis());
			break;
		}
		int numberOfUsers = userService.getNumberOfUsers(start, end);
		BigDecimal sales = orderService.getSales(start, end);
		int numberOfOrders = orderService.getNumberOfOrders(start, end);
		long topSellingProduct = orderService.getTopSellingProduct(start, end);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode();
		((ObjectNode) rootNode).put("numberOfUsers", String.valueOf(numberOfUsers));
		((ObjectNode) rootNode).put("sales", String.valueOf(sales));
		((ObjectNode) rootNode).put("numberOfOrders", String.valueOf(numberOfOrders));
		((ObjectNode) rootNode).put("topSellingProduct", String.valueOf(topSellingProduct));
		return ResponseEntity.ok(mapper.writeValueAsString(rootNode));
	}
}
