package schemacrawler.tools.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import tools.jackson.databind.node.ObjectNode;

public interface Document extends Serializable {

  @JsonProperty("name")
  String getName();

  ObjectNode toObjectNode();
}
