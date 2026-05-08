package unlar.edu.ar.service;

import unlar.edu.ar.model.CuentaBancaria;
import unlar.edu.ar.model.Transaccion;
import unlar.edu.ar.model.Transaccion.TipoTransaccion;
import unlar.edu.ar.exception.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CajeroService {

    // Guardamos el formato de fecha que pide el PDF para no tener que armarlo cada vez que registramos algo.
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void deposito(CuentaBancaria cuenta, double monto) throws CuentaInactivaException {
        // Chequeamos que la cuenta no esté bloqueada.
        validarEstado(cuenta);
        
        
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor a cero.");
        }

        // Sumamos la plata al saldo actual.
        cuenta.setSaldo(cuenta.getSaldo() + monto);
        
        // Dejamos el comprobante en el historial.
        registrarOperacion(cuenta, TipoTransaccion.DEPOSITO, monto, "Depósito de efectivo");
    }

    public void extraccion(CuentaBancaria cuenta, double monto) 
            throws CuentaInactivaException, SaldoInsuficienteException, LimiteExtraccionExcedidoException {
        // Chequeamos que la cuenta no esté bloqueada.
        validarEstado(cuenta);
        
        // El cajero tiene un tope por operación impuesto por el banco (10 mil pesos).
        if (monto > 10000) {
            throw new LimiteExtraccionExcedidoException("Error: El límite máximo por extracción es de $10.000,00.");
        }
        
        // Nadie puede sacar plata que no tiene.
        if (cuenta.getSaldo() < monto) {
            throw new SaldoInsuficienteException("Error: Saldo insuficiente para completar la operación.");
        }

        // Si pasó todos los controles, le descontamos la plata.
        cuenta.setSaldo(cuenta.getSaldo() - monto);
        registrarOperacion(cuenta, TipoTransaccion.EXTRACCION, monto, "Retiro de efectivo");
    }

    public void transferencia(CuentaBancaria origen, CuentaBancaria destino, double monto) 
            throws CuentaInactivaException, SaldoInsuficienteException {
        // Para transferir, las DOS cuentas tienen que estar activas.
        validarEstado(origen);
        validarEstado(destino);

        // Verificamos que el que envía tenga la plata suficiente.
        if (origen.getSaldo() < monto) {
            throw new SaldoInsuficienteException("Error: No posee saldo suficiente para transferir.");
        }
        // Si todo está bien, hacemos el movimiento: le sacamos al que envía y le sumamos al que recibe.
        origen.setSaldo(origen.getSaldo() - monto);
        destino.setSaldo(destino.getSaldo() + monto);

        // Tenemos que anotar el movimiento en los historiales de ambas cuentas.
        registrarOperacion(origen, TipoTransaccion.TRANSFERENCIA, monto, "Transferencia enviada a: " + destino.getNumeroCuenta());
        registrarOperacion(destino, TipoTransaccion.TRANSFERENCIA, monto, "Transferencia recibida de: " + origen.getNumeroCuenta());
    }

    // --- MÉTODOS AUXILIARES PRIVADOS ---
    // Son privados porque solo los usa este mismo CajeroService por dentro.

    // Lo sacamos a un método aparte para no repetir este código en el depósito, la extracción y la transferencia.
    private void validarEstado(CuentaBancaria cuenta) throws CuentaInactivaException {
        if (!cuenta.isActiva()) {
            throw new CuentaInactivaException("La cuenta " + cuenta.getNumeroCuenta() + " está inactiva.");
        }
    }

    // Acá armamos el "ticket" de la operación. 
    private void registrarOperacion(CuentaBancaria cuenta, TipoTransaccion tipo, double monto, String desc) {
        // Creamos el objeto Transaccion para tener los datos ordenados.
        Transaccion t = new Transaccion(tipo, monto, desc, LocalDateTime.now());
        
        // Usamos StringBuilder para armar la línea de texto que va a quedar en el historial de la cuenta.
        StringBuilder log = new StringBuilder();
        log.append("[").append(t.getFecha_hora().format(FORMATO_FECHA)).append("] ");
        log.append(tipo).append(": $").append(String.format("%.2f", monto));
        log.append(" | Saldo: $").append(String.format("%.2f", cuenta.getSaldo()));
        
        // Agregamos esa línea de texto a la lista de movimientos de la cuenta.
        cuenta.getHistorialTransacciones().add(log.toString());
    }

    // Muestra solo los últimos 10 movimientos para no llenar la pantalla.
    public void imprimirHistorial(CuentaBancaria cuenta) {
        System.out.println("\n--- Historial de cuenta: " + cuenta.getNumeroCuenta() + " ---");
        
        var historial = cuenta.getHistorialTransacciones();
        
        // Math.max nos salva si la cuenta es nueva y tiene menos de 10 transacciones. 
        // Así evitamos el error de "Index out of bounds" (fuera de límite).
        int desde = Math.max(0, historial.size() - 10);
        
        // Leemos la lista de atrás para adelante, para mostrar primero lo más reciente.
        for (int i = historial.size() - 1; i >= desde; i--) {
            System.out.println(historial.get(i));
        }
    }
}