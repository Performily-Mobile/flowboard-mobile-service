package com.performily.flowboard.benefits.application.queryservices.views;

import com.performily.flowboard.benefits.domain.model.entities.VacationMovement;

/**
 * A movement with the name of its author, when it has one (manual adjustments).
 */
public record VacationMovementView(VacationMovement movement, String authorName) {
}
