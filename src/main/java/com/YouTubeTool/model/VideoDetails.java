package com.YouTubeTool.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDetails {
    private String id;
    private String title;
    private String description;
    private List<String> tags;
    private String thumbnailUrl;
    private String channelTitle;
    private String publishedAt;
}
