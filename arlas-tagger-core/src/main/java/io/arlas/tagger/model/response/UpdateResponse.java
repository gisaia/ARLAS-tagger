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
import java.util.concurrent.atomic.AtomicLong;

public class UpdateResponse {
    public List<Failure> failures = new ArrayList<>();
    public long failed = 0;
    public long updated = 0;
    public float progress = 0f;
    public long nbResult = 0l;
    public AtomicLong nbRequest = new AtomicLong(0);
    public String id;
    public Action action;
    public long propagated = 0;
    public long startTime;
    public long endTime;
    public long processingTimeMs; // ms
    public String label;

    public UpdateResponse() {
        this.startTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis();
    }

    public static class Failure{
        public String id;
        public String message;
        public String type;
        public  Failure(){}
        public  Failure(String id, String message, String type){
            this.id = id;
            this.message = message;
            this.type = type;
        }
    }

    public void add(UpdateResponse r) {
        this.failures.addAll(r.failures);
        this.failed += r.failed;
        this.updated += r.updated;
        this.action = r.action;
        this.endTime = System.currentTimeMillis();
        this.processingTimeMs = endTime - startTime;
        this.nbRequest.getAndIncrement();
        this.progress = nbResult != 0 ? (float) nbRequest.get()*100/nbResult : 100f;
    }
}
