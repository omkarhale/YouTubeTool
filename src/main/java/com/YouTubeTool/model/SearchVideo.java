package com.YouTubeTool.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchVideo implements Serializable {  // ← Added Serializable

    private static final long serialVersionUID = 1L;

    private Video primaryVideo;
    private List<Video> relatedVideos;

    public Video getPrimaryVideo() {
        return primaryVideo;
    }

    public void setPrimaryVideo(Video primaryVideo) {
        this.primaryVideo = primaryVideo;
    }

    public List<Video> getRelatedVideos() {
        return relatedVideos;
    }

    public void setRelatedVideos(List<Video> relatedVideos) {
        this.relatedVideos = relatedVideos;
    }
}