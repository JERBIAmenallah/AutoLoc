package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.web.dto.EquipementDTO;
import tn.esprit.autoloc.web.mapper.EquipementMapper;

@Service
@RequiredArgsConstructor
public class EquipementServiceImpl implements IEquipementService {

    private final IEquipementRepository equipementRepository;
    private final EquipementMapper equipementMapper;

    @Override
    @Transactional
    public EquipementDTO create(EquipementDTO dto) {
        if (dto.getIdEquipement() != null) {
            throw new IllegalArgumentException("Un nouvel équipement ne doit pas avoir d'identifiant");
        }
        Equipement equipement = equipementMapper.toEntity(dto);
        Equipement saved = equipementRepository.save(equipement);
        return equipementMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipementDTO findById(Long id) {
        return equipementRepository.findById(id)
                .map(equipementMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementDTO> findAll() {
        return equipementRepository.findAll().stream()
                .map(equipementMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EquipementDTO update(Long id, EquipementDTO dto) {
        Equipement existant = equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement", id));
        existant.setLibelle(dto.getLibelle());
        Equipement updated = equipementRepository.save(existant);
        return equipementMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!equipementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Équipement", id);
        }
        equipementRepository.deleteById(id);
    }
}
