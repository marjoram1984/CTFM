package com.cl.ctfm.core.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Wrapper Response Object.
 * Wrapping data when sending response data.
 *
 * @author marjoram1984 (marjoram1984@gmail.com)
 */

class ResponseResults<T>(
        val resultCd: String? = "",
        val resultMsg: String? = "",
        val result: T? = null) {

    fun success(result: T): ResponseEntity<*> {
        return ResponseEntity<ResponseResults<*>>(ResponseResults(RESULT_SUCCESS_CD, RESULT_SUCCESS_MSG, result), HttpStatus.OK)
    }

    fun error(result: T): ResponseEntity<*> {
        return ResponseEntity<ResponseResults<*>>(ResponseResults(RESULT_FAILUE_CD, RESULT_FAILUE_MSG, result), HttpStatus.BAD_REQUEST)
    }

    companion object {
        val RESULT_SUCCESS_CD = "0000"                              // Success

        val RESULT_FAILUE_CD = "9999"                               // Unknown error
        val RESULT_NOTFOUND_USERNAME_CD = "9900"                    // Not found id or invalid id
        val RESULT_NOTFOUND_PASSWORD_CD = "9901"                    // Invalid id

        val RESULT_SUCCESS_MSG = "Success"

        val RESULT_FAILUE_MSG = "Invalid Error"
        val RESULT_NOTFOUND_USERNAME_MSG = "Not found username"
        val RESULT_NOTFOUND_PASSWORD_MSG = "Not found password"
    }
}
