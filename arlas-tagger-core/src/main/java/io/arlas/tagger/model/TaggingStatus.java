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

package io.arlas.tagger.model;

import io.arlas.tagger.model.request.TagRefRequest;
import io.arlas.tagger.model.response.UpdateResponse;
import io.arlas.tagger.util.SelfExpiringHashMap;
import io.arlas.tagger.util.SelfExpiringMap;

import java.util.Optional;

public class TaggingStatus {
    private static final TaggingStatus INSTANCE = new TaggingStatus();

    private final SelfExpiringMap<String, UpdateResponse> statusMap;

    private volatile boolean doReset;

    private TaggingStatus() {
        statusMap = new SelfExpiringHashMap<>();
    }

    public Optional<UpdateResponse> getStatus(String id) {
        return Optional.ofNullable(statusMap.get(id));
    }

    public static TaggingStatus getInstance() {
        return INSTANCE;
    }

    public void initStatus(String id, UpdateResponse status, long timeout) {
        statusMap.put(id, status, timeout);
    }

    public synchronized UpdateResponse updateStatus(TagRefRequest tagRequest, UpdateResponse updResp, boolean incrNbRequest, long statusTimeout) {
        if (doReset) {
            statusMap.clear();
            doReset = false;
        }
        UpdateResponse updateResponse = getStatus(tagRequest.id).orElseGet(UpdateResponse::new);
        updateResponse.id = tagRequest.id;
        updateResponse.label = tagRequest.label;
        updateResponse.action = tagRequest.action;
        updateResponse.propagated = tagRequest.propagated;
        updateResponse.add(updResp, incrNbRequest);
        statusMap.put(tagRequest.id, updateResponse, statusTimeout);
        return updateResponse;
    }

    public void reset() {
        doReset = true;
    }
}