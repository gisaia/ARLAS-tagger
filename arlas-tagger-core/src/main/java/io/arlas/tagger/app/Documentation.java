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

public class Documentation  {

    public static final String TAG_OPERATION = "Search and tag the elements found in the collection, given the filters";
    public static final String TAG_REPLAY = "Scan the tagref topic and replay tagging operations from the given offset";
    public static final String TAG_REPLAY_PARAM_OFFSET = "The offset from which the replay must be done.";
    public static final String UNTAG_OPERATION = "Search and untag the elements found in the collection, given the filters";
    public static final String TAGSTATUS_OPERATION = "Get the status of the (un)tagging operation, given the id of a previously requested operation";
    public static final String TAGLIST_OPERATION = "Get the list of previously submitted tag requests";
    public static final String TAGSTATUS_PARAM_ID = "The id of a previously requested (un)tag operation.";
    public static final String FORM_PRETTY = "Pretty print";

}
