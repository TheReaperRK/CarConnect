/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Security;

import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusPermis;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author david

 */

public class Permis implements GrantedAuthority{
    
    TipusPermis permis;

    public Permis(TipusPermis permis) {
        this.permis = permis;
    }

    @Override
    public String getAuthority() {
        return permis.toString();
    }

}
