package com.squad13.resource;

import com.squad13.service.AggregatorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

@Path("/api/doc")
public class DocResource {

    @Inject
    AggregatorService aggService;

    @GET
    @Path("/test")
    @Produces("text/plain")
    public Response test() {
        return Response.ok("Hi").build();
    }

    @GET
    @Path("/{id:[a-fA-F0-9]{24}}")
    @Produces("application/pdf")
    public Response getPdf(@PathParam("id") String especId) throws IOException {
        return Response.ok(aggService.generatePdf(especId))
                .header("Content-Disposition", "inline; filename=especificacao-" + especId + ".pdf")
                .build();
    }
}