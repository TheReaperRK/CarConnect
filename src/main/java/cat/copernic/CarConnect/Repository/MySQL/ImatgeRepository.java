/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.CarConnect.Repository.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.VehicleImages;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author TheRe
 */
public interface ImatgeRepository extends JpaRepository<VehicleImages, Long> {

    Optional<VehicleImages> findById(Long id);
}
