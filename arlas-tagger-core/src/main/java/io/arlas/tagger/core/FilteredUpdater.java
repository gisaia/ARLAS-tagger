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

package io.arlas.tagger.core;

import io.arlas.commons.exceptions.ArlasException;
import io.arlas.commons.exceptions.BadRequestException;
import io.arlas.commons.exceptions.NotAllowedException;
import io.arlas.commons.exceptions.NotImplementedException;
import io.arlas.server.core.impl.elastic.services.ElasticFluidSearch;
import io.arlas.server.core.impl.elastic.utils.ElasticClient;
import io.arlas.server.core.model.CollectionReference;
import io.arlas.tagger.model.Tag;
import io.arlas.tagger.model.enumerations.Action;
import io.arlas.tagger.model.response.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class FilteredUpdater extends ElasticFluidSearch {
    private final ElasticClient client;
    public FilteredUpdater(CollectionReference collectionReference,
                           ElasticClient client,
                           int arlasElasticMaxPrecisionThreshold) {
        super(collectionReference, arlasElasticMaxPrecisionThreshold);
        this.client = client;
        this.setClient(client);
    }

    public UpdateResponse doAction(Action action,
                                   CollectionReference collectionReference,
                                   Tag tag,
                                   int max_updates,
                                   int slices) throws IOException, ArlasException {
        if(Strings.isNullOrEmpty(tag.path)){
            throw new BadRequestException("The tag path must be provided and must not be empty");
        }
        // The collection can be tagged on that field only if the path belongs to collectionReference.params.taggableFields
        if(Strings.isNullOrEmpty(collectionReference.params.taggableFields) || Arrays.stream(collectionReference.params.taggableFields.split(",")).noneMatch(f->tag.path.equals(f.trim()))){
            throw new NotAllowedException("The path " + tag.path + " is not part of the fields that can be tagged.");
        }

        UpdateByQueryRequest request = new UpdateByQueryRequest(collectionReference.params.indexName)
                .setQuery(this.getBoolQueryBuilder())
                .setMaxDocs(Math.min(collectionReference.params.updateMaxHits,max_updates))
                .setSlices(slices)
                .setScript(this.getTagScript(tag, action));
        BulkByScrollResponse response = this.client.getClient().updateByQuery(request, RequestOptions.DEFAULT);
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.failures.addAll(response.getSearchFailures()
                .stream().map(f -> new UpdateResponse.Failure(f.getIndex(), f.getReason().getMessage(), "SearchFailure")).toList());
        updateResponse.failures.addAll(response.getBulkFailures()
                .stream().map(f -> new UpdateResponse.Failure(f.getId(), f.getMessage(), "BulkFailure")).toList());
        updateResponse.failed = updateResponse.failures.size();
        updateResponse.updated = response.getUpdated();
        updateResponse.action = action;
        return updateResponse;
    }


    public Script getTagScript(Tag tag, Action action) throws BadRequestException, NotImplementedException {
        String script = "";
        if (action.equals(Action.ADD)) {
            script += """
                    if (ctx._source.%s == null) {
                        ctx._source.%s = new ArrayList();
                    }
                    """.formatted(tag.path, tag.path);
            script += """
                    if (!(ctx._source.%s instanceof List)) {
                        Object o = ctx._source.%s;
                        ctx._source.%s = new ArrayList();
                        ctx._source.%s.add(o)
                    }
                    """.formatted(tag.path, tag.path, tag.path, tag.path);
            if (tag.value == null || Strings.isNullOrEmpty(tag.value.toString())) {
                throw new BadRequestException("The tag value must be provided and must not be empty");
            }
            if (tag.value instanceof Number) {
                script += """
                if (!(ctx._source.%s.contains(%s))) {
                    ctx._source.%s.add(%s)
                }
                """.formatted(tag.path, tag.value, tag.path, tag.value);
            } else {
                script += """
                if (!(ctx._source.%s.contains('%s'))) {
                    ctx._source.%s.add('%s')
                }
                """.formatted(tag.path, tag.value.toString(), tag.path, tag.value.toString());
            }
        }

        if (action.equals(Action.REMOVE)) {
            if (tag.value == null) {
                throw new BadRequestException("The tag value must be provided and must not be empty");
            }

            if (tag.value instanceof Number) {
                throw new NotImplementedException("Removal of a number tag is not yet supported");
                //script+="\tctx._source."+tag.path+".remove("+tag.value+")\n";
            } else {
                if (Strings.isNullOrEmpty(tag.value.toString())) {
                    throw new BadRequestException("The tag value must be provided and must not be empty");
                }
                script += """
                if (ctx._source.%s != null) {
                    ctx._source.%s.removeAll(Collections.singleton("%s"))
                }
                """.formatted(tag.path, tag.path, tag.value.toString());
            }
        }

        if (action.equals(Action.REMOVEALL)) {
            script += "ctx._source.%s = null".formatted(tag.path);
        }
        return new Script(ScriptType.INLINE,"painless", script, Collections.emptyMap());
    }
}
