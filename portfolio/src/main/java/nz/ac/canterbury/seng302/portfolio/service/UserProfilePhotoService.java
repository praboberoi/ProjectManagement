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



    // return a status update when the file upload is complete
    // uses proto in file_upload.proto
//    private static class FileUploadObserver implements StreamObserver<FileUploadStatusResponse>{
//        @Override
//        public void onNext(FileUploadStatusResponse fileUploadStatusResponse) {
//            System.out.println("File upload status" + fileUploadStatusResponse.getStatus());
//        }
//
//        @Override
//        public void onError(Throwable throwable){
//
//        }
//
//        @Override
//        public void onCompleted(){
//
//        }
//    }



}
