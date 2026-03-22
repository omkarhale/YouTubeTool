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
public class Video implements Serializable {  // ← Added Serializable

    // This tells Java: "This object CAN be
    // converted to bytes and stored somewhere"
    // Redis needs this to save your object ✅
    private static final long serialVersionUID = 1L;

    private String id;
    private String channelTitle;
    private String title;
    private List<String> tags;
}