package services;

import models.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/taxis")
public class TaxiAdministrator {

    private static final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Produces("application/json")
    public String helloServer(){
        return "{\"message\": \"Welcome to SETA Server\"}";
    }

    @Path("add")
    @POST
    @Produces("application/json")
    @Consumes({"application/json", "application/xml"})
    public Response addTaxi(Taxi taxi){

        Boolean response = Taxis.getInstance().add(taxi);

        if(response){
            return Response.ok(Taxis.getInstance().getTaxisList()).build();

        }else{
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "Error, exits just a taxi with this ID");
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(json).build();
        }
    }

    @Path("delete/{taxiId}")
    @DELETE
    @Produces("application/json")
    @Consumes({"application/json", "application/xml"})
    public Response deleteTaxi(@PathParam("taxiId") Integer taxiID){
        Boolean response = Taxis.getInstance().delete(taxiID);
        if(response){
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "Taxi with ID "  + taxiID + " deleted");
            return Response.ok(Response.Status.CREATED).entity(json).build();
        }else{
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "Error, not exits taxi with ID "+ taxiID);
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }
    }


    @Path("add/ride")
    @POST
    @Produces("application/json")
    @Consumes({"application/json", "application/xml"})
    public Response addRide(Ride ride){

        System.out.println("New Ride");

        Boolean response = RidesQueue.getInstance().add(ride);

        if(response){
            return Response.ok(Taxis.getInstance().getTaxisList()).build();

        }else{
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "Error, exits just a taxi with this ID");
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(json).build();
        }
    }
}
