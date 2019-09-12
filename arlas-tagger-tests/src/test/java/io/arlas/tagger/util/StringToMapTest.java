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

package io.arlas.tagger.util;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class StringToMapTest {

    @Test
    public void stringWithMultiplePropsToMapTest() {
        Map<String, String> extraProperties = StringToMap.parse("ssl.endpoint.identification.algorithm=https," +
                "security.protocol=SASL_SSL," +
                "sasl.mechanism=PLAIN," +
                "sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username=\"myKey\" password=\"mySecret\";");
        assertThat(extraProperties.get("ssl.endpoint.identification.algorithm"), equalTo("https"));
        assertThat(extraProperties.get("security.protocol"), equalTo("SASL_SSL"));
        assertThat(extraProperties.get("sasl.mechanism"), equalTo("PLAIN"));
        assertThat(extraProperties.get("sasl.jaas.config"), equalTo("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"myKey\" password=\"mySecret\";"));
    }

    @Test
    public void stringWithAnEmptyPropToMapTest() {
        Map<String, String> extraProperties = StringToMap.parse("ssl.endpoint.identification.algorithm=https," +
                "security.protocol");
        assertThat(extraProperties.get("ssl.endpoint.identification.algorithm"), equalTo("https"));
        assertThat(extraProperties.get("security.protocol"), equalTo(""));
    }

    @Test
    public void stringWithSinglePropToMapTest() {
        Map<String, String> extraProperties = StringToMap.parse(
                "sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username=\"myKey\" password=\"mySecret\";");
        assertThat(extraProperties.get("sasl.jaas.config"), equalTo("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"myKey\" password=\"mySecret\";"));
    }

    @Test
    public void emptyStringToMapTest() {
        Map<String, String> extraProperties = StringToMap.parse("");
        assertThat(extraProperties.size(), equalTo(0));
    }

    @Test
    public void nullStringToMapTest() {
        Map<String, String> extraProperties = StringToMap.parse(null);
        assertThat(extraProperties.size(), equalTo(0));
    }
}
