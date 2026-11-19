package tn.esprit.autoloc.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IEquipementRepository equipementRepository;
    private final IMaintenanceRepository maintenanceRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    @Transactional
    public VehiculeDTO create(VehiculeDTO dto) {
        if (dto.getIdVehicule() != null) {
            throw new IllegalArgumentException("Un nouveau véhicule ne doit pas avoir d'identifiant");
        }
        Vehicule vehicule = vehiculeMapper.toEntity(dto);
        Vehicule saved = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeDTO findById(Long id) {
        return vehiculeRepository.findById(id)
                .map(vehiculeMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDTO> findAll() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public VehiculeDTO update(Long id, VehiculeDTO dto) {
        Vehicule existant = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", id));

        existant.setImmatriculation(dto.getImmatriculation());
        existant.setMarque(dto.getMarque());
        existant.setModele(dto.getModele());
        existant.setCategorie(dto.getCategorie());
        existant.setTarifJournalier(dto.getTarifJournalier());
        existant.setStatut(dto.getStatut());

        Vehicule updated = vehiculeRepository.save(existant);
        return vehiculeMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Véhicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", idVehicule));

        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement", idEquipement));

        if (vehicule.getEquipements() == null) {
            vehicule.setEquipements(new ArrayList<>());
        }

        boolean dejaAffecte = vehicule.getEquipements().stream()
                .anyMatch(e -> e.getIdEquipement().equals(idEquipement));
        if (!dejaAffecte) {
            vehicule.getEquipements().add(equipement);
        }

        Vehicule saved = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toDto(saved);
    }

    @Override
    @Transactional
    public VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation) {
        if (description == null || description.isBlank() || immatriculation == null || immatriculation.isBlank()) {
            throw new IllegalArgumentException("La description et l'immatriculation sont obligatoires");
        }

        Maintenance maintenance = maintenanceRepository.findFirstByDescriptionIgnoreCase(description.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance introuvable avec la description : " + description));

        Vehicule vehicule = vehiculeRepository.findByImmatriculation(immatriculation.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Véhicule introuvable avec l'immatriculation : " + immatriculation));

        maintenance.setVehicule(vehicule);
        maintenanceRepository.save(maintenance);

        if (vehicule.getMaintenances() == null) {
            vehicule.setMaintenances(new ArrayList<>());
        }
        boolean dejaPresente = vehicule.getMaintenances().stream()
                .anyMatch(m -> m.getIdMaintenance().equals(maintenance.getIdMaintenance()));
        if (!dejaPresente) {
            vehicule.getMaintenances().add(maintenance);
        }

        return vehiculeMapper.toDto(vehicule);
    }
}
