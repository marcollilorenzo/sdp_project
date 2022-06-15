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

        GrcpOuterClass.WelcomeResponse response = GrcpOuterClass.WelcomeResponse
                .newBuilder()
                .setId(
                        request.getId()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
