package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.model.User;
import nz.ac.canterbury.seng302.identityprovider.model.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.*;

import java.util.NoSuchElementException;

@GrpcService
public class UserProfilePhotoServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase{

    
    public StreamObserver<UploadUserProfilePhotoRequest> UploadUserProfilePhoto(StreamObserver<FileUploadStatusResponse> responseObserver){
        return new StreamObserver<UploadUserProfilePhotoRequest>() {
            //TODO Update Messages

            @Override
            public void onNext(UploadUserProfilePhotoRequest request) {
                //TODO Loop this over for the length of the stream (idk length)
                FileUploadStatusResponse response = FileUploadStatusResponse.newBuilder()
                        .setStatus(FileUploadStatus.IN_PROGRESS)
                        .setMessage("In Progress")
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                FileUploadStatusResponse response = FileUploadStatusResponse.newBuilder()
                        .setStatus(FileUploadStatus.FAILED)
                        .setMessage("Failed")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onError(new Throwable());
            }

            @Override
            public void onCompleted() {
                FileUploadStatusResponse response = FileUploadStatusResponse.newBuilder()
                        .setStatus(FileUploadStatus.FAILED)
                        .setMessage("Failed")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }
}
