/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.Organisation;

/**
 * Organisation repository interface
 */
@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    Organisation findByOrganisationIdentification(String organisationIdentification);

    List<Organisation> findByEnabled(boolean enabled);
}
