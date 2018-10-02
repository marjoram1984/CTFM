package com.cl.ctfm.core.jwt

import com.cl.ctfm.core.jwt.JwtAuthenticationConfig
import io.jsonwebtoken.Jwts
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * 해더에 존재하는 JWT 값을 해석해서 정보를 가져온후 SecurityContext에 등록하는 작업을 진행한다.
 * 즉 해더의 정보를 가지고 해당정보로 인증을 한다. 인증에 성공하면 요청한 페이지로 간다. 만약 인증 실패시 ZUUL의 인증 절차를 따르게 된다.
 * OnecePerRequestFilter : 요청당 1번씩 호출 되는 Filter
 *
 * @property config jwt관련 설정정보를 갖는 변수, Application.yml
 * @author marjoram1984 (marjoram1984@gmail.com)
 */
class JwtTokenAuthenticationFilter(val config: JwtAuthenticationConfig) : OncePerRequestFilter() {
    override fun doFilterInternal(req: HttpServletRequest, rsp: HttpServletResponse, filterChain: FilterChain) {
        var token: String? = req.getHeader(config.header)
        if(token != null && token.startsWith(config.prefix + " ")) {
            token = token.replace(config.prefix + " ", "")

            try {
                val claims = Jwts.parser()
                        .setSigningKey(config.secret!!.toByteArray())
                        .parseClaimsJws(token)
                        .getBody()
                val username = claims.getSubject()
                val authorities = claims.get("authorities", List::class.java)

                if (username != null) {
                    val auth = UsernamePasswordAuthenticationToken(username, null, authorities.map { SimpleGrantedAuthority(it as String?) })
                    SecurityContextHolder.getContext().authentication = auth
                }
            } catch (ignore: Exception) {
                SecurityContextHolder.clearContext()
            }
        }
        filterChain.doFilter(req, rsp)
    }
}
