package mServer.crawler.sender.orf.parser;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.mediathekview.mlib.tool.Log;
import java.lang.reflect.Type;
import java.util.Optional;
import mServer.crawler.sender.newsearch.Qualities;
import mServer.crawler.sender.orf.OrfVideoInfoDTO;

public class OrfVideoDetailDeserializer implements JsonDeserializer<Optional<OrfVideoInfoDTO>> {

  private static final String ELEMENT_PLAYLIST = "playlist";
  private static final String ELEMENT_VIDEOS = "videos";
  private static final String ELEMENT_SUBTITLES = "subtitles";
  private static final String ELEMENT_SOURCES = "sources";

  private static final String ATTRIBUTE_DELIVERY = "delivery";
  private static final String ATTRIBUTE_PROTOCOL = "protocol";
  private static final String ATTRIBUTE_QUALITY = "quality";
  private static final String ATTRIBUTE_SRC = "src";
  private static final String ATTRIBUTE_TYPE = "type";

  private static final String RELEVANT_DELIVERY = "progressive";
  private static final String RELEVANT_PROTOCOL = "http";
  private static final String RELEVANT_SUBTITLE_TYPE = "ttml";
  private static final String RELEVANT_VIDEO_TYPE = "video/mp4";

  @Override
  public Optional<OrfVideoInfoDTO> deserialize(JsonElement aJsonElement, Type aType, JsonDeserializationContext aContext) throws JsonParseException {

    final JsonObject jsonObject = aJsonElement.getAsJsonObject();
    if (jsonObject.has(ELEMENT_PLAYLIST)) {
      final JsonObject playlistObject = jsonObject.get(ELEMENT_PLAYLIST).getAsJsonObject();
      if (playlistObject.has(ELEMENT_VIDEOS)) {
        final JsonObject videoObject = playlistObject.get(ELEMENT_VIDEOS).getAsJsonArray().get(0).getAsJsonObject();

        return deserializeVideoObject(videoObject);
      }
    }

    return Optional.empty();
  }

  public Optional<OrfVideoInfoDTO> deserializeVideoObject(final JsonObject aVideoObject) {
    final OrfVideoInfoDTO dto = new OrfVideoInfoDTO();

    if (aVideoObject.has(ELEMENT_SOURCES)) {
      parseVideo(aVideoObject.get(ELEMENT_SOURCES), dto);
    }

    if (aVideoObject.has(ELEMENT_SUBTITLES)) {
      parseSubtitles(aVideoObject.get(ELEMENT_SUBTITLES), dto);
    }

    return Optional.of(dto);
  }

  private static void parseVideo(final JsonElement aVideoElement, final OrfVideoInfoDTO dto) {
    if (aVideoElement.isJsonArray()) {
      aVideoElement.getAsJsonArray().forEach(videoElement -> {
        final JsonObject videoObject = videoElement.getAsJsonObject();
        if (videoObject.has(ATTRIBUTE_PROTOCOL)
                && videoObject.has(ATTRIBUTE_QUALITY)
                && videoObject.has(ATTRIBUTE_SRC)
                && videoObject.has(ATTRIBUTE_TYPE)) {
          String type = videoObject.get(ATTRIBUTE_TYPE).getAsString();
          String protocol = videoObject.get(ATTRIBUTE_PROTOCOL).getAsString();
          String delivery = videoObject.get(ATTRIBUTE_DELIVERY).getAsString();

          if (type.equalsIgnoreCase(RELEVANT_VIDEO_TYPE)
                  && protocol.equalsIgnoreCase(RELEVANT_PROTOCOL)
                  && delivery.equalsIgnoreCase(RELEVANT_DELIVERY)) {
            String quality = videoObject.get(ATTRIBUTE_QUALITY).getAsString();
            String url = videoObject.get(ATTRIBUTE_SRC).getAsString();

            Optional<Qualities> resolution = getQuality(quality);
            if (resolution.isPresent()) {
              dto.put(resolution.get(), url);
            }
          }
        }
      });
    }
  }

  private static void parseSubtitles(final JsonElement aSubtitlesElement, final OrfVideoInfoDTO dto) {
    if (aSubtitlesElement.isJsonArray()) {
      aSubtitlesElement.getAsJsonArray().forEach(subtitleElement -> {
        final JsonObject subtitleObject = subtitleElement.getAsJsonObject();
        if (subtitleObject.has(ATTRIBUTE_SRC)
                && subtitleObject.has(ATTRIBUTE_TYPE)) {
          String type = subtitleObject.get(ATTRIBUTE_TYPE).getAsString();

          if (type.equalsIgnoreCase(RELEVANT_SUBTITLE_TYPE)) {
            String url = subtitleObject.get(ATTRIBUTE_SRC).getAsString();
            dto.setSubtitleUrl(url);
          }
        }
      });
    }
  }

  private static Optional<Qualities> getQuality(String aQuality) {
    switch (aQuality) {
      case "Q1A":
        return Optional.empty();
      case "Q4A":
        return Optional.of(Qualities.SMALL);
      case "Q6A":
        return Optional.of(Qualities.NORMAL);
      case "Q8C":
        return Optional.of(Qualities.HD);
      default:
        Log.sysLog("ORF: unknown quality: " + aQuality);
    }
    return Optional.empty();
  }
}
