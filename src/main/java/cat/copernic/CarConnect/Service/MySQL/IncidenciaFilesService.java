/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Service.MySQL;

/**
 *
 * @author Carlos
 */


import cat.copernic.CarConnect.Entity.MySQL.IncidenciaFiles;
import cat.copernic.CarConnect.Repository.MySQL.IncidenciaFilesRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidenciaFilesService {

    @Autowired
    private IncidenciaFilesRepository incidenciaFilesRepository;

    @Transactional
    public void saveFile(IncidenciaFiles file) {
        incidenciaFilesRepository.save(file);
    }

    @Transactional(readOnly = true)
    public List<IncidenciaFiles> getFilesByIncidencia(Long incidenciaId) {
        return incidenciaFilesRepository.findByIncidenciaId(incidenciaId);
    }

    public IncidenciaFiles getFileById(Long fileId) {
        return incidenciaFilesRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Archivo no encontrado con ID: " + fileId));
    }
    
    public List<String> getDescripcionsForIncidencia(Long id) {
            // Buscar la entidad por ID
            IncidenciaFiles incidenciaFiles = incidenciaFilesRepository.findById(id).orElse(null);

            if (incidenciaFiles != null && incidenciaFiles.getDescription() != null) {
                // Suponiendo que las descripciones están separadas por saltos de línea o comas
                String descriptions = incidenciaFiles.getDescription();

                // Dividir el String en una lista de descripciones
                List<String> descriptionList = Arrays.asList(descriptions.split("\n")); // o "," si usas comas como delimitador

                // Saltar el primer elemento (si es necesario) y devolver el resto
                if (descriptionList.size() > 1) {
                    return descriptionList.stream()
                                          .skip(1) // Salta el primer elemento
                                          .collect(Collectors.toList());
                }
            }

            // Retorna lista vacía si no existe o no hay descripciones
            return Collections.emptyList();
        }
}
