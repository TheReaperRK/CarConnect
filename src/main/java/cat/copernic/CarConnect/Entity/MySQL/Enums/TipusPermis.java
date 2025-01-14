package cat.copernic.CarConnect.Entity.MySQL.Enums;

/**
 * Enumerador que define los tipos de permisos disponibles en el sistema. Estos
 * permisos se asignan a los roles de usuario para controlar el acceso a la
 * gestión de vehículos, reservas e incidencias.
 */
public enum TipusPermis {
    
    // Permisos relacionados con la gestión de clientes
    CREAR_CLIENT, // Permiso para crear un nuevo cliente
    EDITAR_CLIENT, // Permiso para editar un cliente existente
    LLISTAR_CLIENT, // Permiso para listar clientes
    ELIMINAR_CLIENT, // Permiso para eliminar un cliente
    
    // Permisos relacionados con la gestión de agentes
    CREAR_AGENT, // Permiso para crear un nuevo agente
    EDITAR_AGENT, // Permiso para editar un agente existente
    LLISTAR_AGENT, // Permiso para listar agentes
    ELIMINAR_AGENT, // Permiso para eliminar un agente
    
    // Permisos relacionados con la gestión de vehículos
    CREAR_VEHICLE, // Permiso para crear un nuevo vehículo
    EDITAR_VEHICLE, // Permiso para editar un vehículo existente
    LLISTAR_VEHICLE, // Permiso para listar vehículos
    ELIMINAR_VEHICLE, // Permiso para eliminar un vehículo

    // Permisos relacionados con la gestión de reservas
    CREAR_RESERVES, // Permiso para crear una nueva reserva
    EDITAR_RESERVES, // Permiso para editar una reserva existente
    LLISTAR_RESERVES, // Permiso para listar reservas
    ELIMINAR_RESERVES, // Permiso para eliminar una reserva
    GESTIO_RESERVES, // Permiso para gestionar reservas (administración de reservas activas, etc.)

    // Permisos relacionados con la gestión de incidencias
    CREAR_INCIDENCIES, // Permiso para crear una nueva incidencia
    EDITAR_INCIDENCIES, // Permiso para editar una incidencia existente
    LLISTAR_INCIDENCIES,// Permiso para listar incidencias
    ELIMINAR_INCIDENCIES, // Permiso para eliminar una incidencia
    GESTIO_INCIDENCIES,  // Permiso para gestionar incidencias (resolución, administración de estados, etc.)
    
    HISTORIC_INCIDENCIES,
    HISTORIC_RESERVES,
    // Permisos de administrador
    ADMIN // Permiso para obtener todos los permisos
}
