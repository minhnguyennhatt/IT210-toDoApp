package com.todoapp.controller;

import com.todoapp.entity.Todo;
import com.todoapp.repository.TodoRepository;
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

    // READ
    @GetMapping("/")
    public String listTodos(Model model) {
        model.addAttribute("todos", todoRepository.findAll());
        model.addAttribute("todo", new Todo());
        return "todos";
    }

    // CREATE
    @PostMapping("/add")
    public String addTodo(@Valid @ModelAttribute("todo") Todo todo,
                          BindingResult result,
                          Model model) {

        if (result.hasErrors()) {
            model.addAttribute("todos", todoRepository.findAll());
            return "todos";
        }

        todoRepository.save(todo);
        return "redirect:/";
    }

    // GET: Hiển thị form edit
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo id: " + id));

        model.addAttribute("todo", todo);
        model.addAttribute("todos", todoRepository.findAll());
        return "todos";
    }

    // POST: Xử lý cập nhật
    @PostMapping("/update")
    public String updateTodo(@Valid @ModelAttribute("todo") Todo todo,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("todos", todoRepository.findAll());
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