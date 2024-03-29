package com.example.myapplication3.fragment;

public class Upload {
    private String imageName;
    private String imageUrl;
    private  String imageDescription;

    public Upload()
    {
    }
    public Upload(String imageName, String imageUrl, String Descrption)
    {
        this.imageName=imageName;
        this.imageUrl=imageUrl;
        this.imageDescription=Descrption;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageDescrption() {
        return imageDescription;
    }

    public void setImageDescrption(String imageDescrption) {
        this.imageDescription = imageDescrption;
    }
}
