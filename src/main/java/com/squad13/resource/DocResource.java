package com.squad13.resource;

import com.squad13.service.AggregatorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/api/doc")
public class DocResource {

    @Inject
    AggregatorService aggService;

    @GET
    @Path("/{id}")
    @Produces("application/pdf")
    public Response getPdf(@PathParam("id") String especId) {
        return Response.ok(aggService.generatePdf(especId))
                .header("Content-Disposition",
                        "inline; filename=especificacao-" + especId + ".pdf")
                .build();
    }
}
