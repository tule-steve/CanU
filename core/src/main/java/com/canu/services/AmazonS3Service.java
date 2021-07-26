package com.canu.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.identitymanagement.model.ListAttachedUserPoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListPoliciesRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetFederationTokenRequest;
import com.amazonaws.util.IOUtils;
import com.canu.security.config.ExtOAuth2ClientAuthenticationProcessingFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private static final Logger logger = LoggerFactory.getLogger(ExtOAuth2ClientAuthenticationProcessingFilter.class);

    private final AmazonS3 amazonS3;

    public void upload(Optional<Map<String, String>> optionalMetaData,
                       MultipartFile multipartFile,
                       String filePath) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.addUserMetadata("Content-Type", multipartFile.getContentType());
        objectMetadata.addUserMetadata("Content-Length", String.valueOf(multipartFile.getSize()));
        objectMetadata.addUserMetadata("Title", multipartFile.getName());
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        try {
            PutObjectRequest req = new PutObjectRequest("file-canu-app",
                                                        filePath,
                                                        multipartFile.getInputStream(),
                                                        objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);
            PutObjectResult result = amazonS3.putObject(req);
            logger.info("Updated file to S3", result);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file", e);
        }
    }

    public void upload(String fileType, String base64String, String uri) {
        byte[] decodeFile = Base64.getDecoder()
                                  .decode(base64String.getBytes(StandardCharsets.UTF_8));
        InputStream stream = new ByteArrayInputStream(decodeFile);
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.addUserMetadata("Content-Type", fileType);
        objectMetadata.addUserMetadata("Content-Length", String.valueOf(decodeFile.length));

        try {
            PutObjectRequest req = new PutObjectRequest("file-canu-app",
                                                        uri,
                                                        stream,
                                                        objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);
            PutObjectResult result = amazonS3.putObject(req);
            logger.info("Updated file to S3", result);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }

    public void listOut() {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials("AKIAS6XZBBIMWW4TDFPQ", "H9JPbu47VyyanmpTlSLjBnPT1DnSQik0YXCCWUbR");
        try {
            AmazonIdentityManagement client = AmazonIdentityManagementClientBuilder
                    .standard()
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();

            AmazonS3 amazonS31 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();

            AWSSecurityTokenService security = AWSSecurityTokenServiceClientBuilder
                    .standard()
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();


            ObjectListing objectListing = amazonS31.listObjects(new ListObjectsRequest().withBucketName("file-canu-app"));
            while(true) {
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    amazonS31.deleteObject("file-canu-app", objectSummary.getKey());
                }
                if (objectListing.isTruncated()) {
                    objectListing = amazonS31.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }
            security.getCallerIdentity(new GetCallerIdentityRequest());
            security.getSessionToken();
            security.assumeRole(new AssumeRoleRequest().withPolicy("S3FullAccess"));
            security.getSessionToken();
            security.getFederationToken(new GetFederationTokenRequest().withName("canu"));

            GetRoleRequest request = new GetRoleRequest().withRoleName("S3Access");
            client.listAttachedUserPolicies(new ListAttachedUserPoliciesRequest().withUserName("canu"));
            client.getRole(request);
            client.getUser();
            client.getAccountSummary();
            client.listPolicies(new ListPoliciesRequest().withPathPrefix("\\canu"));
            amazonS31.listObjectsV2(new ListObjectsV2Request().withBucketName("file-canu-app")
                                                              .withPrefix("documents/"));
            //            amazonS31.listObjects(new ListObjectsRequest().withBucketName("file-canu-app").withPrefix("/documents/"))
            //            amazonS31.getBucketPolicy(new GetBucketPolicyRequest().with)
            amazonS31.doesBucketExistV2("file-canu-app");
            amazonS31.doesBucketExistV2("canu");
            amazonS31.listBuckets();
            amazonS31.getS3AccountOwner();
            amazonS31.createBucket("file-canu-app");
            amazonS31.getBucketPolicy("file-canu-app");
            amazonS31.listObjects("file-canu-app");
            int i = 0;
            i++;
        } catch (Exception ex) {
            logger.error("error: " + ex.getMessage());
        }
        ////        List<Bucket> bucket = amazonS3.listBuckets();

        HeadBucketRequest headBucketRequest = new HeadBucketRequest("file-canu-app");
        amazonS3.headBucket(headBucketRequest);
    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = amazonS3.getObject(path, key);
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download the file", e);
        }
    }
}
