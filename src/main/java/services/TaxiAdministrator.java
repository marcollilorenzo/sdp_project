package services;

import models.Coordinate;
import models.Taxi;
import models.Taxis;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

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
        System.out.println(response);

        if(response){

            // Choose position of the recharge station of a randomly chosen district
            int x = (int)Math.floor(Math.random()*(9-0+1)+0);
            int y = (int)Math.floor(Math.random()*(9-0+1)+0);
            Coordinate coordinate = new Coordinate(x,y);
            taxi.setCoordinate(coordinate);

            // Return to taxi the list of taxis already registered in the smart-city
            taxi.setAllTaxi(Taxis.getInstance().getTaxisList());
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
}
