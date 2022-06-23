package smartcity;
import com.example.taxis.GrcpGrpc;
import com.example.taxis.GrcpOuterClass;
import io.grpc.stub.StreamObserver;
import models.Coordinate;
import models.Taxi;

public class GrcpImpl extends GrcpGrpc.GrcpImplBase {
    @Override
    public void welcome(GrcpOuterClass.WelcomeRequest request, StreamObserver<GrcpOuterClass.WelcomeResponse> responseObserver) {
        int x = request.getX();
        int y = request.getY();
        Coordinate taxiCoordinate = new Coordinate(x, y);

        TaxisSingleton.getInstance().addTaxi(
                new Taxi(
                        request.getId(),
                        request.getPort(),
                        request.getAddress(),
                        request.getBattery(),
                        taxiCoordinate
                )
        );

        System.out.println("Added taxi with id: " + request.getId());

        GrcpOuterClass.WelcomeResponse response = GrcpOuterClass.WelcomeResponse
                .newBuilder()
                .setId(
                        request.getId()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void election(GrcpOuterClass.ElectionRequest request, StreamObserver<GrcpOuterClass.ElectionResponse> responseObserver) {
        System.out.println("Nuova elezione dal taxi: " + request.getTaxiId() +  " per la corsa: " + request.getRideId());



        // TODO: rispondere con OK o NO in base ai parametri

        /*
            CRITERI IN ORDINE DI RILEVANZA:

            1. Se il taxi è giù in un'altra corsa va scartata
            2. Il taxi deve avere la distanza minima dal punto di partenza della corsa
            3. Il taxi deve avere il più alto livello di batteria
            4. Il taxi deve avere ID più grande

         */


        GrcpOuterClass.ElectionResponse response = GrcpOuterClass.ElectionResponse
                .newBuilder()
                .setRideId(request.getRideId())
                .setTaxiId(request.getTaxiId())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
