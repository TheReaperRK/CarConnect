package cat.copernic.CarConnect.Entity.MongoDB;

import jakarta.websocket.Encoder.Binary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documentacio_incidencia")
public class DocumentacioIncidencia {

    @Id
    private String id; // ID único para el documento

    private String idIncidencia; // ID de la incidencia (relación con Incident en MongoDB)
    private Binary[] documents; // Array de rutas o referencias a documentos (e.g., fotos, informes)
}
