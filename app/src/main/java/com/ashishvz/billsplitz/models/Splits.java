package com.ashishvz.billsplitz.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Splits implements Serializable {
    public Double splitAmount;
    public Double totalAmount;
    public String name;
    public String createdBy;
    public Map<String, Boolean> splitStatus;
    public Long createdAt;
    public Boolean isUneven;
    public List<UnevenSplit> unevenSplits;

    public List<SplitStatus> getSplitStatusToList() {
        if (splitStatus != null) {
            List<SplitStatus> splitStatuses = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : splitStatus.entrySet()) {
                splitStatuses.add(new SplitStatus(entry.getKey(), entry.getValue()));
            }
            return splitStatuses;
        }
        return null;
    }
}
