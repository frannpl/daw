package com.daw.persistence.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.enumerados.Estado;

public interface TareaRepository extends ListCrudRepository<Tarea, Integer>{

	//Obtener el número total de tareas completadas.
		/*
		 * 	SELECT count(*)
		 * 	FROM tarea
		 * 	WHERE estado = ?1
		 */
		long countByEstado(Estado estado);
		
		//Obtener una lista de las fechas de vencimiento de las tareas que estén en progreso.	
		//Obtener los títulos de las tareas pendientes.
		List<Tarea> findByEstado(Estado estado);
		
		//Obtener las tareas vencidas.
		List<Tarea> findByFechaVencimientoBefore(LocalDate fecha);
		
		//Obtener las tareas ordenadas por fecha de vencimiento.
		List<Tarea> findAllByOrderByFechaVencimiento();
		
		
		
}
