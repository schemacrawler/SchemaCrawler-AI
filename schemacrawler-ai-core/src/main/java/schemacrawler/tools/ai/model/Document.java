package schemacrawler.tools.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;

public interface Document extends Serializable {

  @JsonProperty("name")
  String getName();

  ObjectNode toObjectNode();
}
