package com.example.dominio.reservas;

import java.util.Date;

public class RangoFechas {
    private final Date inicio;
    private final Date fin;

    public RangoFechas(Date inicio, Date fin) {
        if (inicio != null && fin != null && inicio.after(fin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        this.inicio = inicio;
        this.fin = fin;
    }

    public Date getInicio() {
        return inicio;
    }

    public Date getFin() {
        return fin;
    }

    public boolean seSolapaCon(Date otroInicio, Date otroFin) {
        return !(this.fin.before(otroInicio) || this.inicio.after(otroFin));
    }
    
    public boolean seSolapaCon(RangoFechas otro) {
        return seSolapaCon(otro.getInicio(), otro.getFin());
    }
}
