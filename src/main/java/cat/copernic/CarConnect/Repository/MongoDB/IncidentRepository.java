package cat.copernic.CarConnect.Repository.MongoDB;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 * @author olivi
 */
class IncidentRepository {

    @Autowired
    private MongoTemplate mongoTemplate;
}
