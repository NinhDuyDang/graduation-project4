package com.book_store.controller.admin_controller;

import com.book_store.entity.Notify;
import com.book_store.entity.Order;
import com.book_store.entity.OrderDetail;
import com.book_store.service.CustomerService;
import com.book_store.service.NotifyService;
import com.book_store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping("/admin")
@Controller
public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private NotifyService notifyService;

    @GetMapping("/home")
    public String AdminHomePage(Model model) {

        List<Order> allorders = orderService.getAllOrders();
        double price = 0.0;
        int count = 0;
        for (Order order : allorders) {
            if (order.getStatus() == 2) {
                price += Double.parseDouble(order.getPrice().toString());
                Set<OrderDetail> orderDetailSet = order.getOrderDetails();
                for (OrderDetail orderDetail : orderDetailSet) {
                    count ++;
                }
            }
        }
        List<Notify> noti = notifyService.findAll();
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        noti.forEach(n -> {
            if (n.getOrder() != null) {
                n.getOrder().setFormattedPrice(formatter.format(n.getOrder().getPrice()));
            }
        });
        long countNoti = notifyService.findAll().stream()
                .filter(no -> no.getStatus() == 1)
                .count();
        model.addAttribute("notifyCount", countNoti);
        model.addAttribute("notify", notifyService.findAll());
        model.addAttribute("numberofcustomers", customerService.getAllCustomer().size());
        model.addAttribute("sales", count);
        model.addAttribute("income", price);
        model.addAttribute("listorder", orderService.getLastestOrders());
        return ("admin_dashbord");
    }

    @RequestMapping("/revenue")
    public String getPieChart(@RequestParam(name = "Year", defaultValue = "2024") int Year, Model model) {
        Map<String, Integer> graphData = new TreeMap<>();
        model.addAttribute("Year", Year);
        graphData.put("12", orderService.getDataRevenueByMonthOfYear(12, Year));
        graphData.put("11", orderService.getDataRevenueByMonthOfYear(11, Year));
        graphData.put("10", orderService.getDataRevenueByMonthOfYear(10, Year));
        graphData.put("9", orderService.getDataRevenueByMonthOfYear(9, Year));
        graphData.put("8", orderService.getDataRevenueByMonthOfYear(8, Year));
        graphData.put("7", orderService.getDataRevenueByMonthOfYear(7, Year));
        graphData.put("6", orderService.getDataRevenueByMonthOfYear(6, Year));
        graphData.put("5", orderService.getDataRevenueByMonthOfYear(5, Year));
        graphData.put("4", orderService.getDataRevenueByMonthOfYear(4, Year));
        graphData.put("3", orderService.getDataRevenueByMonthOfYear(3, Year));
        graphData.put("2", orderService.getDataRevenueByMonthOfYear(2, Year));
        graphData.put("1", orderService.getDataRevenueByMonthOfYear(1, Year));

        model.addAttribute("chartData", graphData);
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
        return "admin_revenue";
    }

    @RequestMapping("/revenue/order/{month}/{year}")
    public String viewOrder(Model model,
                            @PathVariable(name = "month") int month,
                            @PathVariable(name = "year") int year) {
        return orderListByMonthAndYear(1, month, year, model);
    }

    @GetMapping("/revenue/order/{pageNumber}")
    public String orderListByMonthAndYear(@PathVariable(name = "pageNumber") int currentPage,
                                          @RequestParam(name = "month") int month,
                                          @RequestParam(name = "year") int year,
                                          Model model) {
        Page<Order> page = orderService.listOrderByMonthAndYear(currentPage, month, year);
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();
        List<Order> orderList = page.getContent();
        model.addAttribute("query", "?month=" + month + "&year=" + year);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("orderList", orderList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
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
        return "admin_listOrderRevenue";
    }

}
