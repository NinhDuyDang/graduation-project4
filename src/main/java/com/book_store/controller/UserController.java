package com.book_store.controller;

import com.book_store.config.Utility;
import com.book_store.dto.UserDto;
import com.book_store.dto.UserInput;
import com.book_store.entity.Cart;
import com.book_store.entity.Customer;
import com.book_store.entity.User;
import com.book_store.jwt.JwtTokenProvider;
import com.book_store.payload.LoginRequest;
import com.book_store.payload.LoginResponse;
import com.book_store.repository.RoleRepository;
import com.book_store.security.MyUserDetail;
import com.book_store.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MailService mailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private StaffService staffService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CartService cartService;

    @GetMapping("/login")
    public String loginPage() {
        //prevent user return back to login page if they already login to the system
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    @RequestMapping("/loginFail")
    public RedirectView loginFail(RedirectAttributes model) {
        model.addFlashAttribute("alert", "Tên người dùng hoặc mật khẩu sai!");
        return new RedirectView("/login");
    }

    @RequestMapping("/accountLocked")
    public RedirectView accountLocked(RedirectAttributes model) {
        model.addFlashAttribute("alert", "Tài khoản của bạn đã bị khóa!");
        return new RedirectView("/login");
    }

    @PostMapping("/login")
    public LoginResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Xác thực thông tin người dùng Request lên
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((MyUserDetail) authentication.getPrincipal());
        return new LoginResponse(jwt);
    }

    @GetMapping("/process_register")
    public String createNewUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = new User();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("user", user);
            return "sign_up";
        }
        return "redirect:/";
    }

    @RequestMapping("/forgot_password")
    public String forgotPassword() {
        //prevent user return back to fotgot password page if they already login to the system
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "forgotPassword";
        }
        return "redirect:/";
    }

    @PostMapping("/check_email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestBody UserInput userInput) {
        try {
            UserDto userDto = userService.getUserByEmail(userInput.getEmail());
            return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
        }
        catch (Exception exception){
            UserDto userDto = new UserDto();
            return new ResponseEntity<UserDto>(userDto, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/send_pass_back")
    public RedirectView sendPassword(RedirectAttributes model, @RequestParam(name = "email") String email) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        try {
            user = customerService.findByEmail(email).getUser();
        } catch (Exception e) {
            model.addFlashAttribute("alert", "Email không tồn tại!!");
            return new RedirectView("/forgot_password");
        }
        String newPass = randomPassword();
        user.setPassword(encoder.encode(newPass));
        userService.saveUser(user);
        mailService.sendSimpleMessage(email, "Reset password", "Đây là mật khẩu mới của bạn: " + newPass + "\n" +
                "Vui lòng đăng nhập và thay đổi mật khẩu!!");
        model.addFlashAttribute("alert", "Mật khẩu đã thay đổi!! Vui lòng kiểm tra email của bạn!!");
        return new RedirectView("/login");
    }

    private String randomPassword() {
        String character = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789"
                + "@$!%*#?&";
        Random random = new Random();
        String generatedString = "";
        for (int i = 0; i < 8; i++) {
            generatedString += character.charAt(random.nextInt(character.length()));
        }
        return generatedString;
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute(name = "user") User user,
                           @ModelAttribute(value = "image") MultipartFile image,
                           HttpServletRequest request, Model model,
                           RedirectAttributes redirectAttributes)
            throws MessagingException, UnsupportedEncodingException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
        if ((!(fileName.toLowerCase().endsWith("jpeg") || fileName.toLowerCase().endsWith("png") || fileName.toLowerCase().endsWith("jpg"))) && !fileName.equals("")) {
            model.addAttribute("alert", "Ảnh sai định dạng! (.jpeg or .png or  .jpg)");
            return "sign_up";
        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setUsername(user.getUsername());
            user.setRole(roleRepository.getById(5));
            Customer customer = user.getCustomer();
            if(!fileName.equals("")){
                customer.setAvatar(fileName);
            }
            if (customerService.findByEmail(user.getCustomer().getEmail()) == null
                    && userService.findByUsername(user.getUsername()) == null) {
                customer.setStatus(1);
                Customer cu =  customerService.saveCustomer(customer);
                user.setCustomer(customer);
                userService.saveUser(user);
                if(!fileName.equals("")){
                    Path uploadPath = Paths.get("image/" + cu.getId());
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    try (InputStream inputStream = image.getInputStream()) {
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new IOException("Could not save Image: " + fileName);
                    }
                }
            } else {
                model.addAttribute("alert", "Tên đăng nhập hoặc Email đã được sử dụng!!!");
                return "sign_up";
            }
            String siteUrl = Utility.getSiteURL(request);
            userService.sendVerificationEmail(user, siteUrl);
            redirectAttributes.addFlashAttribute("alert", "Đăng ký thành công!!!");
            return "redirect:/login";
        }
        return "redirect:/";
    }

    @GetMapping("/changePassword")
    public String changePassword(Model model) {
        //prevent user return back to login page if they already login to the system
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("size_carts", cartService.getCartSize());
        model.addAttribute("sortField", "id");
        model.addAttribute("sortDir", "asc");
        return "changePassword";
    }

    @PostMapping("/saveChangePassword")
    public RedirectView saveChangePassword(HttpServletRequest request, HttpServletResponse response, RedirectAttributes model) {
        //prevent user return back to login page if they already login to the system
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new RedirectView("/login");
        }
        User user = userService.findByUsername(authentication.getName());
        String curPass = request.getParameter("currentpass");
        String newPass = request.getParameter("newpass");
        String reNewPass = request.getParameter("re_newpass");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(curPass, user.getPassword())) {
            model.addFlashAttribute("alert_currentPass", "Wrong password!");
            return new RedirectView("/changePassword");
        } else if (curPass.equals(newPass) || newPass.length() < 6) {
            model.addFlashAttribute("alert_newPass", "New password must different and has more than 6 characters!");
            return new RedirectView("/changePassword");
        } else if (!newPass.equals(reNewPass)) {
            model.addFlashAttribute("alert_reNewPass", "Enter exactly new password again!");
            return new RedirectView("/changePassword");
        }
        user.setPassword(encoder.encode(newPass));
        userService.saveUser(user);
        model.addFlashAttribute("alert", "Change password successfully!");
        mailService.sendSimpleMessage(user.getCustomer().getEmail(), "Reset password", "Your password has been changed successfully!!");
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return new RedirectView("/login");
    }

    //function edit customer by id
    @RequestMapping("/customeruser/edit/")
    public String showEditCustomerUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        return "edit_Customer_Profile";
    }

    //function save customer by id
    @PostMapping("/customeruser/save")
    public RedirectView saveCustomerUser(@ModelAttribute("user") User user,
                                         @ModelAttribute(value = "image") MultipartFile image,
                                         RedirectAttributes model) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new RedirectView("/login");
        }
        Customer customerSave = new Customer();
        customerSave.setId(user.getCustomer().getId());
        customerSave.setEmail(user.getCustomer().getEmail());
        customerSave.setStatus(user.getCustomer().getStatus());
        customerSave.setPoint(user.getCustomer().getPoint());
        customerSave.setName(user.getCustomer().getName());
        customerSave.setPhone(user.getCustomer().getPhone());
        customerSave.setAddress(user.getCustomer().getAddress());
        customerSave.setCreatedAt(user.getCustomer().getCreatedAt());
        customerSave.setBirthday(user.getCustomer().getBirthday());
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
        if(!fileName.equals("")){
            customerSave.setAvatar(fileName);
        }
        Customer cu = customerService.saveCustomer(customerSave);
        if(!fileName.equals("")){
            Path uploadPath = Paths.get("image/" + cu.getId());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Could not save Image: " + fileName);
            }
        }
        User userSave = new User();
        userSave.setId(user.getId());
        userSave.setUsername(user.getUsername());
        userSave.setPassword(user.getPassword());
        userSave.setRole(user.getRole());
        userSave.setCreatedAt(user.getCreatedAt());
        userSave.setCustomer(customerSave);
        if (user.getStaff().getId() != null) {
            userSave.setStaff(staffService.get(user.getStaff().getId()));
        }
        userService.saveUser(userSave);
        int a =1;
        return new RedirectView("/product/listproducts");
    }

    @RequestMapping("/404")
    public String notFoundPage() {
        return "404";
    }
}
