package com.disney.xband.xbrms.common.model;

public enum ProblemAreaType {
        JmsMessaging,
        ReadXbrmsConfig,
        ConnectToXbrmsDb,
        InsertMetricsData,
        RetrieveMetricsData,
        ProcessMessage,
        AssignReader,
        UpdateHealthItem,
        Networking,
        ParksConfig,
        DatabaseFull,
        IncorrectSchemaVersion,
        QueryTimeout,
        DataCorruption_FacilityId
}