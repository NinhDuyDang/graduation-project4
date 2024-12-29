package com.book_store.controller.admin_controller;

import com.book_store.entity.Feedback;
import com.book_store.entity.Notify;
import com.book_store.entity.Product;
import com.book_store.entity.ProductImage;
import com.book_store.service.*;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RequestMapping("/admin")
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    NotifyService notifyService;

    //function show list product
    @RequestMapping("/product")
    public String listProductAdmin(Model model,
                                   @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                   @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                   @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        return listProductAdminByPage(1, sortField, sortDir, keyword, model);
    }

    //function show list product by page
    @RequestMapping("/product/{pageNumber}")
    public String listProductAdminByPage(@PathVariable(name = "pageNumber") int currentPage,
                                         @RequestParam("sortField") String sortField,
                                         @RequestParam("sortDir") String sortDir,
                                         @RequestParam("keyword") String keyword,
                                         Model model) {
        Page<Product> page = productService.listAllProduct(currentPage, sortField, sortDir, keyword);
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();
        List<Product> listproduct = page.getContent();
        List<Product> listproductRegister = new ArrayList<>();
        model.addAttribute("listproductRegister", listproductRegister);
        model.addAttribute("listproduct", listproduct);
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
        return "datatables";
    }

    //function edit product by id
    @RequestMapping("/product/edit/{id}")
    public ModelAndView showEditProductPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("admin_product_edit");
        mav.addObject("listCategory", productService.getCategoryList());
        Product product = productService.getProduct(id);
        mav.addObject("product", product);
        ProductImage productImage = new ProductImage();
        mav.addObject("productImage", productImage);
        List<ProductImage> productImageList = productService.findImageProductById(id);
        mav.addObject("productImageList", productImageList);
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

    //function save product
    @RequestMapping(value = "/product/save", method = RequestMethod.POST)
    public String saveProduct(@ModelAttribute("product") Product product) {
        if(product.getStatus()==null){
            product.setStatus(1);
        }
        if(product.getCount()==null || product.getCount()<0){
            product.setCount(0);
        }
        productService.saveProduct(product);
        return "redirect:/admin/product";
    }

    //function save image product
    @RequestMapping(value = "/product/saveImage/{id}", method = RequestMethod.POST)
    public String saveImageProduct(@ModelAttribute("productImage") ProductImage productImage,
                                   @PathVariable(name = "id") int productId) {
        productImage.setProduct(productService.getProduct(productId));
        productImage.setId(null);
        productService.saveImageProduct(productImage);
        return "redirect:/admin/product/edit/" + productId;
    }

    //function delete image product by id
    @RequestMapping("/product/deleteImage/{id}")
    public String deleteImageProduct(@PathVariable(name = "id") int id) {
        int productId = productService.findImageById(id).getProduct().getId();
        productService.deleteImageProduct(id);
        return "redirect:/admin/product/edit/" + productId;
    }

    //function delete product by id
    @RequestMapping("/product/delete/{id}")
    public String delete(@PathVariable(name = "id") int id) {
        Product product = productService.getProduct(id);
        product.setStatus(0);
        product.setCreatedAt(product.getCreatedAt());
        productService.saveProduct(product);
        return "redirect:/admin/product";
    }

    //function create new product
    @RequestMapping("/product/new")
    public String showNewProductPage(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("listCategory", productService.getCategoryList());
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
        return "admin_product_add";
    }

//    @RequestMapping("/exportproducts")
//    public RedirectView ExportProductsToExcel(RedirectAttributes model) {
//        List<Product> allProducts = productService.getAllProducts();
//        Path uploadPath = Paths.get("excel_export");
//        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss");
//        String path = uploadPath.toAbsolutePath().toString() + "\\productList" + sdf.format(date).toString() + ".xlsx";
//        try {
//            XSSFWorkbook workbook = new XSSFWorkbook();
//            XSSFSheet sheet = workbook.createSheet("Product List");
//            Row row0 = sheet.createRow(0);
//            excelService.createHeader(row0);
//            int rownum = 1;
//            for (Product product : allProducts) {
//                Row row = sheet.createRow(rownum++);
//                excelService.createList(product, row);
//            }
//            FileOutputStream out = new FileOutputStream(new File(path)); // path + file name
//            workbook.write(out);
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        model.addFlashAttribute("alert", "Đã xuất file tại: " + path);
//        return new RedirectView("/admin/product");
//    }

    @RequestMapping("/readExcel")
    public RedirectView getUserByUserName(@RequestParam(value = "excelFile", required = false) MultipartFile excelFile,
                                          RedirectAttributes model) throws IOException {
        try {
            List<Product> productList = excelService.readExcel(excelFile);
            productService.saveAllProduct(productList);
            model.addFlashAttribute("alert", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            model.addFlashAttribute("alert", "Thêm sản phẩm thất bại!");
        }
        return new RedirectView("/admin/product");
    }
}
