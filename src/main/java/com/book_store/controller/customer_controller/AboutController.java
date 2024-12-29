package com.book_store.controller.customer_controller;

import com.book_store.entity.Cart;
import com.book_store.entity.Product;
import com.book_store.entity.User;
import com.book_store.service.CartService;
import com.book_store.service.ProductService;
import com.book_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/about")
@Controller
public class AboutController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping()
    public String getUserandCartNavBar(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        List<Cart> cartList = new ArrayList<>();
        if(!(authentication == null || authentication instanceof AnonymousAuthenticationToken)) {
            user = userService.findByUsername(authentication.getName());
            cartList = cartService.getCustomerCart(user.getCustomer().getId());
        }
        model.addAttribute("user", user);
        model.addAttribute("listCart", cartList);
        model.addAttribute("categoryList", productService.getCategoryList());
        model.addAttribute("size_carts", cartService.getCartSize());
        model.addAttribute("sortField", "id");
        model.addAttribute("sortDir", "asc");
        return "about";
    }

    @GetMapping({"/support1"})
    public String support1(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        List<Cart> cartList = new ArrayList<>();
        if(!(authentication == null || authentication instanceof AnonymousAuthenticationToken)) {
            user = userService.findByUsername(authentication.getName());
            cartList = cartService.getCustomerCart(user.getCustomer().getId());
        }
        model.addAttribute("user", user);
        model.addAttribute("listCart", cartList);
        model.addAttribute("categoryList", productService.getCategoryList());
        model.addAttribute("size_carts", cartService.getCartSize());
        model.addAttribute("sortField", "id");
        model.addAttribute("sortDir", "asc");
        return "support1";
    }

    @GetMapping({"/support2"})
    public String support2(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        List<Cart> cartList = new ArrayList<>();
        if(!(authentication == null || authentication instanceof AnonymousAuthenticationToken)) {
            user = userService.findByUsername(authentication.getName());
            cartList = cartService.getCustomerCart(user.getCustomer().getId());
        }
        model.addAttribute("user", user);
        model.addAttribute("listCart", cartList);
        model.addAttribute("categoryList", productService.getCategoryList());
        model.addAttribute("size_carts", cartService.getCartSize());
        model.addAttribute("sortField", "id");
        model.addAttribute("sortDir", "asc");
        return "support2";
    }

    @GetMapping({"/support3"})
    public String support3(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        List<Cart> cartList = new ArrayList<>();
        if(!(authentication == null || authentication instanceof AnonymousAuthenticationToken)) {
            user = userService.findByUsername(authentication.getName());
            cartList = cartService.getCustomerCart(user.getCustomer().getId());
        }
        model.addAttribute("user", user);
        model.addAttribute("listCart", cartList);
        model.addAttribute("categoryList", productService.getCategoryList());
        model.addAttribute("size_carts", cartService.getCartSize());
        model.addAttribute("sortField", "id");
        model.addAttribute("sortDir", "asc");
        return "support3";
    }
}
