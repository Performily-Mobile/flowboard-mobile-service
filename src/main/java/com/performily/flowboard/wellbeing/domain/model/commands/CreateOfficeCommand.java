package com.performily.flowboard.wellbeing.domain.model.commands;

public record CreateOfficeCommand(String name, String area, String address, String floor, String reference) {}
