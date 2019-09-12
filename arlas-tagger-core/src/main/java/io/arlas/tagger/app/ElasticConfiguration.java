/*
 * Licensed to Gisaïa under one or more contributor
 * license agreements. See the NOTICE.txt file distributed with
 * this work for additional information regarding copyright
 * ownership. Gisaïa licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.arlas.tagger.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.arlas.server.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ElasticConfiguration {
    @JsonProperty("elastic_nodes")
    public String elasticnodes;

    @JsonProperty("elastic_sniffing")
    public Boolean elasticsniffing;

    @JsonProperty("elastic_cluster")
    public String elasticcluster;

    public static List<Pair<String,Integer>> getElasticNodes(String esNodes) {
        List<Pair<String,Integer>> elasticNodes = new ArrayList<>();
        if(!StringUtil.isNullOrEmpty(esNodes)) {
            String[] nodes = esNodes.split(",");
            for(String node : nodes) {
                String[] hostAndPort = node.split(":");
                if(hostAndPort.length == 2 && StringUtils.isNumeric(hostAndPort[1])) {
                    elasticNodes.add(new ImmutablePair<>(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
                }
            }
        }
        return elasticNodes;
    }

    public List<Pair<String,Integer>> getElasticNodes() {
        List<Pair<String,Integer>> elasticNodes = new ArrayList<>();
        if(!StringUtil.isNullOrEmpty(elasticnodes)) {
            elasticNodes.addAll(getElasticNodes(elasticnodes));
        }
        return elasticNodes;
    }
}
