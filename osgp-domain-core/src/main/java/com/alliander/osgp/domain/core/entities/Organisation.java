/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * Organisation entitiy class
 */
@Entity
public class Organisation extends AbstractEntity {

    /**
     * SerialVersionID for serrialization
     */
    private static final long serialVersionUID = -1097307978466479033L;

    private static final int ORGANISATION_IDENTIFICATION_MAX_LENGTH = 40;
    private static final int NAME_MAX_LENGTH = 255;
    private static final int PREFIX_MAX_LENGTH = 3;
    private static final String SEPARATOR = ";";

    @Column(length = ORGANISATION_IDENTIFICATION_MAX_LENGTH, nullable = false, unique = true)
    @Identification
    private String organisationIdentification;

    @Column(length = NAME_MAX_LENGTH, nullable = false, unique = false)
    @NotEmpty
    @Length(max = NAME_MAX_LENGTH)
    private String name;

    @Column(length = PREFIX_MAX_LENGTH, nullable = false, unique = false)
    @NotEmpty
    @Length(max = PREFIX_MAX_LENGTH)
    private String prefix;

    @OneToMany(mappedBy = "organisation", targetEntity = DeviceAuthorization.class)
    @Cascade(value = { CascadeType.DELETE })
    private final List<DeviceAuthorization> authorizations = new ArrayList<DeviceAuthorization>();

    @Column(nullable = false)
    private PlatformFunctionGroup functionGroup;

    @Column(nullable = false)
    private boolean enabled;

    public Organisation() {
        // Default constructor
    }

    /**
     * Comma separated list of domains organisation is allowed to access.
     */
    @Column(nullable = false, length = NAME_MAX_LENGTH)
    @NotEmpty
    @Length(max = NAME_MAX_LENGTH)
    private String domains;

    public Organisation(final String organisationIdentification, final String name, final String prefix,
            final PlatformFunctionGroup functionGroup) {
        this.organisationIdentification = organisationIdentification;
        this.name = name;
        this.prefix = prefix;
        this.functionGroup = functionGroup;
    }

    /**
     * Gets the organisations identification
     * 
     * @return the identification
     */
    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    /**
     * Gets the organisations name
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the organisations prefix
     * 
     * @return the prefix
     */
    public String getPrefix() {
        return this.prefix;
    }

    public List<DeviceAuthorization> getAuthorizations() {
        return this.authorizations;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.authorizations == null) ? 0 : this.authorizations.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result
                + ((this.organisationIdentification == null) ? 0 : this.organisationIdentification.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Organisation other = (Organisation) obj;
        if (this.authorizations == null) {
            if (other.authorizations != null) {
                return false;
            }
        } else if (!this.authorizations.equals(other.authorizations)) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.prefix == null) {
            if (other.prefix != null) {
                return false;
            }
        } else if (!this.prefix.equals(other.prefix)) {
            return false;
        }
        if (this.organisationIdentification == null) {
            if (other.organisationIdentification != null) {
                return false;
            }
        } else if (!this.organisationIdentification.equals(other.organisationIdentification)) {
            return false;
        }
        if (this.functionGroup == null) {
            return false;
        } else if (!this.functionGroup.equals(other.functionGroup)) {
            return false;
        }
        if (this.domains == null) {
            if (other.domains != null) {
                return false;
            }
        } else if (!this.domains.equals(other.domains)) {
            return false;
        }
        return true;
    }

    public PlatformFunctionGroup getFunctionGroup() {
        return this.functionGroup;
    }

    public void changeOrganisationData(final String organisationIdentification, final String organisationName,
            final PlatformFunctionGroup platformFunctionGroup) {
        this.organisationIdentification = organisationIdentification;
        this.name = organisationName;
        this.functionGroup = platformFunctionGroup;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setIsEnabled(final boolean value) {
        this.enabled = value;
    }

    public void setDomains(final List<PlatformDomain> domains) {
        this.domains = StringUtils.EMPTY;

        // Check for empty list
        if (domains == null) {
            return;
        }

        for (final PlatformDomain domain : domains) {
            this.domains += domain + SEPARATOR;
        }
    }

    public void addDomain(final PlatformDomain domain) {
        if (this.domains == null) {
            this.domains = StringUtils.EMPTY;
        }

        this.domains += domain + SEPARATOR;
    }

    public List<PlatformDomain> getDomains() {
        final List<PlatformDomain> result = new ArrayList<PlatformDomain>();

        if (StringUtils.isEmpty(this.domains)) {
            return result;
        }

        final String[] splits = this.domains.split(SEPARATOR);
        for (final String s : splits) {
            result.add(PlatformDomain.valueOf(s));
        }

        return result;
    }
}
