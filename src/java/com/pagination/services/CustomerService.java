package com.pagination.services;

import com.pagination.dao.CustomerDAO;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

/**
 *
 * @author Michael.Orokola
 *
 */
@Stateless
@Path("customers")
public class CustomerService {

    @Context
    UriInfo info;
    @Inject
    CustomerDAO customerFacade;

    @GET
    @Path("{startingFrom}/{pageSize}/{order}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers(@PathParam("startingFrom") int startingFrom,
            @PathParam("pageSize") int pageSize, @PathParam("order") String order) {

        try {
            JSONObject response = new JSONObject();
            int totalRecord = customerFacade.count();
            JSONArray customers = customerFacade.getCustomerJson(startingFrom, pageSize, order);
            response.put("customers", customers);
            response.put("totalRecord", totalRecord);
            response.put("pageSize", pageSize);
            double pages = (double) totalRecord / (double) pageSize;
            double pageOn = (double) (pageSize + startingFrom) / (double) pageSize;
            response.put("noOfPages", Math.ceil(pages));
            response.put("pageRatio", (int)Math.floor(pageOn) + "/" + (int)Math.ceil(pages));
            String baseUri = info.getBaseUri().toString();
            baseUri = baseUri + "customers/";
            if (totalRecord > (startingFrom + pageSize - 1)) {
                response.put("nextPage", baseUri + (startingFrom + pageSize) + "/"
                        + pageSize + "/" + order);
            } else {
                response.put("nextPage", "nil");
            }
            response.put("currentPage", info.getAbsolutePath().toString());
            if (startingFrom <= 1) {
                response.put("prevPage", "nil");
            } else if (startingFrom - pageSize <= 0) {
                response.put("prevPage", baseUri + "1/" + pageSize + "/" + order);
            } else {
                response.put("prevPage", baseUri + (startingFrom - pageSize) + "/"
                        + pageSize + "/" + order);
            }

            return Response.ok(response.toString()).build();

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
