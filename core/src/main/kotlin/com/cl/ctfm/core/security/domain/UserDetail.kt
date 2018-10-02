package com.cl.ctfm.core.security.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

/**
 * 인증 성공 후 세션에서 들고 다닐 데이터를 가질 org.springframework.security.core.userdetails
 * Data is carry in session after successful authentication
 * @property no: 로그인한 계정의 일련번호
 * @property loginId: 로그인 아이디
 * @property pswd: 로그인 패스워드
 * @author marjoram1984 (marjoram1984@gmail.com)
 */
class UserDetail : User {
    var no : Long? = 0;
    var loginId : String? = null
    var pswd: String? = null

    constructor(no:Long, loginId: String, password: String,
                   enabled: Boolean, accountNonExpired: Boolean, credentialsNonExpired: Boolean, accountNonLocked: Boolean, authorities: Collection<GrantedAuthority>)
            : super(loginId, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities) {

        this.no = no
        this.loginId = loginId
        this.pswd = pswd
    }
}