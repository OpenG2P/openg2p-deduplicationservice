package org.openg2p.searchservice.exceptions;

import org.springframework.http.HttpStatus

open class ValidationException(reason: String) : ApiException(HttpStatus.BAD_REQUEST, reason)