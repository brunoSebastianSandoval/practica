package unlar.edu.ar.model;

public class CuentaBancaria {
    private String numeroCuenta;
    private double saldo;
    private String titular;
    private boolean activa;
    private ArrayList<String> historialTransacciones;

    public CuentaBancaria(String numeroCuenta, String titular, double saldoInicial, boolean activa) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.activa = activa;
        this.saldo = saldoInicial;
        this.historialTransacciones = new ArrayList<>();
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public ArrayList<String> getHistorialTransacciones() {
        return historialTransacciones;
    }

    public void setHistorialTransacciones(ArrayList<String> historialTransacciones) {
        this.historialTransacciones = historialTransacciones;
    }
    
}
    