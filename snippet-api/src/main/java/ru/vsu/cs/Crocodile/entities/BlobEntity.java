package ru.vsu.cs.Crocodile.entities;

public class BlobEntity {

    private String typeFile;
    private String nameObject;
    private String nameBucket;

    public BlobEntity() {
    }

    public BlobEntity(String typeFile, String nameObject, String nameBucket) {
        this.typeFile = typeFile;
        this.nameObject = nameObject;
        this.nameBucket = nameBucket;
    }

    public String getTypeFile() {
        return typeFile;
    }

    public void setTypeFile(String typeFile) {
        this.typeFile = typeFile;
    }

    public String getNameObject() {
        return nameObject;
    }

    public void setNameObject(String nameObject) {
        this.nameObject = nameObject;
    }

    public String getNameBucket() {
        return nameBucket;
    }

    public void setNameBucket(String nameBucket) {
        this.nameBucket = nameBucket;
    }

    @Override
    public String toString() {
        return "BlobEntity{" +
            "typeFile='" + typeFile + '\'' +
            ", nameObject='" + nameObject + '\'' +
            ", nameBucket='" + nameBucket + '\'' +
            '}';
    }
}