package unlar.edu.ar.ui;

import unlar.edu.ar.model.CuentaBancaria;
import unlar.edu.ar.service.CajeroService;
import unlar.edu.ar.exception.*;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class MenuUI {
    private final CajeroService cajeroService;
    private final Scanner scanner;
    
    // usamos maps para simular una base de datos de cuentas, con el número de cuenta como clave y el objeto CuentaBancaria como valor
    private final Map<String, CuentaBancaria> baseDatosCuentas;

    public MenuUI(CajeroService cajeroService, Map<String, CuentaBancaria> baseDatosCuentas) {
        this.cajeroService = cajeroService;
        this.baseDatosCuentas = baseDatosCuentas;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar(CuentaBancaria cuentaActiva) {
        boolean salir = false;

        //  Bucle principal del menú
        while (!salir) {
            System.out.println("\n=================================");
            System.out.println(" CAJERO AUTOMÁTICO UNLaR");
            System.out.println(" Titular: " + cuentaActiva.getTitular());
            System.out.println("=================================");
            System.out.println("1. Consultar Saldo");
            System.out.println("2. Depositar Efectivo");
            System.out.println("3. Extraer Efectivo");
            System.out.println("4. Transferir a otra cuenta");
            System.out.println("5. Ver Historial de Movimientos");
            System.out.println("6. Salir");
            System.out.print(" Elija una opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiamos el "Enter" que queda flotando en el buffer del teclado

                
                switch (opcion) {
                    case 1 -> consultarSaldo(cuentaActiva);
                    case 2 -> realizarDeposito(cuentaActiva);
                    case 3 -> realizarExtraccion(cuentaActiva);
                    case 4 -> realizarTransferencia(cuentaActiva);
                    case 5 -> cajeroService.imprimirHistorial(cuentaActiva);
                    case 6 -> {
                        System.out.println(" Gracias por utilizar la red de cajeros UNLaR. ¡Hasta luego!");
                        salir = true;
                    }
                    default -> System.out.println(" Opción inválida. Por favor, ingrese un número del 1 al 6.");
                }

            } catch (InputMismatchException e) {
                
                System.out.println(" Error: Debe ingresar valores numéricos. No se aceptan letras o símbolos.");
                scanner.nextLine(); // Limpiamos el buffer corrupto para que el bucle no se vuelva loco (loop infinito)
            }
        }
    }

    // --- MÉTODOS PRIVADOS PARA CADA OPERACIÓN ---

    private void consultarSaldo(CuentaBancaria cuenta) {
        // Usamos Locale.US para asegurar que los miles se separen con coma y los decimales con punto.
        System.out.println("\n Su saldo actual es: " + String.format(Locale.US, "$%,.2f", cuenta.getSaldo()));
    }

    private void realizarDeposito(CuentaBancaria cuenta) {
        System.out.print("Ingrese el monto a depositar: $");
        try {
            double monto = scanner.nextDouble();
            cajeroService.deposito(cuenta, monto);
            System.out.println(" Depósito realizado con éxito.");
            consultarSaldo(cuenta);
        } catch (InputMismatchException e) {
            System.out.println(" Error: Monto inválido.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println(" " + e.getMessage());
        }
    }

    private void realizarExtraccion(CuentaBancaria cuenta) {
        System.out.print("Ingrese el monto a extraer: $");
        try {
            double monto = scanner.nextDouble();
            cajeroService.extraccion(cuenta, monto);
            System.out.println(" Extracción exitosa. Retire su dinero.");
            consultarSaldo(cuenta);
        // Acá atrapamos las excepciones personalizadas que armamos antes
        } catch (SaldoInsuficienteException | LimiteExtraccionExcedidoException | CuentaInactivaException e) {
            System.out.println(" " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println(" Error: Monto numérico inválido.");
            scanner.nextLine();
        }
    }

    private void realizarTransferencia(CuentaBancaria cuentaOrigen) {
        System.out.print("Ingrese el número de cuenta destino: ");
        String cbuDestino = scanner.nextLine();

        // Buscamos si la cuenta existe en nuestra "base de datos" simulada
        CuentaBancaria cuentaDestino = baseDatosCuentas.get(cbuDestino);

        if (cuentaDestino == null) {
            System.out.println(" Error: La cuenta destino no existe en el sistema.");
            return;
        }

        if (cuentaDestino.getNumeroCuenta().equals(cuentaOrigen.getNumeroCuenta())) {
            System.out.println(" Error: No puede transferirse dinero a usted mismo.");
            return;
        }

        System.out.print("Ingrese el monto a transferir: $");
        try {
            double monto = scanner.nextDouble();
            cajeroService.transferencia(cuentaOrigen, cuentaDestino, monto);
            System.out.println(" Transferencia enviada exitosamente a " + cuentaDestino.getTitular() + ".");
            consultarSaldo(cuentaOrigen);
        } catch (SaldoInsuficienteException | CuentaInactivaException e) {
            System.out.println(" " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println(" Error: Monto numérico inválido.");
            scanner.nextLine();
        }
    }
}