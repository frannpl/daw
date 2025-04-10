package com.daw.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.enumerados.Estado;
import com.daw.persistence.repositories.TareaRepository;
import com.daw.services.exception.TareaException;
import com.daw.services.exception.TareaNotFoundException;

@Service
public class TareaService {

	@Autowired
	private TareaRepository tareaRepository;
	
	public List<Tarea> findAll(){	
		return this.tareaRepository.findAll();
	}
	
	public Tarea findById(int idTarea) {
		
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("El id de la tarea no existe");
		}
		
		return this.tareaRepository.findById(idTarea).get(); 
	}
	
	public boolean existById(int idTarea) {
		return this.tareaRepository.existsById(idTarea);
	}
	
	public void deleteById(int idTarea) {
		
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe la tarea con ID: " + idTarea);
		}
		
		this.tareaRepository.deleteById(idTarea);
	}
	
	public Tarea create(Tarea tarea) {
		tarea.setId(0);
		tarea.setFechaCreacion(LocalDate.now());
		tarea.setEstado(Estado.PENDIENTE);
		
		if(tarea.getFechaVencimiento() == null || !tarea.getFechaVencimiento().isAfter(LocalDate.now())) {
			throw new TareaException("La fecha de vencimiento debe ser posterior a la de creación");
		}
		
		return this.tareaRepository.save(tarea);
	}
	
	public Tarea update(Tarea tarea, int idTarea) {
		 
		
		if(idTarea != tarea.getId()) {
			throw new TareaException("El ID del path ("+ idTarea +") y el id del body ("+ tarea.getId() +") no coinciden");
		}
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe la tarea con ID: " + idTarea);
		}
		if(tarea.getFechaCreacion() != null || tarea.getEstado() != null) {
			throw new TareaException("No se permite modificar la fecha de creación y/o el estado. ");
		}
		
		Tarea tareaBD = this.findById(tarea.getId());
		
		if(tarea.getFechaVencimiento() == null || !tarea.getFechaVencimiento().isAfter(tareaBD.getFechaCreacion())) {
			throw new TareaException("La fecha de vencimiento debee ser posterior a la fecha de creación");
		}
		
		tareaBD.setTitulo(tarea.getTitulo());
		tareaBD.setDescripcion(tarea.getDescripcion());
		tareaBD.setFechaVencimiento(tarea.getFechaVencimiento());
		
		return this.tareaRepository.save(tareaBD);
	}
	
	//Ejemplos Optional
		public boolean deleteDeclarativo(int idTarea) {
			boolean result = false;
			
			if(this.tareaRepository.existsById(idTarea)) {
				this.tareaRepository.deleteById(idTarea);
				result = true;
			}
			
			return result;
		}
		
		public boolean deleteFuncional(int idTarea) {
			return this.tareaRepository.findById(idTarea)
					.map(t -> {
						this.tareaRepository.deleteById(idTarea);
						return true;
					})
					.orElse(false);
		}
		
		public Tarea findByIdFuncional(int idTarea) {		
			return this.tareaRepository.findById(idTarea)
					.orElseThrow(() -> new TareaNotFoundException("No existe la tarea con ID: " + idTarea));
		}
		
		//Ejemplos Stream
		//Obtener el número total de tareas completadas.
		public long totalTareasCompletadasFuncional() {
			return this.tareaRepository.findAll().stream()
					.filter(t -> t.getEstado() == Estado.COMPLETADA)
					.count();
		}
		
		public long totalTareasCompletadas() {
			return this.tareaRepository.countByEstado(Estado.COMPLETADA);
		}
		
		//Obtener una lista de las fechas de vencimiento de las tareas que estén en progreso.
		public List<LocalDate> fechasVencimientoEnProgresoFuncional() {
			return this.tareaRepository.findAll().stream()
					.filter(t -> t.getEstado() == Estado.EN_PROGRESO)
					.map(t -> t.getFechaVencimiento())
					.collect(Collectors.toList());
		}
		
		public List<LocalDate> fechasVencimientoEnProgreso() {
			return this.tareaRepository.findByEstado(Estado.EN_PROGRESO).stream()
					.map(t -> t.getFechaVencimiento())
					.collect(Collectors.toList());
		}
		
		
		//Obtener las tareas vencidas.
		public List<Tarea> obtenerTareasVencidas() {
			LocalDate hoy = LocalDate.now();
			List<Tarea> resultado = new ArrayList<>();
			for (Tarea tarea : this.findAll()) {
				if (tarea.getFechaVencimiento().isBefore(hoy)) {
					resultado.add(tarea);
				}
			}
			return resultado;
		}
		
		
		public List<Tarea> obtenerTareasNoVencidas() {
			LocalDate hoy = LocalDate.now();
			List<Tarea> resultado = new ArrayList<>();
			for (Tarea tarea : this.findAll()) {
				if (tarea.getFechaVencimiento().isAfter(hoy) || tarea.getFechaVencimiento().isEqual(hoy)) {
					resultado.add(tarea);
				}
			}
			return resultado;
		}
		
		//Obtener las tareas ordenadas por fecha de vencimiento.
		public List<Tarea> ordenadasFechaVencimientoFuncional(){
			return this.tareaRepository.findAll().stream()
					.sorted((t1, t2) -> t1.getFechaVencimiento().compareTo(t2.getFechaVencimiento()))
					.collect(Collectors.toList());
		}	

		public List<Tarea> ordenadasFechaVencimiento(){
			return this.tareaRepository.findAllByOrderByFechaVencimiento();
		}
		
		public Tarea iniciarTarea(int idTarea) {
			Tarea tarea = this.findById(idTarea);
			if (tarea.getEstado() != Estado.PENDIENTE) {
				throw new TareaException("Solo se pueden iniciar tareas PENDIENTES.");
			}
			tarea.setEstado(Estado.EN_PROGRESO);
			return tareaRepository.save(tarea);
		}

		public Tarea completarTarea(int idTarea) {
			Tarea tarea = this.findById(idTarea);
			if (tarea.getEstado() != Estado.EN_PROGRESO) {
				throw new TareaException("Solo se pueden completar tareas EN PROGRESO.");
			}
			tarea.setEstado(Estado.COMPLETADA);
			return tareaRepository.save(tarea);
		}

		public List<Tarea> obtenerTareasPorEstado(Estado estado) {
			List<Tarea> resultado = new ArrayList<>();
			for (Tarea tarea : this.findAll()) {
				if (tarea.getEstado() == estado) {
					resultado.add(tarea);
				}
			}
			return resultado;
		}
		
		public List<Tarea> buscarPorTitulo(String titulo) {
			List<Tarea> resultado = new ArrayList<>();
			for (Tarea tarea : this.findAll()) {
				if (tarea.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
					resultado.add(tarea);
				}
			}
			return resultado;
		}
}
