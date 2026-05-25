package com.teamstracking.backend.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HaversineUtilTest {

    @Test
    void deveCalcularDistanciaEntreDoisPontos() {
        double distancia = HaversineUtil.calculate(-23.5505, -46.6333, -23.5600, -46.6400);
        assertTrue(distancia > 1000 && distancia < 1500,
                "Dist�ncia esperada entre 1000m e 1500m, mas foi: " + distancia);
    }

    @Test
    void deveRetornarZeroParaMesmoPonto() {
        double distancia = HaversineUtil.calculate(-23.5505, -46.6333, -23.5505, -46.6333);
        assertEquals(0.0, distancia, 0.001);
    }

    @Test
    void deveCalcularDistanciaPositiva() {
        double distancia = HaversineUtil.calculate(-23.5505, -46.6333, -23.6000, -46.7000);
        assertTrue(distancia > 0);
    }
};
