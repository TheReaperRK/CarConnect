package cat.copernic.CarConnect.Entity.MongoDB;

import jakarta.websocket.Encoder.Binary;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "documentacio_client")
public class DocumentacioClient {

    @Id
    private String id; // ID único para el documento

    private String dni; // DNI del cliente (relación con Client en MySQL)
    private Binary[] documents; // Array de rutas o referencias a documentos del cliente (e.g., DNI escaneado)
}
