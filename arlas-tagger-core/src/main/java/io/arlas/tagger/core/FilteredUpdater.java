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

import io.arlas.server.core.exceptions.ArlasException;
import io.arlas.server.core.exceptions.BadRequestException;
import io.arlas.server.core.exceptions.NotImplementedException;
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
import java.util.stream.Collectors;

public class FilteredUpdater extends ElasticFluidSearch {
    private ElasticClient client;
    public FilteredUpdater(CollectionReference collectionReference, ElasticClient client) {
        super(collectionReference);
        this.client = client;
        this.setClient(client);
    }

    public UpdateResponse doAction(Action action, CollectionReference collectionReference, Tag tag, int max_updates) throws IOException, ArlasException {
        return doAction(action, collectionReference, tag, max_updates, 1);
    }

    public UpdateResponse doAction(Action action, CollectionReference collectionReference, Tag tag, int max_updates, int slices) throws IOException, ArlasException {
        if(Strings.isNullOrEmpty(tag.path)){
            throw new io.arlas.server.core.exceptions.BadRequestException("The tag path must be provided and must not be empty");
        }
        // The collection can be tagged on that field only if the path belongs to collectionReference.params.taggableFields
        if(Strings.isNullOrEmpty(collectionReference.params.taggableFields) || Arrays.stream(collectionReference.params.taggableFields.split(",")).noneMatch(f->tag.path.equals(f.trim()))){
            throw new io.arlas.server.core.exceptions.NotAllowedException("The path "+tag.path+" is not part of the fields that can be tagged.");
        }

        UpdateByQueryRequest request = new UpdateByQueryRequest(collectionReference.params.indexName)
                .setQuery(this.getBoolQueryBuilder())
                .setMaxDocs(Math.min(collectionReference.params.updateMaxHits,max_updates))
                .setSlices(slices)
                .setScript(this.getTagScript(tag, action));
        BulkByScrollResponse response = this.client.getClient().updateByQuery(request, RequestOptions.DEFAULT);
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.failures.addAll(response.getSearchFailures()
                .stream().map(f->new UpdateResponse.Failure(f.getIndex(),f.getReason().getMessage(),"SearchFailure")).collect(Collectors.toList()));
        updateResponse.failures.addAll(response.getBulkFailures()
                .stream().map(f->new UpdateResponse.Failure(f.getId(),f.getMessage(),"BulkFailure")).collect(Collectors.toList()));
        updateResponse.failed = updateResponse.failures.size();
        updateResponse.updated=response.getUpdated();
        updateResponse.action=action;
        return updateResponse;
    }


    public Script getTagScript(Tag tag, Action action) throws BadRequestException, NotImplementedException {
        String script="";
        if(action.equals(Action.ADD)){
            script+="if (ctx._source."+tag.path+" == null) {\n" +
                    "\tctx._source."+tag.path+" = new ArrayList(); \n" +
                    "}\n";
            script+="if (!(ctx._source."+tag.path+" instanceof List)) {\n" +
                    "\tObject o = ctx._source."+tag.path+"; \n"+
                    "\tctx._source."+tag.path+" = new ArrayList(); \n" +
                    "\tctx._source."+tag.path+".add(o)\n"+
                    "}\n";
            if(tag.value==null && Strings.isNullOrEmpty(tag.value.toString())){
                throw new io.arlas.server.core.exceptions.BadRequestException("The tag value must be provided and must not be empty");
            }
            if(tag.value instanceof Number){
                script+="if (!(ctx._source."+tag.path+".contains("+tag.value+"))) {\n";
                script+="ctx._source."+tag.path+".add("+tag.value+")\n";
                script+="}\n";
            }else{
                script+="if (!(ctx._source."+tag.path+".contains('"+tag.value.toString()+"'))) {\n";
                script+="ctx._source."+tag.path+".add('"+tag.value.toString()+"')\n";
                script+="}\n";
            }
        }
        if(action.equals(Action.REMOVE)){
            if(tag.value==null){
                throw new io.arlas.server.core.exceptions.BadRequestException("The tag value must be provided and must not be empty");
            }
            script+="if (ctx._source."+tag.path+" != null) {\n";

            if(tag.value instanceof Number){
                throw new io.arlas.server.core.exceptions.NotImplementedException("Removal of a number tag is not yet supported");
                //script+="\tctx._source."+tag.path+".remove("+tag.value+")\n";
            }else{
                if(Strings.isNullOrEmpty(tag.value.toString())){
                    throw new io.arlas.server.core.exceptions.BadRequestException("The tag value must be provided and must not be empty");
                }
                script+="ctx._source."+tag.path+".removeAll(Collections.singleton(\""+tag.value.toString()+"\"))\n";
            }
            script+="}\n";
        }
        if(action.equals(Action.REMOVEALL)){
            script+="ctx._source."+tag.path+" = null";
        }
        return new Script(ScriptType.INLINE,"painless", script, Collections.emptyMap());
    }
}
