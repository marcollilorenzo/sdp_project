package smartcity;
import com.example.taxis.GrcpGrpc;
import com.example.taxis.GrcpOuterClass;
import io.grpc.stub.StreamObserver;

public class GrcpImpl extends GrcpGrpc.GrcpImplBase {
    @Override
    public void welcome(GrcpOuterClass.WelcomeRequest request, StreamObserver<GrcpOuterClass.WelcomeResponse> responseObserver) {
        System.out.println("New TAXI in City with ID: " + request.getId());
    }
}
