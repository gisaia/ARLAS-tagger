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

import io.arlas.server.utils.RequestFieldsExtractor;
import io.arlas.tagger.model.request.TagRequest;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class TagRequestFieldsExtractor implements RequestFieldsExtractor.IRequestFieldsExtractor<TagRequest> {
    @Override
    public Stream<String> getCols(TagRequest tagRequest, Set<String> includeFields) {
        Stream<String> searchCols = tagRequest.search != null ?
                new RequestFieldsExtractor.SearchRequestFieldsExtractor().getCols(tagRequest.search, includeFields) : Stream.of();
        Stream<String> fCols = tagRequest.propagation != null && tagRequest.propagation.filter != null ?
                Optional.ofNullable(tagRequest.propagation.filter).flatMap(filter -> Optional.ofNullable(filter.f))
                .map(f -> f.stream().flatMap(fList -> fList.stream().map(fFilter -> fFilter.field))).orElse(Stream.of()) : Stream.of();
        Stream<String> propagationCol = tagRequest.propagation != null && tagRequest.propagation.field != null ?
                Stream.of(tagRequest.propagation.field) : Stream.of();
        Stream<String> tagCol = Stream.of(tagRequest.tag.path);
        return Stream.of(searchCols, fCols, propagationCol, tagCol).flatMap(x -> x);
    }
}
