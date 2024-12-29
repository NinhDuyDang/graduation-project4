package com.book_store.service;

import com.book_store.entity.Cart;
import com.book_store.entity.User;
import com.book_store.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private UserService userService;

    @Autowired
    private CartRepository cartRepository;

    public List<Cart> getCustomerCart(int customerId){
        return cartRepository.findByCustomer_Id(customerId);
    }

    public List<Cart> getCustomerAndStatusCart(int customerId, int status){
        return cartRepository.findByCustomer_IdAndStatus(customerId, status);
    }

    public int getCartSize(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return 0;
        }
        User user = userService.findByUsername(authentication.getName());
        List<Cart> carts = getCustomerCart(user.getCustomer().getId());
        return carts.size();
    }

    public void saveCart(Cart cart){
        cartRepository.save(cart);
    }

    public void deleteUserCart(List<Cart> cartList) {
        for(Cart cart:cartList){
            cartRepository.delete(cart);
        }
    }

    public void deleteCart(Cart cart){
        cartRepository.delete(cart);
    }

    public Cart getCartById(int cartId){
        return cartRepository.getById(cartId);
    }

    public void deleteAllCart(){
        cartRepository.deleteAll();
    }

}
