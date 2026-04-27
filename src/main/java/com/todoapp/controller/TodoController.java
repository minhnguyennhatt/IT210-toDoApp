package com.todoapp.controller;

import com.todoapp.entity.Todo;
import com.todoapp.repository.TodoRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TodoController {

    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // ========== SESSION: Owner name ==========

    // GET: Hiển thị trang nhập tên (login)
    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        // Nếu đã có tên rồi thì thẳng vào trang chủ
        if (session.getAttribute("ownerName") != null) {
            return "redirect:/";
        }
        return "login";
    }

    // POST: Lưu tên vào session
    @PostMapping("/login")
    public String processLogin(@RequestParam("ownerName") String ownerName,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập tên của bạn!");
            return "redirect:/login";
        }
        session.setAttribute("ownerName", ownerName.trim());
        return "redirect:/";
    }

    // GET: Đăng xuất (xóa session)
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ========== CRUD ==========

    // READ
    @GetMapping("/")
    public String listTodos(Model model, HttpSession session) {
        // Chưa nhập tên → chuyển về login
        if (session.getAttribute("ownerName") == null) {
            return "redirect:/login";
        }

        model.addAttribute("todos", todoRepository.findAll());
        model.addAttribute("todo", new Todo());
        model.addAttribute("ownerName", session.getAttribute("ownerName"));
        return "todos";
    }

    // CREATE
    @PostMapping("/add")
    public String addTodo(@Valid @ModelAttribute("todo") Todo todo,
                          BindingResult result,
                          Model model,
                          HttpSession session) {

        if (result.hasErrors()) {
            model.addAttribute("todos", todoRepository.findAll());
            model.addAttribute("ownerName", session.getAttribute("ownerName"));
            return "todos";
        }

        todoRepository.save(todo);
        return "redirect:/";
    }

    // GET: Hiển thị form edit
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id,
                                 Model model,
                                 HttpSession session) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo id: " + id));

        model.addAttribute("todo", todo);
        model.addAttribute("todos", todoRepository.findAll());
        model.addAttribute("ownerName", session.getAttribute("ownerName"));
        return "todos";
    }

    // POST: Xử lý cập nhật
    @PostMapping("/update")
    public String updateTodo(@Valid @ModelAttribute("todo") Todo todo,
                             BindingResult result,
                             Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("todos", todoRepository.findAll());
            model.addAttribute("ownerName", session.getAttribute("ownerName"));
            return "todos";
        }

        todoRepository.save(todo); // auto update nếu có id
        redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");
        return "redirect:/";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {

        if (!todoRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("message", "Task không tồn tại!");
            return "redirect:/";
        }

        todoRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Xóa thành công!");
        return "redirect:/";
    }
}
