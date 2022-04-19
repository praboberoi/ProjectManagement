package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.compiler.PluginProtos;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserProfilePhotoService {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceStub userAccountStub;


//    StreamObserver<UploadUserProfilePhotoRequest> streamObserver = this.userAccountStub.uploadUserProfilePhoto(new FileUploadObserver());

//    public FileUploadStatusResponse getImage(
//            ProfilePhotoUploadMetadata MetaData,
//            final ByteString fileContent) throws StatusRuntimeException{
//        FileUploadStatusResponse response = userAccountStub.uploadUserProfilePhoto(UploadUserProfilePhotoRequest.newBuilder()
//                .setMetaData(MetaData)
//                .setFileContent(fileContent)
//                .build());
//        return response;
//    }

    //path of the image
//    Path path = Paths.get("/static/icons/user.png");

//     build metadata in UploadUserProfilePhotoRequest proto
//    public UploadUserProfilePhotoRequest getImage(
//            final ProfilePhotoUploadMetadata MetaData) throws StatusRuntimeException, IOException {
        public void UploadUserProfilePhotoRequest() throws IOException {


            StreamObserver<UploadUserProfilePhotoRequest> streamObserver = this.userAccountStub.uploadUserProfilePhoto(new FileUploadObserver());
            Path path = Paths.get("/static/icons/user.png");

//            ProfilePhotoUploadMetadata MetaData;

            UploadUserProfilePhotoRequest response = (UploadUserProfilePhotoRequest.newBuilder()
                .setMetaData(ProfilePhotoUploadMetadata.newBuilder()
                        .setUserId(1)
                        .setFileType("png").build())
                .build());


        streamObserver.onNext(response);


        // upload file
        InputStream inputStream = Files.newInputStream(path);
        byte[] bytes = new byte[4096];
        int size;
        while((size = inputStream.read(bytes)) > 0){
            UploadUserProfilePhotoRequest uploadImage = UploadUserProfilePhotoRequest.newBuilder()
                    .setFileContent(ByteString.copyFrom(bytes,0,size))
                    .build();
            streamObserver.onNext(uploadImage);
        }
        inputStream.close();
        streamObserver.onCompleted();

//    return response;
//        return response;
    }



    private static class FileUploadObserver implements StreamObserver<FileUploadStatusResponse>{
        @Override
        public void onNext(FileUploadStatusResponse fileUploadStatusResponse) {
            System.out.println("File upload status" + fileUploadStatusResponse.getStatus());
        }

        @Override
        public void onError(Throwable throwable){

        }

        @Override
        public void onCompleted(){

        }
    }



//    public StreamObserver<UploadUserProfilePhotoRequest> getImage(
//            final ProfilePhotoUploadMetadata MetaData,
//            final ByteString fileContent) throws StatusRuntimeException{
//        StreamObserver<UploadUserProfilePhotoRequest> response = userAccountStub.uploadUserProfilePhoto(UploadUserProfilePhotoRequest.newBuilder()
//                .setMetaData(MetaData)
//                .setFileContent(fileContent)
//                .build();
//        return response;
//    }


//
//    public FileUploadStatusResponse getImage(int id) throws StatusRuntimeException {
//        StreamObserver<UploadUserProfilePhotoRequest> response = userAccountStub.uploadUserProfilePhoto(UploadUserProfilePhotoRequest);
//        return response;
//    }

}
