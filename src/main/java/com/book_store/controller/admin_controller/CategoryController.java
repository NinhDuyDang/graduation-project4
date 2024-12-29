package com.book_store.controller.admin_controller;

import com.book_store.entity.Category;
import com.book_store.entity.Notify;
import com.book_store.service.CategoriesService;
import com.book_store.service.NotifyService;
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
public class CategoryController {
    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    NotifyService notifyService;


    @GetMapping("/category/listCategory")
    public String viewCategory(Model model,
                               @RequestParam(value = "keyword", defaultValue = "") String keyword,
                               @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                               @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        return listByPagesCategory(1, sortField, sortDir, keyword, model);
    }

    @GetMapping("/category/listCategory/{pageNumber}")
    public String listByPagesCategory(@PathVariable(name = "pageNumber") int currentPage,
                                      @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                      @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                                      @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                      Model model) {
        Page<Category> page = categoriesService.listAllCategory(currentPage, sortField, sortDir, keyword);
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();
        List<Category> listcategory = page.getContent();
        model.addAttribute("listcategory", listcategory);
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
        return "admin_categories";
    }

    @RequestMapping("/category/edit/{id}")
    public ModelAndView showEditCategory(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("admin_categories_edit");
        Category category = categoriesService.getById(id);
        mav.addObject("category", category);
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

    @PostMapping("/category/listCategory/editAndSave")
    public RedirectView saveEditCategory(@ModelAttribute("category") Category category,
                                         RedirectAttributes model) {

        categoriesService.save(category);
        return new RedirectView("/admin/category/listCategory");
    }

    @GetMapping("/category/listCategory/add")
    public String addCategory(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);
        model.addAttribute("addcategory", "/admin/category/listCategory/save");
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
        return "admin_categories_add";
    }

    @PostMapping("/category/listCategory/save")
    public RedirectView saveAddCategory(@ModelAttribute("category") Category category,
                                        RedirectAttributes model) {
        Category category1 = categoriesService.findByName(category.getName());
        if (category1 != null) {
            model.addFlashAttribute("alert", "Tên danh mục đã tồn tại!!!");
            return new RedirectView("/admin/category/listCategory/add");
        }

        categoriesService.save(category);
        return new RedirectView("/admin/category/listCategory");
    }

    @RequestMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") int id) {
        categoriesService.delete(id);
        return "redirect:/admin/category/listCategory";
    }
}
