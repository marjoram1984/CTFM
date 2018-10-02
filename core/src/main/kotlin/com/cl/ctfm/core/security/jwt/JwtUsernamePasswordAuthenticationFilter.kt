package com.cl.ctfm.core.jwt

import com.cl.ctfm.core.response.ResponseResults
import com.cl.ctfm.core.security.domain.User
import com.cl.ctfm.core.security.domain.UserDetail
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.time.Instant
import java.util.*
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 인증시 토큰을 생성하고 응답 해더에 해당 토큰 값을 저장하는 Filter
 * AbstractAuthenticationProcessingFilter는 인증요청시 최초로 호출되는 Filter이다.
 *
 * @property config jwt관련 설정정보를 갖는 변수, Application.yml
 * @author marjoram1984 (marjoram1984@gmail.com)
 */
class JwtUsernamePasswordAuthenticationFilter(val config: JwtAuthenticationConfig, val authManager: AuthenticationManager)
    : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(config.url!!, "POST")) {

    private val mapper: ObjectMapper

    init {
        setAuthenticationManager(authManager)
        this.mapper = ObjectMapper()
    }

    /**
     *  실제 인증을 실행하는 ovverride 메소드
     */
    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse): Authentication {
        val u: User = mapper.readValue(req.inputStream, User::class.java)
        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(u.username, u.password, emptyList()))
    }

    /**
     *  인증 성공시 호출되는 부분, JWT토큰을 만들어서 Response Header과 cookie에 넣는다.
     */
    override fun successfulAuthentication(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain?, auth: Authentication) {
        val now = Instant.now()
        val user: UserDetail = auth.principal as UserDetail;

        // 토큰 생성
        val token = Jwts.builder()
                .setSubject(auth.name)
                .claim("id", user.no)
                .claim(config.header, auth.authorities.stream().map<String>(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(Date.from(now))    // 토큰 발생시간 등록
                .setExpiration(Date.from(now.plusSeconds(config.expiration.toLong())))  //토큰 만료시간 등록
                .signWith(SignatureAlgorithm.HS256, config.secret!!.toByteArray())
                .compact()

        // 토큰값을 header와 cookie에 저장
        res.addHeader(config.header, config.prefix + " " + token)
        val cookie = Cookie(config.secret, token)
        cookie.secure = false   // [todo] : https로 전환후 해당 secure true로 변경해야 한다.
        cookie.maxAge = config.expiration
        cookie.path = "/"
        res.addCookie(cookie)
    }

    /**
     * 인증 실패시 실패 json 반환
     */
    override fun unsuccessfulAuthentication(req: HttpServletRequest, res: HttpServletResponse, e: AuthenticationException) {
        val gson = Gson()

        // ID가 존재하지 않을 경우
        if(e is AuthenticationServiceException) {
            res.writer.print(gson.toJson(ResponseResults(ResponseResults.RESULT_NOTFOUND_USERNAME_CD, ResponseResults.RESULT_NOTFOUND_PASSWORD_MSG, "아이디가 존재하지 않습니다.")))
        }else if(e is BadCredentialsException) {    // 패스워드가 존재하지 않을 경우
            res.writer.print(gson.toJson(ResponseResults(ResponseResults.RESULT_NOTFOUND_PASSWORD_CD, ResponseResults.RESULT_NOTFOUND_PASSWORD_MSG, "아이디가 존재하지 않습니다.")))
        } else {    // 알 수 없는 오류
            res.writer.print(gson.toJson(ResponseResults(ResponseResults.RESULT_FAILUE_CD, ResponseResults.RESULT_FAILUE_MSG, "알수없는 오류입니다.")))
        }

        res.writer.flush()
    }
}
