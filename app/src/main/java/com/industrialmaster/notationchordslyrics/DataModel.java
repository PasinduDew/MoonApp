package com.industrialmaster.notationchordslyrics;

public class DataModel {

    private String songName, artistName, contributor, imgURL;
    private int likes, dislikes;

    public String getImgURL(){
        return imgURL;
    }



    public void setImgURL(String imgURL){
        this.imgURL = imgURL;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public void setLikes(int likes){
        this.likes = likes;
    }

    public int getLikes(){
        return likes;
    }

    public void setDislikes(int likes){
        this.dislikes= likes;
    }

    public int getDislikes(){
        return dislikes;
    }
}
