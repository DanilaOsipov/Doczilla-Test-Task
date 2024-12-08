package com.test.doczilla_test_task.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TodoController {
    @GetMapping("/todos")
    public ResponseEntity<String> getAllTodos() {
        String url = "https://todo.doczilla.pro/api/todos";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @GetMapping("/todos/date")
    public ResponseEntity<String> getTodosByDate(@RequestParam long from,
                                                 @RequestParam long to,
                                                 @RequestParam(required = false) Boolean status) {
        String url = String.format("https://todo.doczilla.pro/api/todos/date?from=%d&to=%d", from, to);
        if (status != null) url = String.format(url + "&status=%b", status);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @GetMapping("/todos/find")
    public ResponseEntity<String> getTodosByName(@RequestParam String q) {
        String url = String.format("https://todo.doczilla.pro/api/todos/find?q=%s", q);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }
}
