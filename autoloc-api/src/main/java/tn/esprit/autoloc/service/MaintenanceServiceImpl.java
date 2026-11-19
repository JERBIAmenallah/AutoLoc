package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.MaintenanceDTO;
import tn.esprit.autoloc.web.mapper.MaintenanceMapper;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final MaintenanceMapper maintenanceMapper;

    @Override
    @Transactional
    public MaintenanceDTO create(MaintenanceDTO dto) {
        if (dto.getIdMaintenance() != null) {
            throw new IllegalArgumentException("Une nouvelle maintenance ne doit pas avoir d'identifiant");
        }
        validerMaintenance(dto);
        Maintenance maintenance = maintenanceMapper.toEntity(dto);
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(dto.getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Véhicule", dto.getIdVehicule()));
            maintenance.setVehicule(vehicule);
        }
        Maintenance saved = maintenanceRepository.save(maintenance);
        return maintenanceMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceDTO findById(Long id) {
        return maintenanceRepository.findById(id)
                .map(maintenanceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> findAll() {
        return maintenanceRepository.findAll().stream()
                .map(maintenanceMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public MaintenanceDTO update(Long id, MaintenanceDTO dto) {
        validerMaintenance(dto);
        Maintenance existant = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        existant.setDateDebut(dto.getDateDebut());
        existant.setDateFin(dto.getDateFin());
        existant.setDescription(dto.getDescription());
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(dto.getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Véhicule", dto.getIdVehicule()));
            existant.setVehicule(vehicule);
        }
        Maintenance updated = maintenanceRepository.save(existant);
        return maintenanceMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance", id);
        }
        maintenanceRepository.deleteById(id);
    }

    private void validerMaintenance(MaintenanceDTO dto) {
        if (dto.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début de maintenance est obligatoire");
        }
        if (dto.getDateFin() != null && dto.getDateFin().isBefore(dto.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
