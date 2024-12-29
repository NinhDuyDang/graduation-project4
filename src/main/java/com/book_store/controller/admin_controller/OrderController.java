package com.book_store.controller.admin_controller;

import com.book_store.entity.Notify;
import com.book_store.entity.Order;
import com.book_store.entity.OrderDetail;
import com.book_store.service.NotifyService;
import com.book_store.service.OrderService;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RequestMapping("/admin")
@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private NotifyService notifyService;

    // admin Order
    @GetMapping("/order")
    public String view(Model model,
                       @RequestParam(value = "keyword", defaultValue = "") String keyword,
                       @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                       @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws ParseException {
        return listByPagesOrder(1, keyword, startDate, endDate, model);
    }

    @GetMapping("/order/{pageNumber}")
    public String listByPagesOrder(@PathVariable(name = "pageNumber") int currentPage,
                                   @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                   @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                   @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                   Model model) throws ParseException {
        Page<Order> page = orderService.sortByTime(currentPage, keyword, startDate, endDate);
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();
        List<Order> orderList = page.getContent();
        List<Notify> noti = notifyService.findAll();
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        noti.forEach(n -> {
            if (n.getOrder() != null) {
                n.getOrder().setFormattedPrice(formatter.format(n.getOrder().getPrice()));
            }
        });
        long count = notifyService.findAll().stream()
                .filter(no -> no.getStatus() == 1)
                .count();
        model.addAttribute("notifyCount", count);
        model.addAttribute("notify", notifyService.findAll());
        model.addAttribute("orderList", orderList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);
        String start="";
        String end="";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate!=null){
            start=simpleDateFormat.format(startDate);
        }
        if(endDate!=null){
            end=simpleDateFormat.format(endDate);
        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("query", "?keyword=" + keyword + "&startDate=" + start + "&endDate=" + end);
        return "admin_order";
    }

    //function OrderDetail
    @RequestMapping("/orderDetail/{id}")
    public ModelAndView showOrderDetailPage(@PathVariable(name = "id") int id,
                                            @RequestParam(name = "source", required = false) String source) {
        ModelAndView mav = new ModelAndView("admin_order_edit");
        List<OrderDetail> orderDetailList = orderService.getOrderDetailByID(id);
        Order order = orderService.getById(id);
        List<Notify> noti = notifyService.findAll();
        if (source != null) {
            for (Notify notify : noti){
                if (notify.getOrder().getId() == id){
                    notify.setStatus(0);
                    notifyService.save(notify);
                }
            }
        }
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        noti.forEach(n -> {
            if (n.getOrder() != null) {
                n.getOrder().setFormattedPrice(formatter.format(n.getOrder().getPrice()));
            }
        });
        long count = notifyService.findAll().stream()
                .filter(no -> no.getStatus() == 1)
                .count();
        orderDetailList.forEach(n -> {
            n.setFormattedPrice(formatter.format(n.getPrice()));
        });
        order.setFormattedPrice(formatter.format(order.getPrice()));
        mav.addObject("order", order);
        mav.addObject("orderDetailList", orderDetailList);
        mav.addObject("notifyCount", count);
        mav.addObject("notify", notifyService.findAll());
        return mav;
    }

    @RequestMapping(value = "/saveOrderEdit", method = RequestMethod.POST)
    public RedirectView saveOrderEdit(@ModelAttribute("order") Order order,
                                      RedirectAttributes model) {
        Order orderSave = orderService.getById(order.getId());
        if (orderSave.getPaymentStatus() == 1 && order.getPaymentStatus() == 1 && orderSave.getStatus()==2) {
            model.addFlashAttribute("alert_status", "Đơn hàng này đã được hoàn thành!");
            return new RedirectView("/admin/orderDetail/" + order.getId());
        } else if (orderSave.getPaymentStatus() == 0 && order.getPaymentStatus() == 0 && order.getStatus() == 2) {
            model.addFlashAttribute("alert_status", "Đơn hàng này chưa được thanh toán!");
            return new RedirectView("/admin/orderDetail/" + order.getId());
        }
        orderSave.setStatus(order.getStatus());
        orderSave.setPaymentStatus(order.getPaymentStatus());
        orderService.saveOrder(orderSave);
        model.addFlashAttribute("alert_status", "Trạng thái đã được thay đổi!");
        return new RedirectView("/admin/orderDetail/" + order.getId());
    }
}
