package com.book_store.controller.admin_controller;

import com.book_store.entity.Customer;
import com.book_store.entity.Notify;
import com.book_store.entity.Staff;
import com.book_store.entity.User;
import com.book_store.repository.RoleRepository;
import com.book_store.repository.StaffRepository;
import com.book_store.repository.UserRepository;
import com.book_store.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@RequestMapping("/admin")
@Controller
public class StaffController {
    @Autowired
    private StaffService staffService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    NotifyService notifyService;

    @GetMapping("/staff")
    public String viewStaff(Model model,
                            @RequestParam(value = "keyword", defaultValue = "") String keyword,
                            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        return listByPagesStaff(1, sortField, sortDir, keyword, model);
    }

    @GetMapping("/staff/{pageNumber}")
    public String listByPagesStaff(@PathVariable(name = "pageNumber") int currentPage,
                                   @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                   @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                                   @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                   Model model) {
        Page<Staff> page = staffService.listAllStaff(currentPage, sortField, sortDir, keyword);
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();
        List<Staff> StaffList = page.getContent();
        model.addAttribute("StaffList", StaffList);
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
        return "admin_staff";
    }

    // function create new Staff
    @RequestMapping("/newStaff")
    public String showNewStaffForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
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
        return "admin_staff_add";
    }

    // function saveStaff
    @RequestMapping(value = "/saveStaff", method = RequestMethod.POST)
    public RedirectView saveStaffCustomer(@Valid User user, RedirectAttributes model) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setUsername(user.getUsername());
        user.setRole(roleRepository.getById(user.getRole().getId()));
        Customer customer = user.getCustomer();
        customer.setName(user.getStaff().getName());
        customer.setEmail(user.getStaff().getEmail());
        customer.setPhone(user.getStaff().getPhone());
        customer.setEmail(user.getCustomer().getEmail());
        customer.setBirthday(user.getCustomer().getBirthday());
        customer.setAvatar(user.getStaff().getAvatar());
        customer.setPoint(0);
        customer.setStatus(1);
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addFlashAttribute("alert_Username", "Username is exited!");
            return new RedirectView("/admin/newStaff");
        } else if (staffRepository.findByEmail(user.getStaff().getEmail()) != null) {
            model.addFlashAttribute("alert_Email", "Email is exited!");
            return new RedirectView("/admin/newStaff");
        }
        customerService.saveCustomer(customer);
        Staff staff = user.getStaff();
        staffService.saveStaff(staff);
        user.setStaff(staff);
        userService.saveUser(user);
        model.addAttribute("user", user);
        return new RedirectView("/admin/staff");
    }

    //Function saveStaffEdit
    @RequestMapping(value = "/saveStaffEdit", method = RequestMethod.POST)
    public RedirectView saveStaffEdit(@ModelAttribute("user") User user, RedirectAttributes model) {
        //user
        User userSave = userService.getById(user.getId());
        userSave.setUsername(user.getUsername());
        userSave.setRole(roleRepository.getById(user.getRole().getId()));
        Customer customerSave = customerService.getById(user.getCustomer().getId());
        //Staff
        Staff staffSave = staffService.getById(user.getStaff().getId());
        staffSave.setAvatar(user.getStaff().getAvatar());
        staffSave.setEmail(user.getStaff().getEmail());
        staffSave.setPhone(user.getStaff().getPhone());
        staffSave.setName(user.getStaff().getName());
        staffSave.setUser(userSave);

        customerService.saveCustomer(customerSave);
        staffService.saveStaff(staffSave);
        userService.saveUser(userSave);
        return new RedirectView("/admin/staff");
    }

    // function edit staff
    @RequestMapping("/editStaff/{id}")
    public ModelAndView showEditStaffPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("admin_staff_edit");
        User user = userService.getById(id);
        mav.addObject("user", user);
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

    // function delete staff
    @RequestMapping("/deleteStaff/{id}")
    public String deleteStaff(@PathVariable(name = "id") int id) {
        User user = userService.getById(id);
        int staffId = user.getStaff().getId();
        int customerId = user.getCustomer().getId();
        user.setStaff(null);
        user.setCustomer(null);
        userService.deleteUser(user.getId());
        customerService.deleteCustomer(customerId);
        staffService.delete(staffId);
        return "redirect:/admin/staff";
    }
}
