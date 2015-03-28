package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RelayConfiguration implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 7065046304131439924L;

    @Valid
    @Size(min = 1, max = 6)
    @NotNull
    private List<RelayMap> relayMap;

    public RelayConfiguration(final List<RelayMap> relayMap) {
        this.relayMap = new ArrayList<>(relayMap);
    }

    public List<RelayMap> getRelayMap() {
        return this.relayMap;
    }
}