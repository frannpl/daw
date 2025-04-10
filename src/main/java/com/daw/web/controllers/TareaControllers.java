package com.daw.web.controllers;

import java.time.LocalDate;

import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.daw.persistence.entities.Tarea;

import com.daw.persistence.entities.enumerados.Estado;
import com.daw.services.TareaService;
import com.daw.services.exception.TareaException;
import com.daw.services.exception.TareaNotFoundException;

@RestController
@RequestMapping("/tareas")

public class TareaControllers {

	@Autowired
	private TareaService tareaService;

	@GetMapping
	public ResponseEntity<List<Tarea>> list() {
		return ResponseEntity.status(HttpStatus.OK).body(this.tareaService.findAll());
	}

	@GetMapping("/{idTarea}")
	public ResponseEntity<?> findById(@PathVariable int idTarea) {
		try {
			return ResponseEntity.ok(this.tareaService.findById(idTarea));
		} catch (TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	@DeleteMapping("/{idTarea}")
	public ResponseEntity<?> delete(@PathVariable int idTarea) {
		try {
			this.tareaService.deleteById(idTarea);
			return ResponseEntity.ok("La tarea con ID("+ idTarea +") ha sido borrada correctamente. " );
		} catch (TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}

	}

	@PostMapping("/{idTarea}")
	public ResponseEntity<Tarea> create(@RequestBody Tarea tarea) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.tareaService.create(tarea));
	}


	@PutMapping("/{idTarea}")
	public ResponseEntity<?> update(@PathVariable int idTarea, @RequestBody Tarea tarea) {
		try {
			return ResponseEntity.ok(this.tareaService.update(tarea, idTarea));
		} catch (TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		} catch (TareaException ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
		}
	}
	
	@PutMapping("/{idTarea}/iniciar")
	public ResponseEntity<?> iniciarTarea(@PathVariable int idTarea) {
		try {
			Tarea tarea = tareaService.findById(idTarea);
			if (tarea.getEstado() != Estado.PENDIENTE) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Solo se pueden iniciar tareas PENDIENTES.");
			}
			tarea.setEstado(Estado.EN_PROGRESO);
			return ResponseEntity.ok(tareaService.create(tarea)); 
		} catch (TareaNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	@PutMapping("/{idTarea}/completar")
	public ResponseEntity<?> completarTarea(@PathVariable int idTarea) {
		try {
			Tarea tarea = tareaService.findById(idTarea);
			if (tarea.getEstado() != Estado.EN_PROGRESO) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Solo se pueden completar tareas EN PROGRESO.");
			}
			tarea.setEstado(Estado.COMPLETADA);
			return ResponseEntity.ok(tareaService.create(tarea)); 
		} catch (TareaNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		
	}
	
	@GetMapping("/pendientes")
	public ResponseEntity<List<Tarea>> getTareasPendientes() {
		List<Tarea> resultado = new ArrayList<>();
		for (Tarea tarea : tareaService.findAll()) {
			if (tarea.getEstado() == Estado.PENDIENTE) {
				resultado.add(tarea);
			}
		}
		return ResponseEntity.ok(resultado);
	}
	
	@GetMapping("/en-progreso")
	public ResponseEntity<List<Tarea>> getTareasEnProgreso() {
		List<Tarea> resultado = new ArrayList<>();
		for (Tarea tarea : tareaService.findAll()) {
			if (tarea.getEstado() == Estado.EN_PROGRESO) {
				resultado.add(tarea);
			}
		}
		return ResponseEntity.ok(resultado);
	}
	
	@GetMapping("/completadas")
	public ResponseEntity<List<Tarea>> getTareasCompletadas() {
		List<Tarea> resultado = new ArrayList<>();
		for (Tarea tarea : tareaService.findAll()) {
			if (tarea.getEstado() == Estado.COMPLETADA) {
				resultado.add(tarea);
			}
		}
		return ResponseEntity.ok(resultado);
	}
	
	@GetMapping("/vencidas")
	public ResponseEntity<List<Tarea>> getTareasVencidas() {
		List<Tarea> resultado = new ArrayList<>();
		LocalDate hoy = LocalDate.now();
		for (Tarea tarea : tareaService.findAll()) {
			if (tarea.getFechaVencimiento().isBefore(hoy)) {
				resultado.add(tarea);
			}
		}
		return ResponseEntity.ok(resultado);
	}
	
	@GetMapping("/no-vencidas")
	public ResponseEntity<List<Tarea>> getTareasNoVencidas() {
		List<Tarea> resultado = new ArrayList<>();
		LocalDate hoy = LocalDate.now();
		for (Tarea tarea : tareaService.findAll()) {
			if (tarea.getFechaVencimiento().isAfter(hoy) || tarea.getFechaVencimiento().isEqual(hoy)) {
				resultado.add(tarea);
			}
		}
		return ResponseEntity.ok(resultado);
	}
	
	@GetMapping("/buscar/{titulo}")
	public ResponseEntity<List<Tarea>> buscarPorTitulo(@PathVariable String titulo) {
		List<Tarea> resultado = new ArrayList<>();
		for (Tarea tarea : tareaService.findAll()) {
			if (tarea.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
				resultado.add(tarea);
			}
		}
		return ResponseEntity.ok(resultado);
	}
}