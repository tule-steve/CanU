package com.canu.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    public void listFile() {
        /*AmazonS3EncryptionV2 s3Encryption = AmazonS3EncryptionClientV2Builder.standard()
                                                                             .withRegion(Regions.US_WEST_2)
                                                                             .withCryptoConfiguration(new CryptoConfigurationV2().withCryptoMode((CryptoMode.StrictAuthenticatedEncryption)))
                                                                             .withEncryptionMaterialsProvider(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
                                                                             .build();*/

    }
}
