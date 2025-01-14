/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.CarConnect.Repository.MySQL;

/**
 *
 * @author Usuario
 */

import cat.copernic.CarConnect.Entity.MySQL.IncidenciaFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaFilesRepository extends JpaRepository<IncidenciaFiles, Long> {
    List<IncidenciaFiles> findByIncidenciaId(Long incidenciaId);
}
