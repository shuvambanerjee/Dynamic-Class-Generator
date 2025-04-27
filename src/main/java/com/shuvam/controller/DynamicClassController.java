package com.shuvam.controller;

import com.shuvam.model.ClassDefinitionRequest;
import com.shuvam.service.DynamicClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dynamic")
public class DynamicClassController {

    private final DynamicClassService dynamicClassService;

    public DynamicClassController(DynamicClassService dynamicClassService) {
        this.dynamicClassService = dynamicClassService;
    }

    @PostMapping("/generateClass")
    public ResponseEntity<String> generateClass(@RequestBody ClassDefinitionRequest request) {
        if (request.getClassName() == null || request.getClassName().isEmpty()) {
            return ResponseEntity.badRequest().body("Class name is required.");
        }

        if (request.getFields() == null || request.getFields().isEmpty()) {
            return ResponseEntity.badRequest().body("Class fields are required.");
        }

        String response = dynamicClassService.generateAndRegisterClass(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listGeneratedClasses() {
        return ResponseEntity.ok(dynamicClassService.listGeneratedClasses());
    }

}
