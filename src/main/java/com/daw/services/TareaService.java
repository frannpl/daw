package com.daw.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.enumerados.Estado;
import com.daw.persistence.repositories.TareaRepository;

@Service
public class TareaService {

	@Autowired
	private TareaRepository tareaRepository;
	
	public List<Tarea> findAll(){	
		return this.tareaRepository.findAll();
	}
}
