package com.example.myapplication3.fragment;
public class UploadWithKey {
    private String key;
    private Upload upload;

    public UploadWithKey() {
        // Required empty public constructor
    }

    public UploadWithKey(String key, Upload upload) {
        this.key = key;
        this.upload = upload;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }
}

