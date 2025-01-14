/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para gestionar las solicitudes de inicio de sesión. Esta clase
 * maneja las peticiones de login, procesando los datos de usuario y contraseña.
 *
 * @author david
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    /**
     * Método que procesa los datos enviados por el formulario de inicio de
     * sesión. Recibe el nombre de usuario y la contraseña como parámetros y
     * realiza el procesamiento.
     *
     * @param username El nombre de usuario ingresado por el usuario.
     * @param password La contraseña ingresada por el usuario.
     * @return La vista de login si la autenticación falla o se completa
     * correctamente.
     */
    @PostMapping
    public String LoginForm(@RequestParam("username") String username, @RequestParam("password") String password) {
        // Aquí se pueden agregar las lógicas de autenticación
        System.out.println("Iniciando sesión con usuario: " + username);
        // Se puede agregar más lógica para verificar el usuario y la contraseña

        // Redirige a la página de login (se puede modificar para que redirija según el resultado)
        return "login";
    }

}
