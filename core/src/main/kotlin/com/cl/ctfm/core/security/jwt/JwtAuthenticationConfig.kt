package com.cl.ctfm.core.jwt

import org.springframework.beans.factory.annotation.Value

/**
 * JWT Token인증 방식에 사용될 속성 정보를 관리하는 클래스
 *
 * @author marjoram1984 (marjoram1984@gmail.com)
 */
class JwtAuthenticationConfig() {

    // 인증에 사용될 URL.
    @Value("\${glp.security.jwt.url:/login}")
    val url: String? = null

    // JWT정보를 가질 해더의 이름.
    @Value("\${glp.security.jwt.header:Authorization}")
    val header: String? = null

    // JWT Token 앞에 Prefix로 붙일 이름 Default
    @Value("\${glp.security.jwt.prefix:Bearer}")
    val prefix: String? = null

    // JWT 인증 유효시간 Default : 1Day
    @Value("\${glp.security.jwt.expiration:#{24*60*60}}")
    val expiration: Int = 0

    // JWT 토큰 생성에 필요한 Secret키
    @Value("\${glp.security.jwt.secret}")
    val secret: String? = null
}
