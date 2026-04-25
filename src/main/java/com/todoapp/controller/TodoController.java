package com.todoapp.controller;

import com.todoapp.entity.Todo;
import com.todoapp.repository.TodoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
}