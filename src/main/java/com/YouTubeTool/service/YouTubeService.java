package com.YouTubeTool.service;

import com.YouTubeTool.model.SearchVideo;
import com.YouTubeTool.model.VideoDetails;
import com.YouTubeTool.model.Video;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YouTubeService {
    private final WebClient.Builder webClientBuilder;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.base.url}")
    private String baseUrl;

    @Value("${youtube.api.max.related.videos}")
    private int maxRelatedVideos;

    // ================================
    // @Cacheable explained:
    // value = which cache to store in ("videoSearch")
    // key   = unique identifier for this cache entry
    //         here we use the videoTitle as key
    //         e.g. key="MrBeast" → stores SearchVideo result
    //
    // First call:  searchVideos("MrBeast")
    //              → Redis miss → calls YouTube API → saves to Redis
    // Second call: searchVideos("MrBeast")
    //              → Redis hit → returns from Redis instantly ✅
    //              → NO YouTube API call made!
    // ================================
    @Cacheable(value = "videoSearch", key = "#videoTitle")
    public SearchVideo searchVideos(String videoTitle){
        List<String> videoIds = searchForVideoIds(videoTitle);
        if (videoIds.isEmpty()){
            return SearchVideo.builder()
                    .primaryVideo(null)
                    .relatedVideos(Collections.emptyList())
                    .build();
        }
        String primaryVideoId = videoIds.get(0);
        List<String> relatedVideoIds = videoIds.subList(
                1, Math.min(videoIds.size(), maxRelatedVideos + 1));

        Video primaryVideo = getVideoById(primaryVideoId);
        List<Video> relatedVideos = new ArrayList<>();
        for (String id : relatedVideoIds) {
            Video video = getVideoById(id);
            if (video != null) {
                relatedVideos.add(video);
            }
        }
        return SearchVideo.builder()
                .primaryVideo(primaryVideo)
                .relatedVideos(relatedVideos)
                .build();
    }

    private List<String> searchForVideoIds(String videoTitle) {
        SearchApiResponse response = webClientBuilder.baseUrl(baseUrl).build().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("part", "snippet")
                        .queryParam("q", videoTitle)
                        .queryParam("type", "video")
                        .queryParam("maxResult", maxRelatedVideos)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(SearchApiResponse.class)
                .block();
        if (response == null || response.items == null) {
            return Collections.emptyList();
        }
        List<String> videoIds = new ArrayList<>();
        for (SearchItem item : response.items) {
            videoIds.add(item.id.videoId);
        }
        return videoIds;
    }

    // ================================
    // Caches video details by videoId
    // e.g. key="dQw4w9WgXcQ" → stores VideoDetails
    // TTL = 6 hours (set in RedisConfig.java)
    // ================================
    @Cacheable(value = "videoDetails", key = "#videoId")
    public VideoDetails getVideoDetails(String videoId) {
        VideoApiResponse response = webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", "snippet")
                        .queryParam("id", videoId)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();
        if (response == null || response.items.isEmpty()) {
            return null;
        }
        Snippet snippet = response.items.get(0).snippet;
        String thumbnailUrl = snippet.thumbnails.getBestThumbnailsUrl();

        return VideoDetails.builder()
                .id(videoId)
                .title(snippet.description)
                .tags(snippet.tags == null ? Collections.emptyList() : snippet.tags)
                .thumbnailUrl(thumbnailUrl)
                .channelTitle(snippet.channelTitle)
                .publishedAt(snippet.publishedAt)
                .build();
    }

    // ================================
    // No @Cacheable here intentionally
    // getVideoById is a private helper
    // called inside searchVideos which
    // is already cached as a whole ✅
    // ================================
    private Video getVideoById(String videoId) {
        VideoApiResponse response = webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", "snippet")
                        .queryParam("id", videoId)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();
        if (response == null || response.items == null) {
            return null;
        }
        Snippet snippet = response.items.get(0).snippet;
        return Video.builder()
                .id(videoId)
                .channelTitle(snippet.channelTitle)
                .title(snippet.title)
                .tags(snippet.tags == null ? Collections.emptyList() : snippet.tags)
                .build();
    }

    @Data
    static class SearchApiResponse {
        List<SearchItem> items;
    }
    @Data
    static class SearchItem {
        Id id;
    }
    @Data
    static class Id {
        String videoId;
    }
    @Data
    static class VideoApiResponse {
        List<VideoItem> items;
    }
    @Data
    static class VideoItem {
        Snippet snippet;
    }
    @Data
    static class Snippet {
        String title;
        String description;
        String channelTitle;
        String publishedAt;
        List<String> tags;
        Thumbnails thumbnails;
    }
    @Data
    static class Thumbnails {
        Thumbnail maxres;
        Thumbnail high;
        Thumbnail medium;
        Thumbnail _default;

        String getBestThumbnailsUrl() {
            if (maxres != null) return maxres.url;
            if (high != null) return high.url;
            if (medium != null) return medium.url;
            return _default != null ? _default.url : "";
        }

        @Data
        static class Thumbnail {
            String url;
        }
    }
}