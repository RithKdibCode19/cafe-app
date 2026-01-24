package com.example.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:8082")
@RequiredArgsConstructor
public class EmployeeController {

}
