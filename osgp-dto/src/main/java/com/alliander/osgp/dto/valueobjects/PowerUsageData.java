package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class PowerUsageData implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 7571950922988802598L;

    private DateTime recordTime;
    private MeterType meterType;
    private long totalConsumedEnergy;
    private long actualConsumedPower;

    private PsldData psldData;
    private SsldData ssldData;

    public PowerUsageData(final DateTime recordTime, final MeterType meterType, final long totalConsumedEnergy,
            final long actualConsumedPower) {
        this.recordTime = recordTime;

        if (meterType == null) {
            throw new IllegalArgumentException("Meter type is null.");
        } else {
            this.meterType = meterType;
        }

        this.totalConsumedEnergy = totalConsumedEnergy;
        this.actualConsumedPower = actualConsumedPower;
    }

    public DateTime getRecordTime() {
        return this.recordTime;
    }

    public MeterType getMeterType() {
        return this.meterType;
    }

    public long getTotalConsumedEnergy() {
        return this.totalConsumedEnergy;
    }

    public long getActualConsumedPower() {
        return this.actualConsumedPower;
    }

    public PsldData getPsldData() {
        return this.psldData;
    }

    public SsldData getSsldData() {
        return this.ssldData;
    }

    public void setPsldData(final PsldData psldData) {
        this.psldData = psldData;
    }

    public void setSsldData(final SsldData ssldData) {
        this.ssldData = ssldData;
    }
}