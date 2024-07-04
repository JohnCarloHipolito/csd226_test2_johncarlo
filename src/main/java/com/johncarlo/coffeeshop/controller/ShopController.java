package com.johncarlo.coffeeshop.controller;

import com.johncarlo.coffeeshop.model.*;
import com.johncarlo.coffeeshop.service.OrderService;
import com.johncarlo.coffeeshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping("/shop")
    public String shop(Model model, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        model.addAttribute("user", user);
        List<Product> products = productService.getProducts();
        model.addAttribute("products", products);
        Order order = (Order) httpSession.getAttribute("order");
        if(order != null) {
            model.addAttribute("itemCount", order.getItems().stream().mapToInt(Item::getQuantity).sum());
        }
        return "shop";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, @RequestParam String callback, HttpSession httpSession, RedirectAttributes redirectAttributes) {
        Order order = httpSession.getAttribute("order") == null ? new Order() : (Order) httpSession.getAttribute("order");
        Product product = productService.getProduct(productId);
        List<Item> items = order.getItems() == null ? new ArrayList<>() : order.getItems();
        items.stream().filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().ifPresentOrElse(
                        (item) -> item.setQuantity(item.getQuantity() + 1),
                        () -> items.add(Item.builder().product(product).quantity(1).build())
                );
        order.setItems(items);
        httpSession.setAttribute("order", order);
        redirectAttributes.addFlashAttribute("message", "Item added to cart!");
        return callback;
    }



    @GetMapping("/orders")
    public String orders(HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        model.addAttribute("orders", orderService.getOrders(user));
        Order order = (Order) httpSession.getAttribute("order");
        if(order != null) {
            model.addAttribute("itemCount", order.getItems().stream().mapToInt(Item::getQuantity).sum());
        }
        return "orders";
    }

    @GetMapping("/cart")
    public String cart(HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("order", httpSession.getAttribute("order"));
        Order order = (Order) httpSession.getAttribute("order");
        if(order != null) {
            double totalAmount = order.getItems().stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
            model.addAttribute("itemCount", order.getItems().stream().mapToInt(Item::getQuantity).sum());
            model.addAttribute("totalAmount", Math.round(totalAmount * 100.0) / 100.0);
            model.addAttribute("message", "Welcome to the cart!");
        }
        return "cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId, @RequestParam String callback, HttpSession httpSession, RedirectAttributes redirectAttributes) {
        Order order = (Order) httpSession.getAttribute("order");
        List<Item> items = order.getItems();
        items.removeIf(item -> item.getProduct().getId().equals(productId));
        order.setItems(items);
        httpSession.setAttribute("order", order);
        redirectAttributes.addFlashAttribute("message", "Item removed from cart!");
        return callback;
    }

    @PostMapping("/cart/checkout")
    public String checkout(HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        if(user == null) {
            redirectAttributes.addFlashAttribute("message", "Please login to checkout!");
            return "redirect:/login";
        } else {
            Order order = (Order) httpSession.getAttribute("order");
            order.setUser(user);
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setOrderDate(LocalDateTime.now());
            Double totalAmount = BigDecimal.valueOf(order.getItems().stream()
                            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum())
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            order.setTotalAmount(totalAmount);
            order.setStatus(Status.PENDING);
            orderService.saveOrder(order);
            httpSession.removeAttribute("order");
            return "redirect:/orders";
        }
    }
}
