In gRPC Java, there are three primary types of client stubs used to interact with a service. 
1. Synchronous (Blocking) Stub
The BlockingStub is used for synchronous calls where the client thread waits (blocks) until the server returns a response or throws an exception. It is easiest to implement for simple request-response (unary) logic but is not recommended for streaming or high-concurrency needs. 
Example Usage:
java
// Created using ServiceNameGrpc.newBlockingStub(channel)
HelloRequest request = HelloRequest.newBuilder().setName("World").build();
HelloResponse response = blockingStub.sayHello(request); 
System.out.println(response.getMessage());
Use code with caution.

 
2. Asynchronous (Non-Blocking) Stub 
The Stub (regular asynchronous stub) uses a callback-based model via the StreamObserver interface. It returns immediately, allowing the client to continue other work while the response is handled in a separate thread. This is required for client-side or bidirectional streaming. 
Example Usage:
java
// Created using ServiceNameGrpc.newStub(channel)
asyncStub.sayHello(request, new StreamObserver<HelloResponse>() {
    @Override
    public void onNext(HelloResponse value) {
        System.out.println("Received: " + value.getMessage());
    }
    @Override
    public void onError(Throwable t) { /* handle error */ }
    @Override
    public void onCompleted() { /* handle completion */ }
});
Use code with caution.

3. ListenableFuture Stub
The FutureStub is a hybrid that returns a ListenableFuture (from the Google Guava library). It allows you to use non-blocking callbacks or block if necessary, and it is highly efficient for managing multiple concurrent RPCs without blocking threads. 
Example Usage:
java
// Created using ServiceNameGrpc.newFutureStub(channel)
ListenableFuture<HelloResponse> future = futureStub.sayHello(request);
Futures.addCallback(future, new FutureCallback<HelloResponse>() {
    @Override
    public void onSuccess(HelloResponse result) {
        System.out.println("Success: " + result.getMessage());
    }
    @Override
    public void onFailure(Throwable t) { /* handle failure */ }
}, MoreExecutors.directExecutor());
