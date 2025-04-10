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
			Tarea tareaActualizada = tareaService.iniciarTarea(idTarea);
			return ResponseEntity.ok(tareaActualizada);
		} catch (TareaNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (TareaException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PutMapping("/{idTarea}/completar")
	public ResponseEntity<?> completarTarea(@PathVariable int idTarea) {
		try {
			Tarea tareaActualizada = tareaService.completarTarea(idTarea);
			return ResponseEntity.ok(tareaActualizada);
		} catch (TareaNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (TareaException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@GetMapping("/pendientes")
	public ResponseEntity<List<Tarea>> getTareasPendientes() {
		return ResponseEntity.ok(tareaService.obtenerTareasPorEstado(Estado.PENDIENTE));
	}

	@GetMapping("/en-progreso")
	public ResponseEntity<List<Tarea>> getTareasEnProgreso() {
		return ResponseEntity.ok(tareaService.obtenerTareasPorEstado(Estado.EN_PROGRESO));
	}

	@GetMapping("/completadas")
	public ResponseEntity<List<Tarea>> getTareasCompletadas() {
		return ResponseEntity.ok(tareaService.obtenerTareasPorEstado(Estado.COMPLETADA));
	}

	@GetMapping("/vencidas")
	public ResponseEntity<List<Tarea>> getTareasVencidas() {
		return ResponseEntity.ok(tareaService.obtenerTareasVencidas());
	}

	@GetMapping("/no-vencidas")
	public ResponseEntity<List<Tarea>> getTareasNoVencidas() {
		return ResponseEntity.ok(tareaService.obtenerTareasNoVencidas());
	}

	@GetMapping("/buscar/{titulo}")
	public ResponseEntity<List<Tarea>> buscarPorTitulo(@PathVariable String titulo) {
		return ResponseEntity.ok(tareaService.buscarPorTitulo(titulo));
	}
}