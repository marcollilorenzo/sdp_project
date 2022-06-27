package smartcity;

import com.example.taxis.GrcpGrpc;
import com.example.taxis.GrcpOuterClass;
import io.grpc.stub.StreamObserver;
import models.Coordinate;
import models.Taxi;

import java.util.Date;

public class GrcpImpl extends GrcpGrpc.GrcpImplBase {

    public double getDistanceFromCoordinate(Coordinate start, Coordinate end) {


        return Math.sqrt(
                Math.pow(end.getX() - start.getX(), 2) +
                        Math.pow(end.getY() - start.getY(), 2));
    }


    // RPC

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
        System.out.println("Nuova elezione dal taxi: " + request.getTaxiId() + " per la corsa: " + request.getRideId());


        /*
            CRITERI IN ORDINE DI RILEVANZA:

            1. Se il taxi è giù in un'altra corsa va scartata
            2. Il taxi deve avere la distanza minima dal punto di partenza della corsa
            3. Il taxi deve avere il più alto livello di batteria
            4. Il taxi deve avere ID più grande
         */

        GrcpOuterClass.ElectionResponse response;

        System.out.println("DISTRETTO RICHIESTA: " + request.getDistrict());
        System.out.println("DISTRETTO DEL MIO TAXI: " + TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate().getDistrict());

