/*
 Copyright 2021 Baloise Group

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.baloise.open.maven.codeql.sarif.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
public class Result {

  private String ruleId;
  private Integer ruleIndex;
  private String message;
  private List<Location> locations;

  @Override
  public String toString() {
    String result = "Found issue based on rule '%s': '%s'".formatted(ruleId, message);
    if (locations != null && !locations.isEmpty()) {
      result += "\nin '" + locations.get(0).toString() + "'";
    }
    return result;
  }

}
