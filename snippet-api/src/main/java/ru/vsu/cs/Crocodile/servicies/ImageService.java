package ru.vsu.cs.Crocodile.servicies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.vsu.cs.Crocodile.CustomException.ImageFieldIsNotInCorrectFormatException;
import ru.vsu.cs.Crocodile.CustomException.MultiPartDataEmptyException;
import ru.vsu.cs.Crocodile.CustomException.NoRequiredFieldException;
import ru.vsu.cs.Crocodile.entities.BlobEntity;
import ru.vsu.cs.Crocodile.entities.User;

import java.util.Base64;
import java.util.Objects;

@Component
public class ImageService {

    private Logger LOG = LoggerFactory.getLogger(ImageService.class);

    public Mono<FormFieldPart> getImageRepFromMultiForm(Mono<MultiValueMap<String, Part>> multiValueMapMono) {
        return multiValueMapMono
            .switchIfEmpty(Mono.error(new MultiPartDataEmptyException()))
            .map(MultiValueMap::toSingleValueMap)
            .filter(x -> x.containsKey("image"))
            .map(x -> x.get("image"))
            .switchIfEmpty(Mono.error(new NoRequiredFieldException()))
            .cast(FormFieldPart.class);
    }

    public Mono<Tuple2<BlobEntity, byte[]>> handleSource(String source, User user) {
        return Mono.just(source)
            .map(this::convertSourceToBlobEntity)
            .map(blobEntity -> {
                    blobEntity.setNameObject(generateImageName(user));
                    return blobEntity;
                }
            )
            .zipWith(Mono.just(source)
                .map(this::convertSourceToContentValue));
    }

    public Mono<String> handleFormFieldPart(FormFieldPart formFieldPart) {
        String regex = "^data:image/(png|jpg|jpeg);base64,[a-zA-Z0-9+/=]+";
        return Mono.just(formFieldPart)
            .map(FormFieldPart::value)
            .filter(src -> !src.isEmpty() && src.matches(regex))
            .switchIfEmpty(Mono.error(new ImageFieldIsNotInCorrectFormatException()))
            ;
    }

    private String generateImageName(User user) {
        return Objects.isNull(user.getImagePath()) ? user.getId() + "_1" :
            user.getId() + "_" + ( Integer.parseInt(user.getImagePath().substring(user.getImagePath().indexOf("_") + 1)) + 1);
    }

    private byte[] convertSourceToContentValue(String source) {
        String encodingBase64 = source.substring(source.indexOf(",") + 1);
        return Base64.getDecoder().decode(encodingBase64);
    }

    private BlobEntity convertSourceToBlobEntity(String source) {
        String bucketName = "snippet-images";
        int startTypeImage = source.indexOf(":");
        int endTypeImage = source.indexOf(";");
        String fileType = source.substring(startTypeImage + 1, endTypeImage);
        BlobEntity blobEntity = new BlobEntity();
        blobEntity.setNameBucket(bucketName);
        blobEntity.setTypeFile(fileType);
        return blobEntity;
    }
}