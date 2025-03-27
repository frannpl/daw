package com.daw.web.controllers;

import java.time.LocalDate;

import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.daw.persistence.entities.Tarea;

import com.daw.persistence.entities.enumerados.Estado;
import com.daw.services.TareaService;

@RestController
@RequestMapping("/tareas")

public class TareaControllers {

	@Autowired
	private TareaService tareaService;
	
	@GetMapping
	public List<Tarea> list() {

		return this.tareaService.findAll();

	}

}