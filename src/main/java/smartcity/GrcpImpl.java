package smartcity;
import com.example.taxis.GrcpGrpc;
import com.example.taxis.GrcpOuterClass;
import io.grpc.stub.StreamObserver;
import models.Coordinate;
import models.Taxi;

public class GrcpImpl extends GrcpGrpc.GrcpImplBase {

    public double getDistanceFromCoordinate(Coordinate start, Coordinate end){
        int rooting = (
                (end.getX() - start.getX())^2 + (end.getY() - start.getY())^2
        );

        double distance = Math.sqrt(rooting);
        return distance;
    }



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

        GrcpOuterClass.ElectionResponse response;

        if(request.getDistrict() == TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate().getDistrict()) { // ok, è il mio distretto, INIZIO CONTROLLI


            if (!TaxisSingleton.getInstance().getCurrentTaxi().isRiding() && !TaxisSingleton.getInstance().getCurrentTaxi().isRecharging()) { // non sono impegnato in un'altra corsa o mi sto caricando

                Coordinate startPosition = new Coordinate(request.getStartX(), request.getStartY());
                Coordinate taxiPosition = TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate();

                Coordinate positionOfTaxiRequest = TaxisSingleton.getInstance().getPositionByTaxiId(request.getTaxiId());

                double myDistance = getDistanceFromCoordinate(startPosition, taxiPosition);
                double taxiRequestDistance = getDistanceFromCoordinate(startPosition, positionOfTaxiRequest);


                if (myDistance == taxiRequestDistance) { // Se distanza è uguale vado avanti con i controlli

                    int myBattery = TaxisSingleton.getInstance().getCurrentTaxi().getBatteryLevel();
                    int taxiRequestBattery = request.getBattery();

                    if (myBattery == taxiRequestBattery) { // se livello batteria è uguale vado avanti con i controlli

                        if (TaxisSingleton.getInstance().getCurrentTaxi().getId() < request.getTaxiId()) { // ho ID più piccolo, invio NO
                            response = GrcpOuterClass.ElectionResponse
                                    .newBuilder()
                                    .setRideId(request.getRideId())
                                    .setTaxiId(request.getTaxiId())
                                    .setResult("NO")
                                    .build();
                        } else { // ho ID più grande, invio OK
                            response = GrcpOuterClass.ElectionResponse
                                    .newBuilder()
                                    .setRideId(request.getRideId())
                                    .setTaxiId(request.getTaxiId())
                                    .setResult("Ok")
                                    .build();
                        }

                    } else if (myBattery < taxiRequestBattery) { // Se la mia batteria è minore invio NO
                        response = GrcpOuterClass.ElectionResponse
                                .newBuilder()
                                .setRideId(request.getRideId())
                                .setTaxiId(request.getTaxiId())
                                .setResult("NO")
                                .build();
                    } else { // Se la mia batteria è maggiore invio OK
                        response = GrcpOuterClass.ElectionResponse
                                .newBuilder()
                                .setRideId(request.getRideId())
                                .setTaxiId(request.getTaxiId())
                                .setResult("OK")
                                .build();
                    }

                } else if (myDistance < taxiRequestDistance) { // Se la mia distanza è minore invio NO
                    response = GrcpOuterClass.ElectionResponse
                            .newBuilder()
                            .setRideId(request.getRideId())
                            .setTaxiId(request.getTaxiId())
                            .setResult("NO")
                            .build();
                } else { // se la mia distanza è maggiore invio OK
                    response = GrcpOuterClass.ElectionResponse
                            .newBuilder()
                            .setRideId(request.getRideId())
                            .setTaxiId(request.getTaxiId())
                            .setResult("OK")
                            .build();
                }

            } else { // sono impegnato in un'altra corsa
                response = GrcpOuterClass.ElectionResponse
                        .newBuilder()
                        .setRideId(request.getRideId())
                        .setTaxiId(request.getTaxiId())
                        .setResult("OK")
                        .build();
            }
        }else{ // non è il mio distretto ti invio OK
            response = GrcpOuterClass.ElectionResponse
                    .newBuilder()
                    .setRideId(request.getRideId())
                    .setTaxiId(request.getTaxiId())
                    .setResult("OK")
                    .build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
