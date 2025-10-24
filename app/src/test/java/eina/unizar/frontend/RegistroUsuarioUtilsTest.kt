package eina.unizar.frontend

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitarios para funciones auxiliares
 */
class RegistroUtilsTest {

    /**
     * Test de validación de email válido
     */
    @Test
    fun testEmailValido() {
        assertTrue(esEmailValido("usuario@example.com"))
        assertTrue(esEmailValido("test.user+filter@domain.co.uk"))
        assertTrue(esEmailValido("user123@test-domain.com"))
    }

    /**
     * Test de validación de email inválido
     */
    @Test
    fun testEmailInvalido() {
        assertFalse(esEmailValido("usuariosindominio"))
        assertFalse(esEmailValido("@example.com"))
        assertFalse(esEmailValido("usuario@"))
        assertFalse(esEmailValido("usuario @example.com"))
    }

    /**
     * Test de cálculo de edad correcta
     */
    @Test
    fun testObtenerEdad_EdadCorrecta() {
        val edad = obtenerEdad("01/01/2000")
        assertNotNull(edad)
        assertTrue(edad!! >= 24) // Ajustar según la fecha actual
    }

    /**
     * Test de cálculo de edad con formato inválido
     */
    @Test
    fun testObtenerEdad_FormatoInvalido() {
        val edad = obtenerEdad("2000/01/01")
        assertNull(edad)
    }

    /**
     * Test de hash de contraseña
     */
    @Test
    fun testHashPassword() {
        val password = "password123"
        val hashed = hashPassword(password)

        assertNotNull(hashed)
        assertNotEquals(password, hashed)
        assertEquals(64, hashed.length) // SHA-256 produce 64 caracteres hexadecimales

        // Verificar que el mismo password produce el mismo hash
        assertEquals(hashed, hashPassword(password))
    }
}