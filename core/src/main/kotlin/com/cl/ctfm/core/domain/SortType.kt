package com.cl.ctfm.core.domain

/**
 * List sort data class.
 * field is sort target column.
 * direction is order type.
 *
 * @author marjoram1984 (marjoram1984@gmail.com)
 */
data class SortType(
        val field: String = "",
        val direction : String = ""
)