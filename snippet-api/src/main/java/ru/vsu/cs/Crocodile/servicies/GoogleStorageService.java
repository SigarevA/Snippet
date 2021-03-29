package ru.vsu.cs.Crocodile.servicies;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.vsu.cs.Crocodile.entities.BlobEntity;

@Component
public class GoogleStorageService {

    private Mono<Storage> storageMono;

    @Autowired
    public GoogleStorageService(Mono<Storage> storageMono) {
        this.storageMono = storageMono;
    }

    public Mono<Blob> createBlob(BlobEntity blobEntity, byte[] content) {
        BlobId blobId = BlobId.of(blobEntity.getNameBucket(), blobEntity.getNameObject());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(blobEntity.getTypeFile())
            .build();
        return storageMono.map(storage -> storage.create(blobInfo, content));
    }
}