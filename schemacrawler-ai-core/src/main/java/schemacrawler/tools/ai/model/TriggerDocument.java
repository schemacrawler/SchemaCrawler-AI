/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.model;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Trigger;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
  "name",
  "action-condition",
  "action-statement",
  "action-orientation",
  "condition-timing",
  "event-manipulation"
})
public final class TriggerDocument implements Document {

  @Serial private static final long serialVersionUID = 1873929712139211255L;

  private final String triggerName;
  private final String actionCondition;
  private final String actionStatement;
  private final int actionOrder;
  private final String actionOrientation;
  private final String conditionTiming;
  private final List<String> eventManipulationType;

  public TriggerDocument(final Trigger trigger) {
    Objects.requireNonNull(trigger, "No table provided");

    triggerName = trigger.getName();
    actionCondition = trigger.getActionCondition();
    actionStatement = trigger.getActionStatement();
    actionOrder = trigger.getActionOrder();
    actionOrientation = trigger.getActionOrientation().toString();
    conditionTiming = trigger.getConditionTiming().toString();
    eventManipulationType =
        trigger.getEventManipulationTypes().stream()
            .map(EventManipulationType::toString)
            .collect(Collectors.toList());
  }

  public String getActionCondition() {
    return actionCondition;
  }

  public int getActionOrder() {
    return actionOrder;
  }

  public String getActionOrientation() {
    return actionOrientation;
  }

  public String getActionStatement() {
    return actionStatement;
  }

  public String getConditionTiming() {
    return conditionTiming;
  }

  public List<String> getEventManipulationType() {
    return eventManipulationType;
  }

  @Override
  public String getName() {
    return triggerName;
  }

  @Override
  public ObjectNode toObjectNode() {
    return mapper.valueToTree(this);
  }

  @Override
  public String toString() {
    return toObjectNode().toString();
  }
}
