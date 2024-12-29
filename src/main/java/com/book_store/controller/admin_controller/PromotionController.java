package com.book_store.controller.admin_controller;

import com.book_store.entity.Notify;
import com.book_store.entity.Promotion;
import com.book_store.service.NotifyService;
import com.book_store.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@RequestMapping("/admin")
@Controller
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    NotifyService notifyService;

    @GetMapping("/listPromotions")
    public String viewCourse(Model model,
                             @RequestParam(value = "keyword", defaultValue = "") String keyword,
                             @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                             @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        return listByPages(1, sortField, sortDir, keyword, model);
    }

    @GetMapping("/listPromotions/{pageNumber}")
    public String listByPages(@PathVariable(name = "pageNumber") int currentPage,
                              @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                              @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                              @RequestParam(value = "keyword", defaultValue = "") String keyword,
                              Model model) {
        Page<Promotion> page = promotionService.listAllPromotion(currentPage, sortField, sortDir, keyword);
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();
        List<Promotion> listpromotions = page.getContent();
        model.addAttribute("listpromotions", listpromotions);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        model.addAttribute("query", "?sortField=" + sortField + "&sortDir="
                + sortDir + "&keyword=" + keyword);
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
        return "admin_promotions";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("admin_promotion_edit");
        Promotion promotion = promotionService.getById(id);
        String applyDay = promotion.getApplyDay();
        if (applyDay.equals("AllWeek")) {
            mav.addObject("AllWeek", true);
        }
        if (applyDay.contains("Monday")) {
            mav.addObject("Monday", true);
        }
        if (applyDay.contains("Tuesday")) {
            mav.addObject("Tuesday", true);
        }
        if (applyDay.contains("Wednesday")) {
            mav.addObject("Wednesday", true);
        }
        if (applyDay.contains("Thursday")) {
            mav.addObject("Thursday", true);
        }
        if (applyDay.contains("Friday")) {
            mav.addObject("Friday", true);
        }
        if (applyDay.contains("Saturday")) {
            mav.addObject("Saturday", true);
        }
        if (applyDay.contains("Sunday")) {
            mav.addObject("Sunday", true);
        }
        mav.addObject("promotion", promotion);
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
        mav.addObject("notifyCount", count);
        mav.addObject("notify", notifyService.findAll());
        return mav;
    }

    @PostMapping("/listPromotions/editAndSave")
    public RedirectView saveEdit(@ModelAttribute("PROMOTION") Promotion promotion,
                                 @RequestParam(name = "Monday", required = false) String Monday,
                                 @RequestParam(name = "Tuesday", required = false) String Tuesday,
                                 @RequestParam(name = "Wednesday", required = false) String Wednesday,
                                 @RequestParam(name = "Thursday", required = false) String Thursday,
                                 @RequestParam(name = "Friday", required = false) String Friday,
                                 @RequestParam(name = "Saturday", required = false) String Saturday,
                                 @RequestParam(name = "Sunday", required = false) String Sunday,
                                 @RequestParam(name = "AllWeek", required = false) String AllWeek,
                                 RedirectAttributes model) {
        Promotion promotion2 = promotionService.findByCode(promotion.getCode());
        if (promotion2 != null && promotion2.getId() != promotion.getId()) {
            model.addFlashAttribute("alert", "Mã khuyến mãi đã tồn tại!!!");
            return new RedirectView("/admin/listPromotions/add");
        }
        if (promotion.getDiscount() > 100 || promotion.getDiscount() < 0) {
            model.addFlashAttribute("alert", "Giảm giá phải từ 0 đến 100!!!");
            return new RedirectView("/admin/edit/" + promotion.getId());
        }
        if (promotion.getStartDate().after(promotion.getEndDate())) {
            model.addFlashAttribute("alert", "Ngày bắt đầu phải trước Ngày kết thúc!!!");
            return new RedirectView("/admin/edit/" + promotion.getId());
        }
        String applyDay = "";
        if (Monday != null && Tuesday != null && Wednesday != null && Thursday != null &&
                Friday != null && Saturday != null && Sunday != null || AllWeek != null) {
            promotion.setApplyDay("AllWeek");
            promotionService.save(promotion);
            return new RedirectView("/admin/listPromotions");
        }
        if (Monday != null) {
            applyDay = applyDay + Monday + " ";
        }
        if (Tuesday != null) {
            applyDay = applyDay + Tuesday + " ";
        }
        if (Wednesday != null) {
            applyDay = applyDay + Wednesday + " ";
        }
        if (Thursday != null) {
            applyDay = applyDay + Thursday + " ";
        }
        if (Friday != null) {
            applyDay = applyDay + Friday + " ";
        }
        if (Saturday != null) {
            applyDay = applyDay + Saturday + " ";
        }
        if (Sunday != null) {
            applyDay = applyDay + Sunday + " ";
        }
        promotion.setApplyDay(applyDay);
        promotionService.save(promotion);
        return new RedirectView("/admin/listPromotions");
    }

    @GetMapping("/listPromotions/add")
    public String addPromotion(Model model) {
        Promotion promotion = new Promotion();
        model.addAttribute("PROMOTION", promotion);
        model.addAttribute("addpromotions", "/admin/listPromotions/saveOrUpdate");
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
        return "admin_promotion_add";
    }

    @PostMapping("/listPromotions/saveOrUpdate")
    public RedirectView saveAdd(@ModelAttribute("PROMOTION") Promotion promotion,
                                @RequestParam(name = "Monday", required = false) String Monday,
                                @RequestParam(name = "Tuesday", required = false) String Tuesday,
                                @RequestParam(name = "Wednesday", required = false) String Wednesday,
                                @RequestParam(name = "Thursday", required = false) String Thursday,
                                @RequestParam(name = "Friday", required = false) String Friday,
                                @RequestParam(name = "Saturday", required = false) String Saturday,
                                @RequestParam(name = "Sunday", required = false) String Sunday,
                                @RequestParam(name = "AllWeek", required = false) String AllWeek,
                                RedirectAttributes model) {
        Promotion promotion1 = promotionService.findByName(promotion.getName());
        if (promotion1 != null) {
            model.addFlashAttribute("alert", "Tên khuyến mãi đã tồn tại!!!");
            return new RedirectView("/admin/listPromotions/add");
        }
        Promotion promotion2 = promotionService.findByCode(promotion.getCode());
        if (promotion2 != null) {
            model.addFlashAttribute("alert", "Mã khuyến mãi đã tồn tại!!!");
            return new RedirectView("/admin/listPromotions/add");
        }
        if (promotion.getDiscount() > 100 || promotion.getDiscount() < 0) {
            model.addFlashAttribute("alert", "Giảm giá phải từ 0 đến 100!!!");
            return new RedirectView("/admin/listPromotions/add");
        }
        if (promotion.getStartDate().after(promotion.getEndDate())) {
            model.addFlashAttribute("alert", "Ngày bắt đầu phải trước Ngày kết thúc!!!");
            return new RedirectView("/admin/listPromotions/add");
        }
        String applyDay = "";
        if (Monday != null && Tuesday != null && Wednesday != null && Thursday != null &&
                Friday != null && Saturday != null && Sunday != null || AllWeek != null) {
            promotion.setApplyDay("AllWeek");
            promotionService.save(promotion);
            return new RedirectView("/admin/listPromotions");
        }
        if (Monday != null) {
            applyDay = applyDay + Monday + " ";
        }
        if (Tuesday != null) {
            applyDay = applyDay + Tuesday + " ";
        }
        if (Wednesday != null) {
            applyDay = applyDay + Wednesday + " ";
        }
        if (Thursday != null) {
            applyDay = applyDay + Thursday + " ";
        }
        if (Friday != null) {
            applyDay = applyDay + Friday + " ";
        }
        if (Saturday != null) {
            applyDay = applyDay + Saturday + " ";
        }
        if (Sunday != null) {
            applyDay = applyDay + Sunday + " ";
        }
        promotion.setApplyDay(applyDay);
        promotionService.save(promotion);
        return new RedirectView("/admin/listPromotions");
    }

    @RequestMapping("/delete/{id}")
    public String deletePromotion(@PathVariable(name = "id") int id) {
        Promotion promotion = promotionService.getById(id);
        promotion.setStatus(0);
        promotionService.save(promotion);
        return "redirect:/admin/listPromotions";
    }
}
