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

package io.arlas.tagger.model.response;

import io.arlas.tagger.model.enumerations.Action;

import java.util.ArrayList;
import java.util.List;

public class UpdateResponse {
    public String id;
    public String label;
    public Action action;
    public List<Failure> failures = new ArrayList<>();
    public long failed = 0l;
    public long updated = 0l;
    public float progress = 0f;
    public long nbRequest = 0l;
    public long propagated = -1l;
    public long startTime;
    public long endTime;
    public long processingTimeMs = 0l; // ms

    public UpdateResponse() {
        this.startTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis();
    }

    public static class Failure {
        public String id;
        public String message;
        public String type;
        public Failure(){}
        public Failure(String id, String message, String type){
            this.id = id;
            this.message = message;
            this.type = type;
        }
    }

    public void add(UpdateResponse r, boolean incrementNbRequest) {
        this.failures.addAll(r.failures);
        this.failed += r.failed;
        this.updated += r.updated;
        this.action = r.action;
        this.endTime = System.currentTimeMillis();
        this.processingTimeMs = endTime - startTime;
        if (incrementNbRequest) this.nbRequest++;
        if (propagated == -1l) { // not started yet
            this.progress = 0f;
        } else if (propagated == 0l) { // no tag request match
            this.progress = 100f;
        } else {
            this.progress = (float) nbRequest * 100 / propagated;
        }
    }
}
