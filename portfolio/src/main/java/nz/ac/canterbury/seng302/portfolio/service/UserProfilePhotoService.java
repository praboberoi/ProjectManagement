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

/**
 * Client service used to communicate to the IDP application relating to uploading user profile photos
 */
@Service
public class UserProfilePhotoService {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceStub userAccountStub;

    /**
     * Sends an upload image request to the server
     * @param id Id of user (required)
     * @param ext Extension of the image the user has uploaded (required)
     * @param path Path of the image (required)
     * @throws IOException Failure to upload the image
     */
    public void uploadImage(final int id, final String ext, final Path path) throws IOException {
        // request observer from UserAccountServiceGrpc
        StreamObserver<UploadUserProfilePhotoRequest> streamObserver = this.userAccountStub.uploadUserProfilePhoto(new FileUploadObserver());

        // build metadata from proto in user_accounts.proto
        UploadUserProfilePhotoRequest metadata = (UploadUserProfilePhotoRequest.newBuilder()
            .setMetaData(ProfilePhotoUploadMetadata.newBuilder()
                    .setUserId(id)
                    .setFileType(ext).build())
            .build());

        streamObserver.onNext(metadata);


        // upload file in chunks and upload as a stream
        InputStream inputStream = Files.newInputStream(path);
        byte[] bytes = new byte[4096];
        int size;
        while((size = inputStream.read(bytes)) > 0){
            UploadUserProfilePhotoRequest uploadImage = UploadUserProfilePhotoRequest.newBuilder()
                    .setFileContent(ByteString.copyFrom(bytes,0,size))
                    .build();
            streamObserver.onNext(uploadImage);
        }
        // close stream
        inputStream.close();
        streamObserver.onCompleted();
}

    // return a status update when the file upload is complete
    // uses proto in file_upload.proto
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



}