        if (request.getDistrict() == TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate().getDistrict()) { // ok, è il mio distretto, INIZIO CONTROLLI

            System.out.println("ELEZIONE MIO DISTRETTO");

            if (!TaxisSingleton.getInstance().isRiding() && !TaxisSingleton.getInstance().isRecharging()) { // non sono impegnato in un'altra corsa o mi sto caricando

                System.out.println("POSSO PARTECIPARE ALL'ELEZIONE");

                Coordinate startPosition = new Coordinate(request.getStartX(), request.getStartY());
                Coordinate taxiPosition = TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate();

                //Coordinate positionOfTaxiRequest = TaxisSingleton.getInstance().getPositionByTaxiId(request.getTaxiId());

                double myDistance = getDistanceFromCoordinate(startPosition, taxiPosition);
                double taxiRequestDistance = request.getDistance();

                System.out.println("Distance Taxi request: " + taxiRequestDistance);
                System.out.println("Distance My Taxi:" + myDistance);


                if (myDistance == taxiRequestDistance) { // Se distanza è uguale vado avanti con i controlli

                    System.out.println("HO LA STESSA DISTANZA DEL TAXI: " + request.getTaxiId());

                    int myBattery = TaxisSingleton.getInstance().getCurrentTaxi().getBatteryLevel();
                    int taxiRequestBattery = request.getBattery();

                    if (myBattery == taxiRequestBattery) { // se livello batteria è uguale vado avanti con i controlli

                        System.out.println("HO LA STESSA BATTERIA DEL TAXI: " + request.getTaxiId());

                        if (TaxisSingleton.getInstance().getCurrentTaxi().getId() < request.getTaxiId()) { // ho ID più piccolo, invio OK
                            System.out.println("HO ID PIU' PICCOLO DEL TAXI: " + request.getTaxiId());
                            response = GrcpOuterClass.ElectionResponse
                                    .newBuilder()
                                    .setRideId(request.getRideId())
                                    .setTaxiId(request.getTaxiId())
                                    .setResult("OK")
                                    .build();
                        } else { // ho ID più grande, invio NO
                            System.out.println("HO ID PIU' GRANDE DEL TAXI: " + request.getTaxiId());
                            response = GrcpOuterClass.ElectionResponse
                                    .newBuilder()
                                    .setRideId(request.getRideId())
                                    .setTaxiId(request.getTaxiId())
                                    .setResult("NO")
                                    .build();
                        }

                    } else if (myBattery < taxiRequestBattery) { // Se la mia batteria è minore invio OK
                        System.out.println("HO BATTERIA MINORE DEL TAXI: " + request.getTaxiId());
                        response = GrcpOuterClass.ElectionResponse
                                .newBuilder()
                                .setRideId(request.getRideId())
                                .setTaxiId(request.getTaxiId())
                                .setResult("OK")
                                .build();
                    } else { // Se la mia batteria è maggiore invio NO
                        System.out.println("HO BATTERIA MAGGIORE DEL TAXI: " + request.getTaxiId());
                        response = GrcpOuterClass.ElectionResponse
                                .newBuilder()
                                .setRideId(request.getRideId())
                                .setTaxiId(request.getTaxiId())
                                .setResult("NO")
                                .build();
                    }

                } else if (myDistance < taxiRequestDistance) { // Se la mia distanza è minore invio NO
                    System.out.println("HO DISTANZA MINORE DEL TAXI: " + request.getTaxiId());
                    response = GrcpOuterClass.ElectionResponse
                            .newBuilder()
                            .setRideId(request.getRideId())
                            .setTaxiId(request.getTaxiId())
                            .setResult("NO")
                            .build();
                } else { // se la mia distanza è maggiore invio OK
                    System.out.println("HO DISTANZA MAGGIORE DEL TAXI: " + request.getTaxiId());
                    response = GrcpOuterClass.ElectionResponse
                            .newBuilder()
                            .setRideId(request.getRideId())
                            .setTaxiId(request.getTaxiId())
                            .setResult("OK")
                            .build();
                }

            } else { // sono impegnato in un'altra corsa
                System.out.println("STO EFFETTUANDO UN'ALTRA RIDE: " + request.getTaxiId());
                response = GrcpOuterClass.ElectionResponse
                        .newBuilder()
                        .setRideId(request.getRideId())
                        .setTaxiId(request.getTaxiId())
                        .setResult("OK")
                        .build();
            }
        } else { // non è il mio distretto ti invio OK

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

    @Override
    public void updateTaxiInfo(GrcpOuterClass.UpdateTaxiInfoRequest request, StreamObserver<GrcpOuterClass.UpdateTaxiInfoResponse> responseObserver) {
        int x = request.getX();
        int y = request.getY();
        Coordinate newTaxiCoordinate = new Coordinate(x, y);

        TaxisSingleton.getInstance().updateTaxiById(request.getId(), request.getBattery(), newTaxiCoordinate);

        GrcpOuterClass.UpdateTaxiInfoResponse response = GrcpOuterClass.UpdateTaxiInfoResponse
                .newBuilder()
                .setReply("OK")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void recharge(GrcpOuterClass.RechargeTaxiRequest request, StreamObserver<GrcpOuterClass.RechargeTaxiResponse> responseObserver) {

        // info
        long requestTimestamp = request.getTimestamp();
        Date date = new Date();
        long taxiTimestamp = date.getTime();

        GrcpOuterClass.RechargeTaxiResponse response;

        if (request.getDistrict() == TaxisSingleton.getInstance().getCurrentTaxi().getCoordinate().getDistrict()) { // ok, è il mio distretto, INIZIO CONTROLLI
            if (TaxisSingleton.getInstance().getRechargingStatus() == 0) { // non la uso
                response = GrcpOuterClass.RechargeTaxiResponse
                        .newBuilder()
                        .setReply("OK")
                        .build();
            } else { // voglio o la sto usando
                if (TaxisSingleton.getInstance().getRechargingStatus() == 1) { // la voglio ma non la sto usando

                    if (taxiTimestamp < requestTimestamp) { // ho il tempo minore quindi prima mi ricarico io

                        synchronized (TaxisSingleton.getInstance().getChargeBatteryLock()) {
                            try {
                                TaxisSingleton.getInstance().getChargeBatteryLock().wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        response = GrcpOuterClass.RechargeTaxiResponse
                                .newBuilder()
                                .setReply("OK")
                                .build();

                    } else { // hai il tempo migliore del mio, ti rispondo OK
                        response = GrcpOuterClass.RechargeTaxiResponse
                                .newBuilder()
                                .setReply("OK")
                                .build();
                    }

                } else { // la sto usando
                    synchronized (TaxisSingleton.getInstance().getChargeBatteryLock()) {
                        try {
                            TaxisSingleton.getInstance().getChargeBatteryLock().wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    response = GrcpOuterClass.RechargeTaxiResponse
                            .newBuilder()
                            .setReply("OK")
                            .build();
                }
            }

        } else { // non è il mio distretto, rispondo OK
            response = GrcpOuterClass.RechargeTaxiResponse
                    .newBuilder()
                    .setReply("OK")
                    .build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
