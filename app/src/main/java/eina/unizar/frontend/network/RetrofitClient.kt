package eina.unizar.frontend.network


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que configura y proporciona la instancia de Retrofit.
 * 
 * Centraliza la configuración del cliente HTTP y la instancia de la API.
 * Incluye logging de peticiones para facilitar el debugging durante el desarrollo.
 */
object RetrofitClient {

    /**
     * URL base del servidor backend.
     * 
     * NOTA: Esta es una dirección IP local. Debe actualizarse según el entorno:
     * - Desarrollo local: IP de la máquina que ejecuta el backend
     * - Producción: Dominio o IP del servidor en producción
     */
    private const val BASE_URL = "http://10.0.2.2:3000"

    /**
     * Cliente OkHttp configurado con interceptor de logging.
     * 
     * El interceptor registra el cuerpo completo de las peticiones y respuestas,
     * útil para debugging. Se inicializa de forma lazy (solo cuando se necesita).
     */
    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    /**
     * Instancia principal de ApiService.
     * 
     * Proporciona acceso a todos los endpoints definidos en la interfaz ApiService.
     * Configurado con:
     * - Conversión JSON mediante Gson
     * - Cliente HTTP con logging
     * - URL base del servidor
     * 
     * Se inicializa de forma lazy para optimizar recursos.
     */
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}