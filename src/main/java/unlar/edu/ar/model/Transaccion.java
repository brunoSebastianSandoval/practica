package unlar.edu.ar.model;

import java.time.LocalDateTime;

public class Transaccion {
    
    
    public enum TipoTransaccion {
        DEPOSITO, EXTRACCION, TRANSFERENCIA, CONSULTA
    }

    private TipoTransaccion tipo;
    private double monto;
    private String descripcion;
    private LocalDateTime fecha_hora;

    
    public Transaccion(TipoTransaccion tipo, double monto, String descripcion, LocalDateTime fecha_hora) {
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha_hora = fecha_hora;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(LocalDateTime fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
}