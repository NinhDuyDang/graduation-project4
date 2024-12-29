package com.book_store.controller.customer_controller;

import com.book_store.entity.*;
import com.book_store.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class HomeController {
    @Autowired
    ProductService productService;

    @Autowired
    CategoriesService categoriesService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    UserService userService;

    @Autowired
    CartService cartService;

    @RequestMapping()
    public String Homepage(Model model){
        List<Order> orderList = orderService.getAllOrders();
        List<Product> products = productService.getAllProducts();
        for (Product product: products ) {
            product.setCount(0);
            productService.saveProduct(product);
        }
        for (Order order: orderList) {
            if (order.getStatus() == 2){
                Set<OrderDetail> orderDetailSet = order.getOrderDetails();
                for (OrderDetail orderDetail: orderDetailSet) {
                    orderDetail.getProduct().setCount(orderDetail.getProduct().getCount() + orderDetail.getQuantity());
                    productService.saveProduct(orderDetail.getProduct());
                }
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Cart> cartList = new ArrayList<>();
        User user = null;
        if(!(authentication == null || authentication instanceof AnonymousAuthenticationToken)) {
            user = userService.findByUsername(authentication.getName());
            cartList = cartService.getCustomerCart(user.getCustomer().getId());
        }
        model.addAttribute("user", user);
        model.addAttribute("listCart", cartList);
        List<Product> productList = productService.getProductHotByCount();
        model.addAttribute("listCategories", productService.getCategoryList());
        model.addAttribute("categoryList", productService.getCategoryList());
        model.addAttribute("keyword","");
        model.addAttribute("listproduct",productList);
        model.addAttribute("listProductsbyCategory1",productService.getProductbyCategoryId(productService.getCategoryList().get(0).getId()));
        model.addAttribute("listProductsbyCategory2",productService.getProductbyCategoryId(productService.getCategoryList().get(1).getId()));
        model.addAttribute("listProductsbyCategory3",productService.getProductbyCategoryId(productService.getCategoryList().get(2).getId()));
        model.addAttribute("size_carts", cartService.getCartSize());
        model.addAttribute("sortField", "id");
        model.addAttribute("sortDir", "asc");
        return "index";
    }
}
