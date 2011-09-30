/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.json.viewer.resources.domaintypes;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.viewer.json.applib.JsonRepresentation;
import org.apache.isis.viewer.json.applib.RepresentationType;
import org.apache.isis.viewer.json.applib.RestfulMediaType;
import org.apache.isis.viewer.json.applib.domaintypes.DomainTypeResource;
import org.apache.isis.viewer.json.viewer.representations.LinkReprBuilder;
import org.apache.isis.viewer.json.viewer.resources.ResourceAbstract;

/**
 * Implementation note: it seems to be necessary to annotate the implementation with {@link Path} rather than the
 * interface (at least under RestEasy 1.0.2 and 1.1-RC2).
 */
@Path("/domainTypes")
public class DomainTypeResourceServerside extends ResourceAbstract implements DomainTypeResource {


    @GET
    @Path("/")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_DOMAIN_TYPES })
    public Response domainTypes() {
        init();

        JsonRepresentation representation = JsonRepresentation.newMap();
        representation.mapPut("self", LinkReprBuilder.newBuilder(getResourceContext(), "self", "domainTypes").render());
        
        JsonRepresentation specList = JsonRepresentation.newArray();
        final Collection<ObjectSpecification> allSpecifications = getSpecificationLoader().allSpecifications();
        for (ObjectSpecification objectSpec : allSpecifications) {
            final LinkReprBuilder linkBuilder = LinkReprBuilder.newBuilder(getResourceContext(), "domainType", "domainTypes/%s", objectSpec.getFullIdentifier());
            specList.arrayAdd(linkBuilder.render());
        }
        
        representation.mapPut("domainTypes", specList);
        
        return responseOfOk(RepresentationType.DOMAIN_TYPE, Caching.ONE_DAY, representation).build();
    }

    
    @GET
    @Path("/{domainType}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_DOMAIN_TYPE })
    public Response domainType(@PathParam("domainType") final String domainType){
        init();

        JsonRepresentation representation = JsonRepresentation.newMap();
        representation.mapPut("self", LinkReprBuilder.newBuilder(getResourceContext(), "self", "domainTypes/%s", domainType).render());
        
        return responseOfOk(RepresentationType.DOMAIN_TYPE, Caching.ONE_DAY, representation).build();
    }

    @GET
    @Path("/{domainType}/properties/{propertyId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_TYPE_PROPERTY })
    public Response typeProperty(
            @PathParam("domainType") final String domainType,
            @PathParam("propertyId") final String propertyId){
        init();

        JsonRepresentation representation = JsonRepresentation.newMap();
        representation.mapPut("self", LinkReprBuilder.newBuilder(getResourceContext(), "self", "domainTypes/%s/properties/%s", domainType, propertyId).render());
        
        return responseOfOk(RepresentationType.TYPE_PROPERTY, Caching.ONE_DAY, representation).build();
    }

    @GET
    @Path("/{domainType}/collections/{collectionId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_TYPE_COLLECTION })
    public Response typeCollection(
            @PathParam("domainType") final String domainType,
            @PathParam("collectionId") final String collectionId){
        init();

        JsonRepresentation representation = JsonRepresentation.newMap();
        representation.mapPut("self", LinkReprBuilder.newBuilder(getResourceContext(), "self", "domainType/%s/collections/%s", domainType, collectionId).render());
        
        return responseOfOk(RepresentationType.TYPE_COLLECTION, Caching.ONE_DAY, representation).build();
    }

    @GET
    @Path("/{domainType}/actions/{actionId}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_TYPE_ACTION })
    public Response typeAction(
            @PathParam("domainType") final String domainType,
            @PathParam("actionId") final String actionId){
        init();

        JsonRepresentation representation = JsonRepresentation.newMap();
        representation.mapPut("self", LinkReprBuilder.newBuilder(getResourceContext(), "self", "domainTypes/%s/actions/%s", domainType, actionId).render());
        
        return responseOfOk(RepresentationType.TYPE_ACTION, Caching.ONE_DAY, representation).build();
    }

    @GET
    @Path("/{domainType}/actions/{actionId}/params/{paramName}")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_TYPE_ACTION_PARAMETER })
    public Response typeActionParam(
            @PathParam("domainType") final String domainType,
            @PathParam("actionId") final String actionId,
            @PathParam("paramName") final String paramName){
        init();

        JsonRepresentation representation = JsonRepresentation.newMap();
        representation.mapPut("self", LinkReprBuilder.newBuilder(getResourceContext(), "self", "domainTypes/%s/actions/%s/params/%s", domainType, actionId, paramName).render());
        
        return responseOfOk(RepresentationType.TYPE_COLLECTION, Caching.ONE_DAY, representation).build();
    }

}