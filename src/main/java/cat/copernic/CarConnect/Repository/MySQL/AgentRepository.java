package cat.copernic.CarConnect.Repository.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Agent;
import cat.copernic.CarConnect.Entity.MySQL.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentRepository extends JpaRepository<Agent, String>, JpaSpecificationExecutor<Agent> {

    Optional<Agent> findByDni(String dni);
    
    @Query("SELECT a FROM Agent a WHERE a.email = :email")
    List<Agent> findByEmail(
            @Param("email") String email
    );
    //List<Agent> findById(String id);
}
