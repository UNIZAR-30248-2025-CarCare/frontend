// En models/SearchResult.kt
package eina.unizar.frontend.models

import androidx.compose.ui.graphics.Color

sealed class SearchResult {
    abstract val id: Int
    abstract val titulo: String
    abstract val subtitulo: String
    abstract val tipo: TipoResultado
    abstract val fecha: String?

    data class ViajeResult(
        override val id: Int,
        override val titulo: String,
        override val subtitulo: String,
        override val fecha: String?,
        val viaje: Viaje
    ) : SearchResult() {
        override val tipo = TipoResultado.VIAJE
    }

    /*
    data class RepostajeResult(
        override val id: Int,
        override val titulo: String,
        override val subtitulo: String,
        override val fecha: String?,
        val repostaje: Repostaje
    ) : SearchResult() {
        override val tipo = TipoResultado.REPOSTAJE
    }
     */


    data class IncidenciaResult(
        override val id: Int,
        override val titulo: String,
        override val subtitulo: String,
        override val fecha: String?,
        val incidencia: IncidenciaDetalle
    ) : SearchResult() {
        override val tipo = TipoResultado.INCIDENCIA
    }

    data class ReservaResult(
        override val id: Int,
        override val titulo: String,
        override val subtitulo: String,
        override val fecha: String?,
        val reserva: ReservaDTO
    ) : SearchResult() {
        override val tipo = TipoResultado.RESERVA
    }

    data class RevisionResult(
        override val id: Int,
        override val titulo: String,
        override val subtitulo: String,
        override val fecha: String?,
        val revision: RevisionDTO
    ) : SearchResult() {
        override val tipo = TipoResultado.REVISION
    }
}

enum class TipoResultado(val displayName: String, val color: Color) {
    VIAJE("Viaje", Color(0xFF8B5CF6)),
    //REPOSTAJE("Repostaje", Color(0xFF10B981)),
    INCIDENCIA("Incidencia", Color(0xFFEF4444)),
    RESERVA("Reserva", Color(0xFF3B82F6)),
    REVISION("Revisión", Color(0xFFF59E0B))
}