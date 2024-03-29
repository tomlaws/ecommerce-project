package ast20201.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ast20201.project.model.Category;
import ast20201.project.model.PageData;
import ast20201.project.model.Product;
import ast20201.project.model.ProductFilter;
import ast20201.project.service.CategoryService;
import ast20201.project.service.ProductService;

@RequestMapping(value = "/api/categories")
@Controller
public class CategoryController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

    @RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getCategories() {
		List<Category> categories = categoryService.getCategories();
		return ResponseEntity.ok(categories);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getCategory(@PathVariable("id") long id) {
		Category category = categoryService.getCategory(id);
		return ResponseEntity.ok(category);
	}

	@RequestMapping(value = "/{id}/products", method = RequestMethod.GET)
	public ResponseEntity<?> getProducts(@PathVariable("id") long id) {
		ProductFilter filter = new ProductFilter();
		filter.setCategory(id);
		PageData<Product> products = productService.getProducts(1, filter, null);
		return ResponseEntity.ok(products);
	}

	@RequestMapping(value = "/{id}/products/{page}", method = RequestMethod.GET)
	public ResponseEntity<?> getProducts(@PathVariable("id") long id, @PathVariable("page") int page) {
		ProductFilter filter = new ProductFilter();
		filter.setCategory(id);
		PageData<Product> products = productService.getProducts(page, filter, null);
		return ResponseEntity.ok(products);
	}
}