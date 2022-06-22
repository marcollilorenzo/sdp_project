package com.example.taxis;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.25.0)",
    comments = "Source: Grcp.proto")
public final class GrcpGrpc {

  private GrcpGrpc() {}

  public static final String SERVICE_NAME = "com.example.taxis.Grcp";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.taxis.GrcpOuterClass.WelcomeRequest,
      com.example.taxis.GrcpOuterClass.WelcomeResponse> getWelcomeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "welcome",
      requestType = com.example.taxis.GrcpOuterClass.WelcomeRequest.class,
      responseType = com.example.taxis.GrcpOuterClass.WelcomeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.taxis.GrcpOuterClass.WelcomeRequest,
      com.example.taxis.GrcpOuterClass.WelcomeResponse> getWelcomeMethod() {
    io.grpc.MethodDescriptor<com.example.taxis.GrcpOuterClass.WelcomeRequest, com.example.taxis.GrcpOuterClass.WelcomeResponse> getWelcomeMethod;
    if ((getWelcomeMethod = GrcpGrpc.getWelcomeMethod) == null) {
      synchronized (GrcpGrpc.class) {
        if ((getWelcomeMethod = GrcpGrpc.getWelcomeMethod) == null) {
          GrcpGrpc.getWelcomeMethod = getWelcomeMethod =
              io.grpc.MethodDescriptor.<com.example.taxis.GrcpOuterClass.WelcomeRequest, com.example.taxis.GrcpOuterClass.WelcomeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "welcome"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.taxis.GrcpOuterClass.WelcomeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.taxis.GrcpOuterClass.WelcomeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GrcpMethodDescriptorSupplier("welcome"))
              .build();
        }
      }
    }
    return getWelcomeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.taxis.GrcpOuterClass.ElectionRequest,
      com.example.taxis.GrcpOuterClass.ElectionResponse> getElectionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "election",
      requestType = com.example.taxis.GrcpOuterClass.ElectionRequest.class,
      responseType = com.example.taxis.GrcpOuterClass.ElectionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.taxis.GrcpOuterClass.ElectionRequest,
      com.example.taxis.GrcpOuterClass.ElectionResponse> getElectionMethod() {
    io.grpc.MethodDescriptor<com.example.taxis.GrcpOuterClass.ElectionRequest, com.example.taxis.GrcpOuterClass.ElectionResponse> getElectionMethod;
    if ((getElectionMethod = GrcpGrpc.getElectionMethod) == null) {
      synchronized (GrcpGrpc.class) {
        if ((getElectionMethod = GrcpGrpc.getElectionMethod) == null) {
          GrcpGrpc.getElectionMethod = getElectionMethod =
              io.grpc.MethodDescriptor.<com.example.taxis.GrcpOuterClass.ElectionRequest, com.example.taxis.GrcpOuterClass.ElectionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "election"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.taxis.GrcpOuterClass.ElectionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.taxis.GrcpOuterClass.ElectionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GrcpMethodDescriptorSupplier("election"))
              .build();
        }
      }
    }
    return getElectionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GrcpStub newStub(io.grpc.Channel channel) {
    return new GrcpStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GrcpBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GrcpBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GrcpFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GrcpFutureStub(channel);
  }

  /**
   */
  public static abstract class GrcpImplBase implements io.grpc.BindableService {

    /**
     */
    public void welcome(com.example.taxis.GrcpOuterClass.WelcomeRequest request,
        io.grpc.stub.StreamObserver<com.example.taxis.GrcpOuterClass.WelcomeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getWelcomeMethod(), responseObserver);
    }

    /**
     */
    public void election(com.example.taxis.GrcpOuterClass.ElectionRequest request,
        io.grpc.stub.StreamObserver<com.example.taxis.GrcpOuterClass.ElectionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getElectionMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getWelcomeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.example.taxis.GrcpOuterClass.WelcomeRequest,
                com.example.taxis.GrcpOuterClass.WelcomeResponse>(
                  this, METHODID_WELCOME)))
          .addMethod(
            getElectionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.example.taxis.GrcpOuterClass.ElectionRequest,
                com.example.taxis.GrcpOuterClass.ElectionResponse>(
                  this, METHODID_ELECTION)))
          .build();
    }
  }

  /**
   */
  public static final class GrcpStub extends io.grpc.stub.AbstractStub<GrcpStub> {
    private GrcpStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GrcpStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrcpStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GrcpStub(channel, callOptions);
    }

    /**
     */
    public void welcome(com.example.taxis.GrcpOuterClass.WelcomeRequest request,
        io.grpc.stub.StreamObserver<com.example.taxis.GrcpOuterClass.WelcomeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getWelcomeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void election(com.example.taxis.GrcpOuterClass.ElectionRequest request,
        io.grpc.stub.StreamObserver<com.example.taxis.GrcpOuterClass.ElectionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getElectionMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GrcpBlockingStub extends io.grpc.stub.AbstractStub<GrcpBlockingStub> {
    private GrcpBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GrcpBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrcpBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GrcpBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.example.taxis.GrcpOuterClass.WelcomeResponse welcome(com.example.taxis.GrcpOuterClass.WelcomeRequest request) {
      return blockingUnaryCall(
          getChannel(), getWelcomeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.taxis.GrcpOuterClass.ElectionResponse election(com.example.taxis.GrcpOuterClass.ElectionRequest request) {
      return blockingUnaryCall(
          getChannel(), getElectionMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GrcpFutureStub extends io.grpc.stub.AbstractStub<GrcpFutureStub> {
    private GrcpFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GrcpFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GrcpFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GrcpFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.taxis.GrcpOuterClass.WelcomeResponse> welcome(
        com.example.taxis.GrcpOuterClass.WelcomeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getWelcomeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.taxis.GrcpOuterClass.ElectionResponse> election(
        com.example.taxis.GrcpOuterClass.ElectionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getElectionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_WELCOME = 0;
  private static final int METHODID_ELECTION = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GrcpImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GrcpImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_WELCOME:
          serviceImpl.welcome((com.example.taxis.GrcpOuterClass.WelcomeRequest) request,
              (io.grpc.stub.StreamObserver<com.example.taxis.GrcpOuterClass.WelcomeResponse>) responseObserver);
          break;
        case METHODID_ELECTION:
          serviceImpl.election((com.example.taxis.GrcpOuterClass.ElectionRequest) request,
              (io.grpc.stub.StreamObserver<com.example.taxis.GrcpOuterClass.ElectionResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class GrcpBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GrcpBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.taxis.GrcpOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Grcp");
    }
  }

  private static final class GrcpFileDescriptorSupplier
      extends GrcpBaseDescriptorSupplier {
    GrcpFileDescriptorSupplier() {}
  }

  private static final class GrcpMethodDescriptorSupplier
      extends GrcpBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GrcpMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GrcpGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GrcpFileDescriptorSupplier())
              .addMethod(getWelcomeMethod())
              .addMethod(getElectionMethod())
              .build();
        }
      }
    }
    return result;
  }
}
