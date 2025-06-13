package upeu.edu.pe.registroa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upeu.edu.pe.registroa.model.Paciente;
import upeu.edu.pe.registroa.repository.PacienteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }
    
    public Optional<Paciente> findById(Long id) {
        return pacienteRepository.findById(id);
    }
    
    public Optional<Paciente> findByDni(String dni) {
        return pacienteRepository.findByDni(dni);
    }
    
    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }
    
    public void deleteById(Long id) {
        pacienteRepository.deleteById(id);
    }
    
    public boolean existsByDni(String dni) {
        return pacienteRepository.existsByDni(dni);
    }
    
    public boolean existsByDniAndIdNot(String dni, Long id) {
        return pacienteRepository.findByDni(dni)
                .map(p -> !p.getId().equals(id))
                .orElse(false);
    }
}