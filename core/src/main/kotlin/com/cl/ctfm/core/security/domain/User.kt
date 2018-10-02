package com.cl.ctfm.core.security.domain

/**
  * This class is used for request login
 * @property username: Login id
 * @property password: Login password
 * @author marjoram1984 (marjoram1984@gmail.com)
 */
data class User(val username: String="", val password: String="")