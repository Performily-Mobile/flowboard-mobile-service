package com.performily.flowboard.workspace.application.commandservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.workspace.domain.model.commands.ActivateAreaCommand;
import com.performily.flowboard.workspace.domain.model.commands.CreateAreaCommand;
import com.performily.flowboard.workspace.domain.model.commands.DeactivateAreaCommand;
import com.performily.flowboard.workspace.domain.model.commands.RenameAreaCommand;
import com.performily.flowboard.workspace.domain.model.entities.Area;

/**
 * Area Command Service
 * @summary
 * Application service contract for commands over the {@link Area} entity.
 *
 * @since 1.0.0
 */
public interface AreaCommandService {
    /**
     * Handles area creation.
     *
     * @param command command containing the area data
     * @return created area identifier or an application error
     */
    Result<Long, ApplicationError> handle(CreateAreaCommand command);

    /**
     * Handles area renaming.
     *
     * @param command command containing the area id and the new name
     * @return updated area or an application error
     */
    Result<Area, ApplicationError> handle(RenameAreaCommand command);

    /**
     * Handles area activation.
     *
     * @param command command containing the area id
     * @return updated area or an application error
     */
    Result<Area, ApplicationError> handle(ActivateAreaCommand command);

    /**
     * Handles area deactivation.
     *
     * @param command command containing the area id
     * @return updated area or an application error
     */
    Result<Area, ApplicationError> handle(DeactivateAreaCommand command);
}