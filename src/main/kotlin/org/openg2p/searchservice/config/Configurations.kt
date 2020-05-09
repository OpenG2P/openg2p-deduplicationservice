package org.openg2p.searchservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ConstructorBinding
@ConfigurationProperties(prefix = "searchservice")
@Validated
data class Configurations(

    /**
     * Attributes that query is allowed against.
     * New backends may need to add to this list. E.g. a fingerprint recognition library may add image
     * WARNING! DO NOT CHANGE EXCEPT YOU KNOW WHAT YOU ARE DOING!
     */
    @Valid @NotNull val allowedQueryAttributes: List<String>
    = listOf(
        "id",
        "identity",
        "first_name",
        "middle_name",
        "last_name",
        "phone",
        "email",
        "street",
        "street2",
        "city",
        "state",
        "postal_code",
        "dob",
        "location",
        "bank",
        "bank_account",
        "emergency_contact_name",
        "emergency_contact_phone"
    ),

    @Valid @NotNull val elastic: Elastic
) {
    data class Elastic(
        /**
         * Elastic search index.
         * WARNING! DO NOT CHANGE EXCEPT YOU KNOW WHAT YOU ARE DOING!
         */
        @NotBlank val index: String = "openg2p_beneficiaries",
        @NotBlank  val endpoint: String,

        /**
         * Attributes sent to the elastic backend
         * WARNING! DO NOT CHANGE EXCEPT YOU KNOW WHAT YOU ARE DOING!
         */
        @Valid @NotNull val attributes: List<String>
        = listOf(
            "id",
            "identity",
            "first_name",
            "middle_name",
            "last_name",
            "phone",
            "email",
            "street",
            "street2",
            "city",
            "state",
            "postal_code",
            "dob",
            "location",
            "bank",
            "bank_account",
            "emergency_contact_name",
            "emergency_contact_phone"
        )
    )
}