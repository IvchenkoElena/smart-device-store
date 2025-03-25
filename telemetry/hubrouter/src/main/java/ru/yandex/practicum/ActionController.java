package ru.yandex.practicum;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@GrpcService
@Slf4j
public class ActionController extends HubRouterControllerGrpc.HubRouterControllerImplBase {

    @Override
    public void handleDeviceAction(DeviceActionRequest request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received action: sensor ID: \"{}\" set value = {}, hub ID: \"{}\", scenario: \"{}\"",
                    request.getAction().getSensorId(), request.getAction().getValue(),
                    request.getHubId(), request.getScenarioName());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}