package com.example.carro.service;

import com.example.carro.DTO.ProductoDTO;
import com.example.carro.client.CatalogoClient;
import com.example.carro.client.InventarioClient;
import com.example.carro.model.Carro;
import com.example.carro.repository.CarroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarroServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private CarroService carroService;

    @Test
    void deberiaAgregarProductoNuevoAlCarroCuandoHayStock() {
        // 1. Preparamos los datos falsos
        ProductoDTO productoMock = new ProductoDTO();
        productoMock.setNombre("Tarjeta Grafica RTX");
        productoMock.setPrecio(500000.0);

        Carro carroGuardado = new Carro();
        carroGuardado.setId(1L);
        carroGuardado.setUserId(1L);
        carroGuardado.setProductId(100L);
        carroGuardado.setQuantity(2);
        carroGuardado.setProductName("Tarjeta Grafica RTX");
        carroGuardado.setUnitPrice(500000.0);

        // 2. Le decimos a los Mocks cómo deben comportarse
        when(catalogoClient.getProducto(eq(100L), anyString())).thenReturn(productoMock);
        when(inventarioClient.checkStock(eq(100L), eq(2), anyString())).thenReturn(true);
        when(carroRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(Optional.empty()); // Simulamos que es nuevo
        when(carroRepository.save(any(Carro.class))).thenReturn(carroGuardado);

        // 3. Ejecutamos el método real
        Carro resultado = carroService.addToCart(1L, 100L, 2, "Bearer tokenFalso");

        // 4. Verificamos que hizo lo correcto
        assertNotNull(resultado);
        assertEquals("Tarjeta Grafica RTX", resultado.getProductName());
        assertEquals(2, resultado.getQuantity());

        // Verificamos que los clientes externos fueron llamados
        verify(catalogoClient).getProducto(eq(100L), anyString());
        verify(inventarioClient).checkStock(eq(100L), eq(2), anyString());
        verify(carroRepository).save(any(Carro.class));
    }
}