package com.book_store.service;

import com.book_store.entity.Category;
import com.book_store.entity.Product;
import com.book_store.entity.ProductImage;
import com.book_store.repository.CategoryRepository;
import com.book_store.repository.ProductImageRepository;
import com.book_store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    public Page<Product> listAll(int currentPage, String sortField,
                                 String sortDirection, String keyword, int categoryId) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(currentPage - 1, 12, sort);
        if (categoryId == -1) {
            return productRepository.searchProductByName(keyword, pageable);
        }
        return productRepository.searchProductByNameAndCategory(keyword, categoryId, pageable);
    }

    public List<Category> getCategoryList() {
        return categoryRepository.findAll();
    }

    // product_detail
    public Product getProduct(int id) {
        return productRepository.getById(id);
    }

    public List<ProductImage> findImageProductById(int id) {
        return productImageRepository.findAllByProductId(id);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public ProductImage findImageById(int id) {
        return productImageRepository.findById(id);
    }

    public void deleteImageProduct(int id) {
        productImageRepository.deleteById(id);
    }

    public void saveImageProduct(ProductImage productImage) {
        productImageRepository.save(productImage);
    }

    public Page<Product> listAllProduct(int currentPage, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(currentPage - 1, 6, sort);
        return productRepository.findByNameContaining(keyword, pageable);
    }

    public List<Product>getAllProducts(){
        return productRepository.findAll();
    }

    public void saveAllProduct(List<Product> productList){
        productRepository.saveAll(productList);
    }

    public List<Product> getProductHot(){
        return  productRepository.getHotProduct();
    }

    public List<Product> getProductHotByCount(){
        return  productRepository.getHotProductByCount();
    }

    public List<Product>getProductbyCategoryId(int categoryId){
        return productRepository.getProductByCategoryId(categoryId);
    }
}
