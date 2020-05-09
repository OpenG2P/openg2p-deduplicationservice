package org.openg2p.searchservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class ApiException : ResponseStatusException {
    constructor(status: HttpStatus) : super(status)
    constructor(status: HttpStatus, reason: String) : super(status, reason)
    constructor(status: HttpStatus, reason: String, cause: Throwable) : super(status, reason, cause)
}