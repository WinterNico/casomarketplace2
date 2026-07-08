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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        ProductoDTO productoMock = new ProductoDTO();
        productoMock.setNombre("Tarjeta Grafica RTX");
        productoMock.setPrecio(500000.0);

        Carro carroGuardado = new Carro();
        carroGuardado.setProductName("Tarjeta Grafica RTX");
        carroGuardado.setQuantity(2);

        when(catalogoClient.getProducto(eq(100L), anyString())).thenReturn(productoMock);
        when(inventarioClient.checkStock(eq(100L), eq(2), anyString())).thenReturn(true);
        when(carroRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(Optional.empty());
        when(carroRepository.save(any(Carro.class))).thenReturn(carroGuardado);

        Carro resultado = carroService.addToCart(1L, 100L, 2, "Bearer tokenFalso");

        assertNotNull(resultado);
        assertEquals("Tarjeta Grafica RTX", resultado.getProductName());
        verify(carroRepository).save(any(Carro.class));
    }

    @Test
    void deberiaActualizarCantidadSiProductoYaExisteEnElCarro() {
        ProductoDTO productoMock = new ProductoDTO();
        productoMock.setNombre("Tarjeta Grafica RTX");
        productoMock.setPrecio(500000.0);

        Carro itemExistente = new Carro();
        itemExistente.setQuantity(1);

        when(catalogoClient.getProducto(eq(100L), anyString())).thenReturn(productoMock);
        when(inventarioClient.checkStock(eq(100L), eq(2), anyString())).thenReturn(true);
        when(carroRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(Optional.of(itemExistente));
        when(carroRepository.save(any(Carro.class))).thenReturn(itemExistente);

        carroService.addToCart(1L, 100L, 2, "Bearer tokenFalso");

        assertEquals(3, itemExistente.getQuantity());
        verify(carroRepository).save(itemExistente);
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExisteEnCatalogo() {
        when(catalogoClient.getProducto(eq(999L), anyString())).thenThrow(new RuntimeException("Not found"));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            carroService.addToCart(1L, 999L, 1, "Bearer tokenFalso");
        });

        assertEquals("El producto no existe en el catálogo.", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoNoHayStock() {
        ProductoDTO productoMock = new ProductoDTO();
        when(catalogoClient.getProducto(eq(100L), anyString())).thenReturn(productoMock);
        when(inventarioClient.checkStock(eq(100L), eq(5), anyString())).thenReturn(false);


        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            carroService.addToCart(1L, 100L, 5, "Bearer tokenFalso");
        });

        assertEquals("No hay stock suficiente para el producto: 100", excepcion.getMessage());
    }


    @Test
    void deberiaCalcularTotalDelCarro() {
        // GIVEN
        Carro item1 = new Carro();
        item1.setUnitPrice(1000.0);
        item1.setQuantity(2);

        Carro item2 = new Carro();
        item2.setUnitPrice(500.0);
        item2.setQuantity(3);

        when(carroRepository.findByUserId(1L)).thenReturn(Arrays.asList(item1, item2));

        Double total = carroService.getCartTotal(1L);

        assertEquals(3500.0, total);
    }


    @Test
    void deberiaObtenerCarroPorUserId() {
        Carro item = new Carro();
        when(carroRepository.findByUserId(1L)).thenReturn(Arrays.asList(item));

        List<Carro> resultado = carroService.getCartByUserId(1L);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }


    @Test
    void deberiaEliminarItemDelCarro() {
        when(carroRepository.existsById(10L)).thenReturn(true);
        doNothing().when(carroRepository).deleteById(10L);

        assertDoesNotThrow(() -> carroService.removeFromCart(10L));
        verify(carroRepository, times(1)).deleteById(10L);
    }

    @Test
    void deberiaLanzarExcepcionAlEliminarItemInexistente() {
        when(carroRepository.existsById(99L)).thenReturn(false);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            carroService.removeFromCart(99L);
        });

        assertEquals("El ítem a eliminar no existe.", excepcion.getMessage());
    }
}