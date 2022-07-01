package services;

import com.google.gson.Gson;
import models.Statistic;
import models.Statistics;
import models.Taxi;
import models.Taxis;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/stat")
public class StatAdministrator {
    private static final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Produces("application/json")
    public String helloServer(){
        return "{\"message\": \"Welcome to SETA Server STATISTIC\"}";
    }


    @Path("add")
    @POST
    @Produces("application/json")
    @Consumes({"application/json", "application/xml"})
    public Response addStatistic(Statistic statistic){

        Statistics.getInstance().addStatistic(statistic);

        System.out.println("AGGIUNTA NUOVA STATISTICA");
        System.out.println(statistic.getTimestamp());

        ObjectNode json = mapper.createObjectNode();
        json.put("message", "Added statistic");

        return Response.ok().build();
    }

    @GET
    @Path("taxi/{taxiId}/last/{n}")
    @Produces("application/json")
    public Response getLastStatisticsByTaxiId(@PathParam("taxiId") int taxiID,@PathParam("n") int n){

        if(Statistics.getInstance().getSizeLastStatisticsByTaxiId(taxiID) < n){
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "There aren't "+ n + " statistics");
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        Gson gson = new Gson();
        ObjectNode stat = Statistics.getInstance().getLastStatisticsByTaxiId(taxiID,n);
        System.out.println(gson.toJson(stat));
        return Response.status(Response.Status.OK)
                .header("Access-Control-Allow-Origin", "*")
                .entity(stat)
                .build();
    }

    @GET
    @Path("time1/{time1}/time2/{time2}")
    @Produces("application/json")
    public Response getStatisticBeetweenTwoTimestamp(@PathParam("time1") long time1, @PathParam("time2") long time2){

        if(Statistics.getInstance().getStatisticsList().size() <= 0){
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "There aren't statistics");
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        Gson gson = new Gson();
        ObjectNode stat = Statistics.getInstance().getLastStatisticsBeetweenTimestamp(time1,time2);

        if (stat.size()<=0){
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "There aren't statistics");
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        System.out.println(gson.toJson(stat));
        return Response.status(Response.Status.OK)
                .header("Access-Control-Allow-Origin", "*")
                .entity(stat)
                .build();
    }
}
