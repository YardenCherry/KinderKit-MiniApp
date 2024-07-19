package dev.netanelbcn.kinderkit.Interfaces;

import dev.netanelbcn.kinderkit.Models.ImmunizationRecord;

public interface DelRecordCallback {
    void deleteClicked(ImmunizationRecord record, int position);
}
