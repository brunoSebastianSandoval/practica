package unlar.edu.ar;

import unlar.edu.ar.model.CuentaBancaria;
import unlar.edu.ar.service.CajeroService;
import unlar.edu.ar.ui.MenuUI;
import unlar.edu.ar.exception.*;

import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        
        // Simulamos la base de datos usando un mapa para buscar cuentas destino por su numero.
        Map<String, CuentaBancaria> baseDatos = new HashMap<>();

        // Creamos las tres cuentas exigidas. La tercera es inactiva para probar los bloqueos correspondientes.
        CuentaBancaria cuentaBruno = new CuentaBancaria("111", "Bruno Sandoval", 50000.0, true);
        CuentaBancaria cuentaVale = new CuentaBancaria("222", "Valentino Lemor", 15000.0, true);
        CuentaBancaria cuentaInactiva = new CuentaBancaria("333", "Cuenta Bloqueada", 5000.0, false);

        // Cargamos las cuentas en nuestra base de datos.
        baseDatos.put(cuentaBruno.getNumeroCuenta(), cuentaBruno);
        baseDatos.put(cuentaVale.getNumeroCuenta(), cuentaVale);
        baseDatos.put(cuentaInactiva.getNumeroCuenta(), cuentaInactiva);

        CajeroService cajeroService = new CajeroService();

        System.out.println("==================================================");
        System.out.println("INICIANDO SIMULACION AUTOMATICA DE 15 TRANSACCIONES");
        System.out.println("==================================================");

        try {
            // Ejecutamos 12 operaciones de rutina para cumplir con la cantidad solicitada.
            cajeroService.deposito(cuentaBruno, 5000);
            cajeroService.extraccion(cuentaBruno, 2000);
            cajeroService.transferencia(cuentaBruno, cuentaVale, 3000);
            cajeroService.deposito(cuentaVale, 1000);
            cajeroService.extraccion(cuentaVale, 500);
            cajeroService.transferencia(cuentaVale, cuentaBruno, 1500);
            
            System.out.println("Primer bloque de operaciones exitosas.");

            cajeroService.deposito(cuentaBruno, 100);
            cajeroService.deposito(cuentaBruno, 200);
            cajeroService.extraccion(cuentaBruno, 100);
            cajeroService.deposito(cuentaVale, 800);
            cajeroService.extraccion(cuentaVale, 100);
            cajeroService.transferencia(cuentaBruno, cuentaVale, 50);
            
            System.out.println("Segundo bloque de operaciones exitosas.");

            // Operacion 13: Forzamos la excepcion por limite de extraccion excedido.
            System.out.println("\nIntentando extraer mas del limite permitido...");
            cajeroService.extraccion(cuentaBruno, 15000); 
            
        } catch (LimiteExtraccionExcedidoException e) {
            System.out.println("Excepcion Atrapada (1): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        try {
            // Operacion 14: Forzamos la excepcion por saldo insuficiente al transferir.
            System.out.println("\nIntentando transferir sin fondos suficientes...");
            cajeroService.transferencia(cuentaVale, cuentaBruno, 100000); 
        } catch (SaldoInsuficienteException e) {
            System.out.println("Excepcion Atrapada (2): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        try {
            // Operacion 15: Forzamos la excepcion operando con una cuenta inactiva.
            System.out.println("\nIntentando operar con una cuenta inactiva...");
            cajeroService.deposito(cuentaInactiva, 1000); 
        } catch (CuentaInactivaException e) {
            System.out.println("Excepcion Atrapada (3): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        System.out.println("\nSimulacion finalizada. Iniciando interfaz de usuario...\n");

        // Pasamos el control al usuario abriendo el menu interactivo.
        MenuUI menu = new MenuUI(cajeroService, baseDatos);
        menu.iniciar(cuentaBruno);
    }
}